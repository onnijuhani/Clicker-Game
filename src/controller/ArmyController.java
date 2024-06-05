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
import model.buildings.Property;
import model.buildings.properties.MilitaryProperty;
import model.characters.Character;
import model.resourceManagement.TransferPackage;
import model.stateSystem.Event;
import model.stateSystem.GameEvent;
import model.war.Army;
import model.war.ArmyCost;

import java.util.Optional;

public class ArmyController extends BaseController {
    @Override
    public void initialize() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateArmyTab()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }


    @FXML
    private Label alloyCost;

    @FXML
    private AnchorPane armyView;

    @FXML
    private Label trainingTime;

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
    private Button increaseAttackBtn;

    @FXML
    private Button increaseDefBtn;

    @FXML
    private Button minusSoldierBtn;

    @FXML
    private Label numOfSoldiers;

    @FXML
    private Button plusSoldierBtn;

    @FXML
    private Button recruitSoldiersBtn;

    @FXML
    private Label recruitTimeLeft;

    @FXML
    private Label soldierAmountToTrain;

    @FXML
    private Button switchButton;
    @FXML
    private VBox trainingBox;
    private boolean isShowing = true;

    protected CharacterController characterController;
    private Character currentCharacter;
    private Army army;

    private void updateArmyTab() {
        timerUpdate();
        costAndPowerUpdate();
    }

    public void setCurrentCharacter() {
        if (currentCharacter == characterController.getCurrentCharacter()) {
            return;
        } else {
            currentCharacter = characterController.getCurrentCharacter();

            Property property = currentCharacter.getPerson().getProperty();

            if (property instanceof MilitaryProperty militaryProperty) {
                army = militaryProperty.getArmy();
            } else {
                army = null;
            }
        }
    }

    void differentiatePlayer(){
        if(currentCharacter == model.getPlayerCharacter()){
            increaseAttackBtn.setDisable(false);
            increaseDefBtn.setDisable(false);
            minusSoldierBtn.setDisable(false);
            plusSoldierBtn.setDisable(false);
            recruitSoldiersBtn.setDisable(false);
        }else{
            increaseAttackBtn.setDisable(true);
            increaseDefBtn.setDisable(true);
            minusSoldierBtn.setDisable(true);
            plusSoldierBtn.setDisable(true);
            recruitSoldiersBtn.setDisable(true);
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

    public void timerUpdate() {

        if (currentCharacter == null) {
            return;
        }

        Optional<GameEvent> armyTrainingEvent = Optional.ofNullable(currentCharacter.getPerson().getAnyOnGoingEvent(Event.ArmyTraining));
        Optional<GameEvent> recruitSoldierEvent = Optional.ofNullable(currentCharacter.getPerson().getAnyOnGoingEvent(Event.RecruitSoldier));

        armyTrainingEvent.ifPresentOrElse(
                event -> {
                    trainingTime.setText(event.getTimeLeftShortString());
                    trainingBox.setVisible(true);
                },
                () -> {
                    trainingBox.setVisible(false);
                    increaseDefBtn.setDisable(false);
                    increaseAttackBtn.setDisable(false);
                }
        );

        recruitSoldierEvent.ifPresentOrElse(
                event -> recruitTimeLeft.setText(event.getTimeLeftShortString()),
                () -> {
                    recruitTimeLeft.setText("");
                    recruitSoldiersBtn.setDisable(false);
                }
        );
    }


    public void costAndPowerUpdate(){
        if(army == null){
            return;
        }

        defPower.setText(""+army.totalDefencePower());
        attackPower.setText(""+army.totalAttackPower());
        numOfSoldiers.setText(""+army.getNumOfSoldiers());

        TransferPackage runningCost = army.getRunningCost();

        foodCost.setText("F: "+runningCost.food());
        alloyCost.setText("A: "+runningCost.alloy());
        goldCost.setText("G: "+runningCost.gold());

        increaseDefBtn.setText(ArmyCost.increaseArmyDefence+ " Alloys");
        increaseAttackBtn.setText(ArmyCost.increaseArmyAttack+ " Alloys");




    }


    void updateRecruitCost(){
        recruitSoldiersBtn.setText(ArmyCost.getRecruitingCost().multiply(Double.parseDouble(soldierAmountToTrain.getText())).toShortString());
    }



    @FXML
    void increaseAtt(ActionEvent event) {
        if(army.increaseAttackPower()){
            increaseAttackBtn.setDisable(true);
            increaseDefBtn.setDisable(true);
        }
    }

    @FXML
    void increaseDef(ActionEvent event) {
        if(army.increaseDefencePower()){
            increaseAttackBtn.setDisable(true);
            increaseDefBtn.setDisable(true);
        }
    }

    @FXML
    void minusSoldier(ActionEvent event) {
        int currentAmount = Integer.parseInt(soldierAmountToTrain.getText());
        if(currentAmount > 1){
            currentAmount--;
            soldierAmountToTrain.setText(String.valueOf(currentAmount));
        }
        updateRecruitCost();
    }

    @FXML
    void plusSoldier(ActionEvent event) {
        int currentAmount = Integer.parseInt(soldierAmountToTrain.getText());
        if(currentAmount < 10){
            currentAmount++;
            soldierAmountToTrain.setText(String.valueOf(currentAmount));
        }
        updateRecruitCost();
    }

    @FXML
    void recruitSoldiers(ActionEvent event) {
        if(army.recruitSoldier(Integer.parseInt(soldierAmountToTrain.getText()))){
            recruitSoldiersBtn.setDisable(true);
        }

    }

    @FXML
    void switchView(ActionEvent event) {

    }





}
