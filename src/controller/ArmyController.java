package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Model;
import model.buildings.Property;
import model.buildings.properties.MilitaryProperty;
import model.characters.Character;
import model.resourceManagement.TransferPackage;
import model.stateSystem.Event;
import model.stateSystem.GameEvent;
import model.war.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ArmyController extends BaseController {
    @Override
    public void initialize() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.25), e -> updateArmyTab()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    @FXML
    void startTestWar(ActionEvent event) {
        switchButton.setVisible(true);
        switchViewMethod();
        MilitaryBattle militaryBattle = MilitaryBattleManager.executeMilitaryBattle(Model.getPlayerAsPerson(),currentCharacter.getPerson());
        if(militaryBattle == null){
            return;
        }
        siegeController.setMilitaries((Military) Model.getPlayerAsPerson().getProperty(),
                (Military) currentCharacter.getPerson().getProperty(),
                militaryBattle,
                "Attacker", "Defender" );


    }

    public SiegeController getSiegeController() {
        return siegeController;
    }

    @FXML
    private SiegeController siegeController;

    @FXML
    private AnchorPane armyManager;
    @FXML
    private AnchorPane siegeManager;

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

    @FXML
    private CheckBox autoTraining;
    @FXML
    private CheckBox autoRecruit;

    private boolean isArmyView = true;
    private boolean isShowing = true;

    protected CharacterController characterController;
    private Character currentCharacter;
    private Army army;
    private String latestUpgrade = "Defence";

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

    void differentiatePlayer() {
        boolean isPlayer = currentCharacter.getPerson() == model.getPlayerPerson();

        List<Node> controls = Arrays.asList(
                increaseAttackBtn,
                increaseDefBtn,
                minusSoldierBtn,
                plusSoldierBtn,
                recruitSoldiersBtn,
                autoTraining,
                trainingBox,
                autoRecruit
        );

        for (Node control : controls) {
            control.setDisable(!isPlayer);
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
        if(!(currentCharacter.getPerson() == model.getPlayerPerson()) || !currentCharacter.getPerson().isPlayer()) {
            return;
        }
        if(army == null){
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
                    if(autoTraining.isSelected()){
                        if(currentCharacter.getPerson().getWallet().getAlloy() > 10_000) {
                            if (Objects.equals(latestUpgrade, "Defence")) {
                                increaseAttackFunction();
                                latestUpgrade = "Attack";
                            } else {
                                increaseDefFunction();
                                latestUpgrade = "Defence";
                            }
                        }
                    }else {
                        trainingBox.setVisible(false);
                        increaseDefBtn.setDisable(false);
                        increaseAttackBtn.setDisable(false);
                    }

                }
        );

        recruitSoldierEvent.ifPresentOrElse(
                event -> recruitTimeLeft.setText(event.getTimeLeftShortString()),
                () -> {
                    if(autoRecruit.isSelected()){
                        if(currentCharacter
                                .getPerson()
                                .getWallet()
                                .hasEnoughResources(ArmyCost.getRecruitingCost().multiply(Double.parseDouble(soldierAmountToTrain.getText())))) {

                            recruitSoldiersFunction();
                        }
                    }else {
                        recruitTimeLeft.setText("");
                        recruitSoldiersBtn.setDisable(false);
                    }
                }
        );
    }


    public void costAndPowerUpdate(){
        if(army == null){
            return;
        }

        defPower.setText(""+army.getTotalDefencePower());
        attackPower.setText(""+army.getTotalAttackPower());
        numOfSoldiers.setText(""+army.getNumOfSoldiers());

        TransferPackage runningCost = army.getRunningCost();

        foodCost.setText("F: "+runningCost.food());
        alloyCost.setText("A: "+runningCost.alloy());
        goldCost.setText("G: "+runningCost.gold());

        increaseDefBtn.setText(ArmyCost.increaseArmyDefence+ " Alloys");
        increaseAttackBtn.setText(ArmyCost.increaseArmyAttack+ " Alloys");

        updateRecruitCost();




    }


    void updateRecruitCost(){
        recruitSoldiersBtn.setText(ArmyCost.getRecruitingCost().multiply(Double.parseDouble(soldierAmountToTrain.getText())).gold()+" Gold");
    }



    @FXML
    void increaseAtt(ActionEvent event) {
        increaseAttackFunction();
    }

    private void increaseAttackFunction() {
        if(army.increaseAttackPower()){
            increaseAttackBtn.setDisable(true);
            increaseDefBtn.setDisable(true);
        }
    }

    @FXML
    void increaseDef(ActionEvent event) {
        increaseDefFunction();
    }

    private void increaseDefFunction() {
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
        recruitSoldiersFunction();

    }

    private void recruitSoldiersFunction() {
        if(army.recruitSoldier(Integer.parseInt(soldierAmountToTrain.getText()))){
            recruitSoldiersBtn.setDisable(true);
        }
    }

    @FXML
    public void switchView(ActionEvent event) {
        switchViewMethod();
    }

    protected void switchViewMethod() {
        armyManager.setVisible(!isArmyView);
        siegeManager.setVisible(isArmyView);
        isArmyView = !isArmyView;
    }


}


