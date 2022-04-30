module EditText exposing (main)

import Browser
import Html exposing (Html, button, div, p, text, textarea)
import Html.Attributes exposing (id)
import Html.Events exposing (onClick, onInput)
import Http exposing (Expect, Response, expectStringResponse)
import Markdown.Parser as Markdown
import Markdown.Renderer
import Maybe exposing (withDefault)
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
    , isEditing : Bool
    , isSaving : Bool
    , newMarkdown : String
    , saveErrorMessage : String
    }


type ContentID
    = NoteID Int
    | DiaryIdAndDay Int String
    | Invalid


elementID : ContentID -> String
elementID x =
    case x of
        NoteID id ->
            String.fromInt id

        DiaryIdAndDay _ month ->
            String.trim month

        Invalid ->
            "invalid"


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



-- Flags come in as arguments to init.


init : ( Maybe Int, Maybe String, Maybe String ) -> ( Model, Cmd Msg )
init ( id, dateAsString, body ) =
    ( initialModel ( id, dateAsString, body ), Cmd.none )


initialModel : ( Maybe Int, Maybe String, Maybe String ) -> Model
initialModel ( id, dateString, body ) =
    { contentID = initContentID ( id, dateString )
    , markdown = withDefault "" body
    , isEditing = False
    , isSaving = False
    , newMarkdown = ""
    , saveErrorMessage = ""
    }



--
--              UPDATE
--


type Msg
    = Edit
    | SaveNote ContentID
    | Cancel
    | Change String
    | SaveRequest (Result MyHttpError String)


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
            ( clearFlags { model | markdown = model.newMarkdown }
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
            ( { model
                | isSaving = False
                , isEditing = True
                , saveErrorMessage = errMsg
              }
            , Cmd.none
            )

        Edit ->
            ( { model
                | isEditing = True
                , newMarkdown = model.markdown
              }
            , Cmd.none
            )

        Change x ->
            ( { model | newMarkdown = x }
            , Cmd.none
            )

        SaveNote Invalid ->
            ( model, Cmd.none )

        -- We are updating an existing note.
        SaveNote (NoteID noteID) ->
            ( { model | isEditing = False, isSaving = True }
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

        -- We are adding a new node.
        SaveNote (DiaryIdAndDay diaryID dateString) ->
            ( { model | isEditing = False, isSaving = True }
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

        Cancel ->
            ( clearFlags model
            , Cmd.none
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


clearFlags : Model -> Model
clearFlags model =
    { model
        | isEditing = False
        , newMarkdown = ""
        , saveErrorMessage = ""
        , isSaving = False
    }


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
    if model.contentID == Invalid then
        div [] [ p [] [ text (displayBug ("E4: " ++ "No ID for this diary entry.")) ] ]

    else if model.isEditing then
        if String.isEmpty model.saveErrorMessage then
            div [] (editBox model)

        else
            div [] (editBox model ++ [ p [] [ text model.saveErrorMessage ] ])

    else if String.isEmpty model.markdown then
        div [] [ editButton model ]

    else
        div
            [ id ("div-" ++ elementID model.contentID) ]
            [ p
                []
                [ case
                    model.markdown
                        |> Markdown.parse
                        |> Result.mapError deadEndsToString
                        |> Result.andThen (\ast -> Markdown.Renderer.render Markdown.Renderer.defaultHtmlRenderer ast)
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
        [ onInput Change
        , id ("textarea-" ++ elementID model.contentID)
        ]
        [ text model.newMarkdown ]
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
