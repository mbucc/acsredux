module EditText exposing (main)

import Browser
import Html exposing (Html, button, div, p, text, textarea)
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
    { photoDiaryID : Int
    , content : String
    , isEditing : Bool
    , isSaving : Bool
    , newContent : String
    , errorMessage : String
    }


initialModel : Int -> Model
initialModel id =
    { photoDiaryID = id
    , content = ""
    , isEditing = False
    , isSaving = False
    , newContent = ""
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
    | SaveRequest (Result Http.Error String)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        SaveRequest (Ok _) ->
            ( clearFlags
                { model | content = model.newContent }
            , Cmd.none
            )

        SaveRequest (Err err) ->
            let
                errMsg =
                    case err of
                        Http.Timeout ->
                            tryAgain "the server took to long to respond."

                        Http.NetworkError ->
                            tryAgain "there was some error in the network."

                        Http.BadStatus x ->
                            displayBug ("E1: " ++ String.fromInt x)

                        Http.BadUrl x ->
                            displayBug ("E2: " ++ x)

                        Http.BadBody x ->
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
                , newContent = model.content
              }
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
                , url = postURL model.photoDiaryID
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
        , newContent = ""
        , errorMessage = ""
        , isSaving = False
    }


postURL : Int -> String
postURL diaryID =
    "/photo-diary/" ++ String.fromInt diaryID ++ "/add-note"



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

    else if String.isEmpty model.content then
        div [] [ editButton ]

    else
        div [] [ p [] [ text model.content ], editButton ]


editButton : Html Msg
editButton =
    button
        [ onClick Edit ]
        [ text "edit summary text for the entire year" ]


editBox : Model -> List (Html Msg)
editBox model =
    [ textarea [ onInput Change ] [ text model.newContent ]
    , button [ onClick Save ] [ text "save" ]
    , button [ onClick Cancel ] [ text "cancel" ]
    ]
