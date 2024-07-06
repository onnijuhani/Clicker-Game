package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.Model;
import model.war.Army;
import model.war.ArmyCost;
import model.war.Military;
import model.war.MilitaryBattle;

import java.util.List;
import java.util.Objects;

public class SiegeController extends BaseController {


    public void update() {
        checkState();
        updateStats();
        updateAvailableStats();
        testForBattle();
    }

    private void checkState() {
        if(militaryBattle == null){
            return;
        }
        if(!militaryBattle.isOnGoing()){ // if battle is ended, set this null.
            militaryBattle = null;
            armyController.switchViewMethod();
        }
    }


    /**
     * @param militaryLeft Set military to left side of the UI (Player)
     * @param militaryRight Set military to right side of the ui (NPC)
     * @param militaryBattle MilitaryBattle object
     * @param uiLeftSideIsAttackerOrDefender Set if player is "Attacker" or "Defender" in this battle
     */
    public void setMilitaries(Military militaryLeft, Military militaryRight, MilitaryBattle militaryBattle, String uiLeftSideIsAttackerOrDefender) {
        this.militaryLeft = militaryLeft;
        this.militaryRight = militaryRight;
        this.militaryBattle = militaryBattle;
        this.left = uiLeftSideIsAttackerOrDefender;
    }

    private void testForBattle(){
        if(militaryBattle != null){ // if militaryBattle is already set, return
            return;
        }
        if(Model.getPlayerAsPerson().getProperty() instanceof Military military){
            if(military.getArmy().getMilitaryBattle() != null){
                MilitaryBattle battle = military.getArmy().getMilitaryBattle();

                if(!battle.isOnGoing()){ // set to null if battle is ended
                    militaryBattle = null;
                    return;
                }

                String uiLeftSide = null;

                if(military.getState().equals(Army.ArmyState.DEFENDING)){
                    militaryRight = battle.getAttackingMilitary();
                    uiLeftSide = "Defender";
                }
                if(military.getState().equals(Army.ArmyState.ATTACKING)){
                    militaryRight = battle.getDefendingMilitary();
                    uiLeftSide = "Attacker";
                }

                setMilitaries(military, militaryRight, battle, uiLeftSide);
                armyController.switchViewMethod();
            }

        }
    }

    private Military militaryLeft; // right and left refers to the side in the UI
    private Military militaryRight;

    public MilitaryBattle getMilitaryBattle() {
        return militaryBattle;
    }

    private MilitaryBattle militaryBattle;
    private String left; // whether player is attacker or defender

    public void setArmyController(ArmyController armyController) {
        this.armyController = armyController;
    }

    private ArmyController armyController;

    public void updateStats() {

        if(militaryBattle == null){
            return;
        }

        MilitaryBattle.ArmyStats armyLeftStats = militaryBattle.getDefendingArmyStats();
        MilitaryBattle.ArmyStats armyRightStats = militaryBattle.getAttackingArmyStats();

        if(Objects.equals(left, "Attacker")){
            armyLeftStats = militaryBattle.getAttackingArmyStats();
            armyRightStats = militaryBattle.getDefendingArmyStats();
        }

        // update current day
        day.setText("Day: " + militaryBattle.getDays());

        // Update the soldiers count for both sides
        soldiersLeft.setText("Soldiers: " + armyLeftStats.getNumOfSoldiers());
        soldiersRight.setText("Soldiers: " + armyRightStats.getNumOfSoldiers());

        // Update the offence power for both sides
        offenceLeft.setText("Offence Weapons: " + armyLeftStats.getAttackPower() / ArmyCost.increaseArmyAttack);
        offenceRight.setText("Offence Weapons: " + armyRightStats.getAttackPower() / ArmyCost.increaseArmyAttack);

        // Update the defence power for both sides
        defenceLeft.setText("Defence Weapons: " + armyLeftStats.getDefencePower() / ArmyCost.increaseArmyDefence);
        defenceRight.setText("Defence Weapons: " + armyRightStats.getDefencePower() / ArmyCost.increaseArmyDefence);

        // Update the state for both sides (attacker or defender)
        if(militaryBattle.getAttackingMilitary() == militaryLeft){
            stateLeft.setText("Attacker");
            stateRight.setText("Defender");
            // Calculate and update the winning chance
            double winningChanceValue = militaryBattle.calculateWinningChance();
            double losingChanceValue = 1 - winningChanceValue;
            winningChance.setText(String.format("%.2f%% - %.2f%%", winningChanceValue * 100, losingChanceValue * 100));
        }else{
            stateLeft.setText("Defender");
            stateRight.setText("Attacker");
            // Calculate and update the winning chance
            double winningChanceValue = militaryBattle.calculateWinningChance();
            double losingChanceValue = 1 - winningChanceValue;
            winningChance.setText(String.format("%.2f%% - %.2f%%", losingChanceValue * 100, winningChanceValue * 100));
        }




        // update log
        List<String> logEntries = militaryBattle.getBattleLog();
        ObservableList<String> items = FXCollections.observableArrayList(logEntries);
        battleLog.setItems(items);
        battleLog.scrollTo(items.size() - 1);


        // update photos
        setCharacterPictures();
    }

    private void setCharacterPictures(){

        Image image;
        String number = String.valueOf(militaryLeft.getOwner().getCharacterPic());
        String imagePath = String.format("/characterImages/%s.png", number);
        image = new Image(imagePath);

        leftImage.setImage(image);

        if(militaryRight.getOwner().getCharacterPic() == 0){
            militaryRight.getOwner().generatePicture();
        }
        number = String.valueOf(militaryRight.getOwner().getCharacterPic());
        imagePath = String.format("/characterImages/%s.png", number);
        image = new Image(imagePath);

        rightImage.setImage(image);

    }




    @FXML
    private ImageView leftImage;
    @FXML
    private ImageView rightImage;


    @FXML
    private ListView<String> battleLog;

    @FXML
    private Label defenceLeft;

    @FXML
    private Label defenceRight;

    @FXML
    private Label offenceLeft;

    @FXML
    private Label offenceRight;
    @FXML
    private Label day;


    void updateAvailableStats(){
        if(militaryBattle == null){
            return;
        }
        int amountSoldiers = militaryLeft.getArmy().getNumOfSoldiers();
        availableSoldiers.setText("Available: " +  amountSoldiers);

        int amountDefence = militaryLeft.getArmy().getDefencePower();
        availableDefence.setText("Available: " + amountDefence);

        int amountOffence = militaryLeft.getArmy().getAttackPower();
        availableOffence.setText("Available: " + amountOffence);

    }

    private MilitaryBattle.ArmyStats getPlayerArmyStats() {
        MilitaryBattle.ArmyStats armyStats;
        if(Objects.equals(left, "Attacker")){
            armyStats = militaryBattle.getAttackingArmyStats();
        }else{
            armyStats = militaryBattle.getDefendingArmyStats();
        }
        return armyStats;
    }

    @FXML
    private Label availableOffence;

    @FXML
    private Label availableDefence;

    @FXML
    private Label availableSoldiers;
    @FXML
    void sendDefence(ActionEvent event) {
        MilitaryBattle.ArmyStats armyStats = getPlayerArmyStats();
        armyStats.addDefencePower(militaryLeft.getArmy());
    }



    @FXML
    void sendOffence(ActionEvent event) {
        MilitaryBattle.ArmyStats armyStats = getPlayerArmyStats();
        armyStats.addAttackPower(militaryLeft.getArmy());
    }

    @FXML
    void sendSoldiers(ActionEvent event) {
        MilitaryBattle.ArmyStats armyStats = getPlayerArmyStats();
        armyStats.addSoldiers(militaryLeft.getArmy());
    }

    @FXML
    private Label soldiersLeft;

    @FXML
    private Label soldiersRight;

    @FXML
    private Label stateLeft;

    @FXML
    private Label stateRight;

    @FXML
    private Label winningChance;

    @FXML
    void getBack(ActionEvent event) {
        armyController.switchViewMethod();
    }



}
