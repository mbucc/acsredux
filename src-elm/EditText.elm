module EditText exposing (main)

import Browser
import Browser.Dom as Dom
import Html exposing (Attribute, Html, button, div, p, text, textarea, br)
import Html.Attributes as Attr exposing (id)
import Html.Events exposing (onClick, onInput)
import Http exposing (Expect, Response, expectStringResponse)
import Markdown.Parser as Markdown
import Markdown.Renderer
import Maybe exposing (withDefault)
import Task
import Url exposing (percentEncode)



--
--              MAIN
--


main =
    Browser.element
        { init = init
        , update = update
        , subscriptions = subscriptions
        , view = view
        }



--
--              MODEL
--


type alias Model =
    { contentID : ContentID
    , markdown : String
    , state : ControlState
    , newMarkdown : String
    , saveErrorMessage : String
    }


type ControlState
    = Viewing
    | Editing
    | Saving
    | SaveError
    | InitError


type alias DiaryID =
    Int


type alias NoteID =
    Int


type alias DateString =
    String


type ContentID
    = NoteID Int
    | DiaryIdAndDay DiaryID DateString
    | Invalid



-- Flags come in as arguments to init.


init : ( Maybe Int, Maybe String, Maybe String ) -> ( Model, Cmd Msg )
init ( id, dateAsString, body ) =
    ( initModel ( id, dateAsString, body ), Cmd.none )


initModel : ( Maybe Int, Maybe String, Maybe String ) -> Model
initModel ( id, dateString, body ) =
    let
        cid =
            initContentID ( id, dateString )
    in
    { contentID = cid
    , markdown = withDefault "" body
    , state = initState cid
    , newMarkdown = ""
    , saveErrorMessage = ""
    }


initContentID : ( Maybe Int, Maybe String ) -> ContentID
initContentID ( id, date ) =
    case ( id, date ) of
        ( Just x, Nothing ) ->
            NoteID x

        ( Just x1, Just x2 ) ->
            DiaryIdAndDay x1 x2

        ( Nothing, Just _ ) ->
            Invalid

        ( Nothing, Nothing ) ->
            Invalid


initState : ContentID -> ControlState
initState id =
    if id == Invalid then
        InitError

    else
        Viewing



--
--              UPDATE
--


type Msg
    = Edit
    | SaveNote ContentID
    | Cancel
    | ChangeMarkdownText String
    | SaveRequest (Result MyHttpError String)
    | NoOp


type alias ValidationError =
    String


type MyHttpError
    = BadUrl String
    | Timeout
    | NetworkError
    | BadStatus Int (Maybe ValidationError)
    | BadBody String


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        SaveRequest (Ok _) ->
            ( { model | markdown = model.newMarkdown, state = Viewing }
            , Cmd.none
            )

        SaveRequest (Err err) ->
            let
                errMsg =
                    case err of
                        Timeout ->
                            tryAgain "the server took to long to respond."

                        NetworkError ->
                            tryAgain "there was some error in the network."

                        BadStatus x Nothing ->
                            displayBug ("E1: " ++ String.fromInt x)

                        BadStatus _ (Just validationError) ->
                            validationError

                        BadUrl x ->
                            displayBug ("E2: " ++ x)

                        BadBody x ->
                            displayBug ("E3: " ++ x)
            in
            ( { model | state = SaveError, saveErrorMessage = errMsg }
            , Cmd.none
            )

        Edit ->
            ( { model
                | state = Editing
                , newMarkdown = model.markdown
              }
            , Task.attempt
                (\_ -> NoOp)
                (Dom.focus ("textarea-" ++ elementID model.contentID))
            )

        Cancel ->
            ( { model | state = Viewing, newMarkdown = "" }, Cmd.none )

        ChangeMarkdownText x ->
            ( { model | newMarkdown = x }
            , Cmd.none
            )

        NoOp ->
            ( model, Cmd.none )

        SaveNote Invalid ->
            ( model, Cmd.none )

        -- We are updating an existing note.
        SaveNote (NoteID noteID) ->
            ( { model | state = Saving, saveErrorMessage = "" }
            , Http.request
                { method = "PUT"
                , headers = []
                , url = putContentTextURL noteID
                , body = Http.stringBody "text/plain" model.newMarkdown
                , expect = expectStringResponse SaveRequest parseResponse
                , timeout = Nothing
                , tracker = Nothing
                }
            )

        -- We are adding a new note for a month.
        SaveNote (DiaryIdAndDay diaryID dateString) ->
            ( { model | state = Saving, saveErrorMessage = "" }
            , Http.post
                { url = postURL diaryID
                , body =
                    Http.stringBody
                        "application/x-www-form-urlencoded"
                        ("entryDate="
                            ++ percentEncode dateString
                            ++ "&text="
                            ++ percentEncode model.newMarkdown
                        )
                , expect = expectStringResponse SaveRequest parseResponse
                }
            )


parseResponse :
    Response String
    -> Result MyHttpError String
parseResponse serverHTTPResponse =
    case serverHTTPResponse of
        Http.BadUrl_ url ->
            Err (BadUrl url)

        Http.Timeout_ ->
            Err Timeout

        Http.NetworkError_ ->
            Err NetworkError

        Http.BadStatus_ metadata body ->
            if metadata.statusCode == 400 then
                Err (BadStatus metadata.statusCode (Just body))

            else
                Err (BadStatus metadata.statusCode Nothing)

        Http.GoodStatus_ _ body ->
            Ok body


tryAgain : String -> String
tryAgain x =
    "Please try again, " ++ x


displayBug : String -> String
displayBug x =
    "Rats, you hit a bug!  "
        ++ "Please take a screen shot and send it to us so we can fix it ("
        ++ x
        ++ ")."


postURL : Int -> String
postURL diaryID =
    "/photo-diary/" ++ String.fromInt diaryID ++ "/notes"


putContentTextURL : Int -> String
putContentTextURL contentID =
    "/content/" ++ String.fromInt contentID ++ "/body"



--
--              SUBSCRIPTIONS
--


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.none



--
--              VIEW
--


view : Model -> Html Msg
view model =
    case model.state of
        InitError ->
            div (divAttrs model)
                [ p []
                    [ text
                        (displayBug ("E4: " ++ "No ID for this diary entry."))
                    ]
                ]

        Editing ->
            div (divAttrs model) (editBox model)

        Saving ->
            div (divAttrs model) (editBox model)

        SaveError ->
            div (divAttrs model)
                (editBox model ++ [ p [] [ text model.saveErrorMessage ] ])

        Viewing ->
            div
                (divAttrs model)
                [ p
                    []
                    [ case
                        model.markdown
                            |> Markdown.parse
                            |> Result.mapError deadEndsToString
                            |> Result.andThen
                                (\ast ->
                                    Markdown.Renderer.render
                                        Markdown.Renderer.defaultHtmlRenderer
                                        ast
                                )
                      of
                        Ok rendered ->
                            div [] rendered

                        Err errors ->
                            text errors
                    ]
                , editButton model
                ]


deadEndsToString deadEnds =
    deadEnds
        |> List.map Markdown.deadEndToString
        |> String.join "\n"


divAttrs : Model -> List (Attribute msg)
divAttrs model =
    [ id ("div-" ++ elementID model.contentID)
    , Attr.style "padding" "20px" ]


editButton : Model -> Html Msg
editButton model =
    button
        [ onClick Edit
        , id ("edit-" ++ elementID model.contentID)
        ]
        [ text
            (if String.isEmpty model.markdown then
                "Add a diary entry."

             else
                "Edit"
            )
        ]


editBox : Model -> List (Html Msg)
editBox model =
    [ textarea
        [ onInput ChangeMarkdownText
        , id ("textarea-" ++ elementID model.contentID)
        , Attr.style "width" "100%"
        , Attr.style "height" "300px"
        , Attr.style "font-size" "16px"
        ]
        [ text model.newMarkdown ]
    , br [] []
    , button
        [ onClick (SaveNote model.contentID)
        , id ("save-" ++ elementID model.contentID)
        ]
        [ text "save" ]
    , button
        [ onClick Cancel
        , id ("cancel-" ++ elementID model.contentID)
        ]
        [ text "cancel" ]
    ]


elementID : ContentID -> String
elementID x =
    case x of
        NoteID id ->
            String.fromInt id

        DiaryIdAndDay _ month ->
            String.trim month

        Invalid ->
            "invalid"
