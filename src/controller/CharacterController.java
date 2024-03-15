package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import model.characters.Character;
import model.characters.decisions.CombatService;

import java.util.ArrayDeque;
import java.util.Deque;

public class CharacterController extends BaseController  {

    @FXML
    private Label characterName;
    @FXML
    private ImageView characterPicture;
    @FXML
    private Label characterStatus;
    @FXML
    private Label walletInfo;
    @FXML
    private Hyperlink authority;
    @FXML
    private Hyperlink homeQuarter;
    @FXML
    private Label attackLevelLabel;
    @FXML
    private Label defenseLevelLabel;
    @FXML
    private Button attackTrainBtn;
    @FXML
    private Button defenseTrainBtn;


    @FXML
    private HBox attackBox;
    @FXML
    private HBox defenseBox;


    private MainController main;
    private PropertyController propertyController;

    private Character currentCharacter;

    private final Deque<Character> characterHistory = new ArrayDeque<>();
    private boolean isNavigatingBack = false;
    @FXML
    private Button previousBtn;
    @FXML
    private HBox CombatBox;


    private void updateWalletInfo(){
        walletInfo.setText(currentCharacter.getWallet().toStringValuesRows());
    }

    public void updateCharacterTab(){
        updateCharacterName();
        updateCharacterStatus();
        propertyController.setCurrentProperty(currentCharacter.getProperty());
        propertyController.updatePropertyTab();
        updateWalletInfo();
        updateAuthority();
        updateHomeQuarter();
        differentiatePlayer();
        updateCombatStats();
        checkUpgradeLevels();
    }

    public void checkUpgradeLevels(){
        if (currentCharacter.getCombatStats().getOffenseLevel() == 10){
            attackTrainBtn.setText("Maxed");
            attackTrainBtn.setDisable(true);
        }
        if (currentCharacter.getCombatStats().getDefenseLevel() == 10){
            defenseTrainBtn.setText("Maxed");
            defenseTrainBtn.setDisable(true);
        }
    }


    void updateCombatStats(){
        attackLevelLabel.setText("Level: "+currentCharacter.getCombatStats().getOffenseLevel());
        defenseLevelLabel.setText("Level: "+currentCharacter.getCombatStats().getDefenseLevel());

        attackTrainBtn.setText(currentCharacter.getCombatStats().getOffense().getUpgradePrice()+" Gold");
        defenseTrainBtn.setText(currentCharacter.getCombatStats().getDefense().getUpgradePrice()+" Gold");
    }
    public void updateCharacterName(){
        characterName.setText(currentCharacter.getName());
    }
    public void updateCharacterStatus(){
        characterStatus.setText(currentCharacter.getStatus().toString());
    }

    public void setMain(MainController main) {
        this.main = main;
    }


    @FXML
    public void initialize() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateCharacterTab()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    @FXML
    void getPrevious(ActionEvent event) {
        if (!characterHistory.isEmpty()) {
            isNavigatingBack = true;
            Character previousCharacter = characterHistory.pop();
            setCurrentCharacter(previousCharacter);
            isNavigatingBack = false;
        }
        updatePreviousButtonState(); // Update the button state
    }


    @FXML
    void executeDuel(){
        CombatService.executeDuel(model.accessPlayer(), currentCharacter);
    }

    @FXML
    void attackUpgrade(){
        model.accessPlayer().getCombatStats().upgradeOffenseWithGold();
        updateCombatStats();
        checkUpgradeLevels();
    }
    @FXML
    void defenseUpgrade(){
        model.accessPlayer().getCombatStats().upgradeDefenceWithGold();
        updateCombatStats();
        checkUpgradeLevels();
    }



    void differentiatePlayer(){
        if (currentCharacter.equals(model.accessPlayer())){
            attackBox.setVisible(true);
            defenseBox.setVisible(true);
            CombatBox.setVisible(false);
        }else{
            attackBox.setVisible(false);
            defenseBox.setVisible(false);
            CombatBox.setVisible(true);
        }
    }


    public PropertyController getPropertyController() {
        return propertyController;
    }

    public void setPropertyController(PropertyController propertyController) {
        this.propertyController = propertyController;
    }

    public Character getCurrentCharacter() {
        return currentCharacter;
    }

    public void setCurrentCharacter(Character newCharacter) {
        if (!isNavigatingBack && currentCharacter != null && !currentCharacter.equals(newCharacter)) {
            characterHistory.push(currentCharacter);
        }
        currentCharacter = newCharacter;
        updateCharacterTab();
        updatePreviousButtonState();
    }

    void updateAuthority(){
        if (currentCharacter.getAuthority().getCharacter().equals(currentCharacter)){
            authority.setText("No one but himself");

        }else {
            authority.setText(currentCharacter.getAuthority().toString());
        }
    }

    @FXML
    void changeCurrent(ActionEvent event) {
        openCharacterProfile(currentCharacter.getAuthority().getCharacter());
    }

    void updateHomeQuarter(){
        homeQuarter.setText(currentCharacter.getProperty().getLocation().toString());
    }
    @FXML
    void openHomeQuarter(ActionEvent event) {
        model.accessCurrentView().setCurrentView(currentCharacter.getProperty().getLocation());
        main.exploreMapController.updateExploreTab();
    }

    private void openCharacterProfile(Character character) {
        setCurrentCharacter(character);
        updateCharacterTab();
    }

    private void updatePreviousButtonState() {
        previousBtn.setDisable(characterHistory.isEmpty());
    }

}
