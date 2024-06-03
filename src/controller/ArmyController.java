package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class ArmyController extends BaseController {
    @Override
    public void initialize() {
    }

    @FXML
    private VBox alloyCost;

    @FXML
    private AnchorPane armyView;

    @FXML
    private Label attackPower;

    @FXML
    private Label defPower;

    @FXML
    private Label foodCost;

    @FXML
    private Label goldCost;

    @FXML
    private Button hideButton;

    @FXML
    private Button increaseAttack;

    @FXML
    private Button increaseDef;

    @FXML
    private Label numOfSoldiers;

    @FXML
    private Button recruitSoldiers;

    @FXML
    private Button switchButton;

    @FXML
    private Button plusSoldier;
    @FXML
    private Button minusSoldier;
    @FXML
    private Label attDaysLeft;
    @FXML
    private Label defDaysLeft;
    @FXML
    private Label recruitTimeLeft;

    private boolean isShowing = true;

    @FXML
    void hideContent(ActionEvent event) {
        if (isShowing) {
            armyView.setVisible(false);
            hideButton.setText("Show");
            isShowing = false;
        } else {
            armyView.setVisible(true);
            hideButton.setText("Hide");
            isShowing = true;
        }

    }

    @FXML
    void switchView(ActionEvent event) {

    }

    @FXML
    void minusSoldier(ActionEvent event) {

    }

    @FXML
    void plusSoldier(ActionEvent event) {

    }





}
