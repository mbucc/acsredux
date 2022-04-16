module EditText exposing (main)

import Browser
import Html exposing (Html, button, div, p, text, textarea)
import Html.Attributes exposing (disabled)
import Html.Events exposing (onClick, onInput)
import Http



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
    { content : String
    , isEditing : Bool
    , newContent : String
    , isSaving : Bool
    , errorMessage : String
    }


initialModel : Model
initialModel =
    { content = ""
    , isEditing = False
    , newContent = ""
    , isSaving = False
    , errorMessage = ""
    }


init : () -> ( Model, Cmd Msg )
init _ =
    ( initialModel, Cmd.none )



--
--              UPDATE
--


type Msg
    = Edit
    | Save
    | Cancel
    | Change String
    | SaveRequest (Result Http.Error String)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        SaveRequest (Ok _) ->
            ( clearFlags { model | content = model.newContent }
            , Cmd.none
            )

        SaveRequest (Err err) ->
            let
                errMsg =
                    case err of
                        Http.Timeout ->
                            "Please try again, the server took to long to respond."

                        Http.NetworkError ->
                            "Please try again, there was some error in the network."

                        Http.BadStatus x ->
                            displayBug ("E1: " ++ String.fromInt x)

                        Http.BadUrl x ->
                            displayBug ("E2: " ++ x)

                        Http.BadBody x ->
                            displayBug ("E3: " ++ x)
            in
            ( { model | isSaving = False, isEditing = True, errorMessage = errMsg }
            , Cmd.none
            )

        Edit ->
            ( { model | isEditing = True, newContent = model.content }
            , Cmd.none
            )

        Change x ->
            ( { model | newContent = x }
            , Cmd.none
            )

        Save ->
            ( { model | isEditing = False, isSaving = True }
            , Http.request
                { method = "POST"
                , headers = []
                , url = "/dummyURL"
                , body = Http.stringBody "text/plain" model.newContent
                , expect = Http.expectString SaveRequest
                , timeout = Nothing
                , tracker = Nothing
                }
            )

        Cancel ->
            ( clearFlags model
            , Cmd.none
            )


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
        , newContent = ""
        , errorMessage = ""
        , isSaving = False
    }



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
        div []
            [ textarea [ disabled True ] [ text model.newContent ]
            , button [ disabled True ] [ text "save" ]
            , button [ disabled True ] [ text "cancel" ]
            , text "Saving ..."
            ]

    else if model.isEditing then
        if String.isEmpty model.errorMessage then
            div []
                [ textarea [ onInput Change ] [ text model.newContent ]
                , button [ onClick Save ] [ text "save" ]
                , button [ onClick Cancel ] [ text "cancel" ]
                ]

        else
            div []
                [ textarea [ onInput Change ] [ text model.newContent ]
                , button [ onClick Save ] [ text "save" ]
                , button [ onClick Cancel ] [ text "cancel" ]
                , p [] [ text model.errorMessage ]
                ]

    else if String.isEmpty model.content then
        div []
            [ button
                [ onClick Edit ]
                [ text "add summary text for the entire year" ]
            ]

    else
        div []
            [ p [] [ text model.content ]
            , button
                [ onClick Edit ]
                [ text "edit summary text for the entire year" ]
            ]
