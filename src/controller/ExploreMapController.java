
package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.characters.Character;
import model.worldCreation.Area;
import model.worldCreation.ControlledArea;

import java.util.List;

public class ExploreMapController extends BaseController {

    @FXML
    private ListView<Button> areasList;
    @FXML
    private Label currentViewLabel;
    @FXML
    private Label areaType;
    @FXML
    private Label containType;
    @FXML
    private Label higherType;
    @FXML
    private Button higherViewButton;
    @FXML
    private TextArea testi;
    private MainController main;
    @FXML
    private ListView<Character> livesHereListView;
    @FXML
    private Hyperlink authorityLink;

    private CharacterController characterController;

    void updateAuthorityLink(){
        if (model.accessCurrentView().getCurrentView() instanceof ControlledArea) {
            ControlledArea currentView = (ControlledArea) model.accessCurrentView().getCurrentView();
            authorityLink.setText(currentView.getAuthorityHere().toString());
        }
    }

    @FXML
    void authorityLinkClick(ActionEvent event) {
        if (model.accessCurrentView().getCurrentView() instanceof ControlledArea currentView) {
            openCharacterProfile(currentView.getAuthorityHere().getCharacterInThisPosition());
        }

    }


    public void updateLivesHereListView() {

        if (model.accessCurrentView().getCurrentView() instanceof ControlledArea currentView) {

            List<Character> list = currentView.getImportantCharacters();
            ObservableList<Character> characters = FXCollections.observableArrayList(list);
            livesHereListView.setItems(characters);

            livesHereListView.setCellFactory(lv -> new ListCell<Character>() {
                private final Hyperlink link = new Hyperlink();

                @Override
                protected void updateItem(Character character, boolean empty) {
                    super.updateItem(character, empty);
                    if (empty || character == null) {
                        setGraphic(null);
                    } else {
                        link.setText(character.toString());
                        link.setOnAction(e -> openCharacterProfile(character));
                        setGraphic(link);
                    }
                }
            });
        } else {
            livesHereListView.setItems(FXCollections.observableArrayList()); // Clears the list when it goes to continent
        }


    }

    private void openCharacterProfile(Character character) {
        characterController.setCurrentCharacter(character);
    }



    @FXML
    private void updateHigherType() {
        var currentView = model.accessCurrentView().getCurrentView();
        if (currentView != null && currentView.getHigher() != null) {
            higherType.setText(currentView.getHigher().getClass().getSimpleName());
        } else {
            higherType.setText("");
        }
        if (model.accessCurrentView().getCurrentView().getHigher().equals(model.accessCurrentView().getCurrentView())){
            higherType.setText("");
        }
    }

    @FXML
    private void updateContainType() {
        var currentView = model.accessCurrentView().getCurrentView();
        if (currentView != null && currentView.getContents() != null && !currentView.getContents().isEmpty()) {
            String className = currentView.getContents().get(0).getClass().getSimpleName();
            String pluralClassName = pluralizeClassName(className);
            containType.setText(pluralClassName);
        } else {
            containType.setText("");
        }
    }

    private String pluralizeClassName(String className) {
        return switch (className) {
            case "City" -> "Cities";
            case "Province" -> "Provinces";
            case "Nation" -> "Nations";
            case "Continent" -> "Continents";
            case "Quarter" -> "Districts";
            default -> className + "s";
        };
    }

    @FXML
    private void updateAreaType() {
        var currentView = model.accessCurrentView().getCurrentView();
        if (currentView != null) {
            areaType.setText(currentView.getClass().getSimpleName());
        } else {
            areaType.setText("");
        }
    }

    @FXML
    void getHigherView() {
        model.accessCurrentView().setCurrentView(model.accessCurrentView().getCurrentView().getHigher());
        updateExploreTab();
        checkAndDisableHigherViewButton(); // Check if the higher view button should be disabled
    }
    public void setMain(MainController main) {
        this.main = main;
    }


    public void updateExploreTab(){
        updateAreasList();
        updateCurrentViewLabel();
        updateHigherButtonText();
        updateAreaType();
        updateContainType();
        updateHigherType();
        updateTextArea();
        updateLivesHereListView();
        updateAuthorityLink();
    }


    void updateTextArea(){
        testi.setText(model.accessCurrentView().getCurrentView().getDetails());
    }

    void updateAreasList() {
        areasList.getItems().clear(); // Clear the list before adding new buttons
        for (Area area : model.accessCurrentView().getContents()) {
            Button button = new Button(area.toString());
            button.setOnAction(event -> {
                model.accessCurrentView().setCurrentView(area);
                updateExploreTab();
                checkAndDisableHigherViewButton(); // Check if the higher view button should be disabled
                main.getClickMeButton().requestFocus();
            });
            areasList.getItems().add(button);
        }
    }

    void updateCurrentViewLabel() {
        String currentViewName = model.accessCurrentView().getCurrentView().getName();

        String pattern = " \\(.*?\\)"; //remove (Home) or (King) or both to display just the name
        String cleanName = currentViewName.replaceAll(pattern, "");

        this.currentViewLabel.setText(cleanName);
    }

    void updateHigherButtonText(){
        higherViewButton.setText(model.accessCurrentView().getCurrentView().getHigher().toString());
        main.getClickMeButton().requestFocus();
    }

    void checkAndDisableHigherViewButton() {
        Area currentView = model.accessCurrentView().getCurrentView();
        if (currentView == null) {
            higherViewButton.setDisable(true);
            higherViewButton.setText("Unavailable");
            return;
        }
        Area higherView = currentView.getHigher();
        higherViewButton.setDisable(currentView.equals(higherView));
        higherViewButton.setText(currentView.equals(higherView) ? "Universe" : model.accessCurrentView().getCurrentView().getHigher().toString() );

    }
    public controller.CharacterController getCharacterController() {
        return characterController;
    }

    public void setCharacterController(controller.CharacterController characterController) {
        this.characterController = characterController;
    }



}