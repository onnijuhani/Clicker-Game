package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.worldCreation.Area;
public class WorldExplorerController extends BaseController {
    @FXML private ListView<Button> areasList;
    @FXML private Label currentViewLabel;
    @FXML private Button higherViewButton;

    public WorldExplorerController() {
    }

    @FXML
    public void initialize() {
        updateExploreTab();
    }

    @FXML
    void updateExploreTab() {
        updateAreasList();
        updateCurrentViewLabel();
        updateHigherButtonText();
    }

    void updateCurrentViewLabel() {
        this.currentViewLabel.setText(model.accessCurrentView().getCurrentView().toString()
                + " " + model.accessCurrentView().getCurrentView().getClass().getSimpleName());
    }

    void updateAreasList() {
        areasList.getItems().clear();
        for (Area area : model.accessCurrentView().getContents()) {
            Button button = new Button(area.toString());
            button.setOnAction(event -> {
                model.accessCurrentView().setCurrentView(area);
                updateHigherButtonText();
                updateCurrentViewLabel();
                updateAreasList();
                checkAndDisableHigherViewButton();
            });
            areasList.getItems().add(button);
        }
    }

    void updateHigherButtonText() {
        higherViewButton.setText(model.accessCurrentView().getCurrentView().toString());
    }

    void checkAndDisableHigherViewButton() {
        Area currentView = model.accessCurrentView().getCurrentView();
        Area higherView = currentView.getHigher();
        higherViewButton.setDisable(currentView.equals(higherView));
    }

    @FXML
    void getHigherView() {
        model.accessCurrentView().setCurrentView(model.accessCurrentView().getCurrentView().getHigher());
        updateExploreTab();
    }

}

