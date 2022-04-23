module EditText exposing (main)

import Browser
import Html exposing (Html, button, div, p, text, textarea)
import Html.Events exposing (onClick, onInput)
import Http exposing (Expect, Response, expectStringResponse)
import Markdown.Option exposing (..)
import Markdown.Render exposing (MarkdownMsg(..), MarkdownOutput(..))



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
    { photoDiaryID : Int
    , markdown : String
    , isEditing : Bool
    , isSaving : Bool
    , newMarkdown : String
    , errorMessage : String
    }


initialModel : Int -> Model
initialModel id =
    { photoDiaryID = id
    , markdown = ""
    , isEditing = False
    , isSaving = False
    , newMarkdown = ""
    , errorMessage = ""
    }


init : Int -> ( Model, Cmd Msg )
init diaryID =
    ( initialModel diaryID, Cmd.none )



--
--              UPDATE
--


type Msg
    = Edit
    | Save
    | Cancel
    | Change String
    | SaveRequest (Result MyHttpError String)
    | MarkdownMsg Markdown.Render.MarkdownMsg


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
                , errorMessage = errMsg
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

        Save ->
            ( { model | isEditing = False, isSaving = True }
            , Http.post
                { url = postURL model.photoDiaryID
                , body = Http.stringBody "text/plain" model.newMarkdown
                , expect = expectStringResponse SaveRequest parseResponse
                }
            )

        Cancel ->
            ( clearFlags model
            , Cmd.none
            )

        MarkdownMsg _ ->
            ( model, Cmd.none )


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
        , errorMessage = ""
        , isSaving = False
    }


postURL : Int -> String
postURL diaryID =
    "/photo-diary/" ++ String.fromInt diaryID ++ "/notes"



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
    if model.isSaving then
        div [] (editBox model ++ [ text "Saving ..." ])

    else if model.isEditing then
        if String.isEmpty model.errorMessage then
            div [] (editBox model)

        else
            div [] (editBox model ++ [ p [] [ text model.errorMessage ] ])

    else if String.isEmpty model.markdown then
        div [] [ editButton ]

    else
        div
            []
            [ p
                []
                [ Markdown.Render.toHtml Extended model.markdown
                    |> Html.map MarkdownMsg
                ]
            , editButton
            ]


editButton : Html Msg
editButton =
    button
        [ onClick Edit ]
        [ text "edit summary text for the entire year" ]


editBox : Model -> List (Html Msg)
editBox model =
    [ textarea [ onInput Change ] [ text model.newMarkdown ]
    , button [ onClick Save ] [ text "save" ]
    , button [ onClick Cancel ] [ text "cancel" ]
    ]
