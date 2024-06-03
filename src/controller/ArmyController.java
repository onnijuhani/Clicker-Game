package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.characters.Character;

public class ArmyController extends BaseController {
    @Override
    public void initialize() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateArmyTab()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
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

    protected CharacterController characterController;
    private Character currentCharacter;

    private void updateArmyTab() {
    }

    public void setCurrentCharacter(){
        if(currentCharacter == characterController.getCurrentCharacter()){
            return;
        }else{
            currentCharacter = characterController.getCurrentCharacter();
        }
    }



    void differentiatePlayer(){
        if(characterController.getCurrentCharacter() == model.getPlayerCharacter()){
            increaseAttack.setDisable(false);
            increaseDef.setDisable(false);
            minusSoldier.setDisable(false);
            plusSoldier.setDisable(false);
            recruitSoldiers.setDisable(false);
        }else{
            increaseAttack.setDisable(true);
            increaseDef.setDisable(true);
            minusSoldier.setDisable(true);
            plusSoldier.setDisable(true);
            recruitSoldiers.setDisable(true);
        }
    }

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
