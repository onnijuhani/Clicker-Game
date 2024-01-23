
package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.worldCreation.Area;

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
    private MainController main;
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
            case "Quarter" -> "Quarters";
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

    @FXML
    void updateExploreTab(){
        updateAreasList();
        updateCurrentViewLabel();
        updateHigherButtonText();
        updateAreaType();
        updateContainType();
        updateHigherType();
    }

    @FXML
    public void initialize() {

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

    void updateCurrentViewLabel(){
        this.currentViewLabel.setText(model.accessCurrentView().getCurrentView().toString());
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

}