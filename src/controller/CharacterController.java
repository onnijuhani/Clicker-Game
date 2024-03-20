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
import model.Model;
import model.characters.Character;
import model.characters.combat.CombatService;

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

    public static Character currentCharacter;

    private final Deque<Character> characterHistory = new ArrayDeque<>();
    private boolean isNavigatingBack = false;
    @FXML
    private Button previousBtn;
    @FXML
    private HBox CombatBox;


    private void updateWalletInfo(){
        walletInfo.setText(currentCharacter.getPerson().getWallet().toStringValuesRows());
    }

    public void updateCharacterTab(){
        updateCharacterName();
        updateCharacterStatus();
        propertyController.setCurrentProperty(currentCharacter.getPerson().getProperty());
        propertyController.updatePropertyTab();
        updateWalletInfo();
        updateAuthority();
        updateHomeQuarter();
        differentiatePlayer();
        updateCombatStats();
        main.resetBtn.setDisable(currentCharacter.getPerson() == Model.getPlayerAsPerson());
    }


    void updateCombatStats(){
        attackLevelLabel.setText("Level: "+currentCharacter.getPerson().getCombatStats().getOffenseLevel());
        defenseLevelLabel.setText("Level: "+currentCharacter.getPerson().getCombatStats().getDefenseLevel());

        attackTrainBtn.setText(currentCharacter.getPerson().getCombatStats().getOffense().getUpgradePrice()+" Gold");
        defenseTrainBtn.setText(currentCharacter.getPerson().getCombatStats().getDefense().getUpgradePrice()+" Gold");
    }
    public void updateCharacterName(){
        characterName.setText(currentCharacter.getName());
    }
    public void updateCharacterStatus(){
        characterStatus.setText(currentCharacter.getRole().getStatus().toString());
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
        CombatService.executeDuel(model.getPlayerCharacter(), currentCharacter);
    }

    @FXML
    void executeAuthorityBattle(){
        CombatService.executeAuthorityBattle(model.getPlayerCharacter(), currentCharacter);
    }

    @FXML
    void attackUpgrade(){
        model.getPlayerCharacter().getPerson().getCombatStats().upgradeOffenseWithGold();
        updateCombatStats();
    }
    @FXML
    void defenseUpgrade(){
        model.getPlayerCharacter().getPerson().getCombatStats().upgradeDefenceWithGold();
        updateCombatStats();
    }



    void differentiatePlayer(){
        if (currentCharacter.getPerson().equals(model.getPlayerPerson())){
            attackBox.setVisible(true);
            defenseBox.setVisible(true);
            CombatBox.setVisible(false);
        }else{
            attackBox.setVisible(false);
            defenseBox.setVisible(false);
            CombatBox.setVisible(true);
        }
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
        main.getRelationsController().resetEverything();
    }

    void updateAuthority(){
        if (currentCharacter.getRole().getAuthority().getCharacterInThisPosition().equals(currentCharacter)){
            authority.setText("No one but himself");
        }else {
            authority.setText(currentCharacter.getRole().getAuthority().toString());
        }
    }

    @FXML
    void changeCurrentToAuth(ActionEvent event) {
        openCharacterProfile(currentCharacter.getRole().getAuthority().getCharacterInThisPosition());
    }

    void updateHomeQuarter(){
        homeQuarter.setText(currentCharacter.getPerson().getProperty().getLocation().toString());
    }
    @FXML
    void openHomeQuarter(ActionEvent event) {
        model.accessCurrentView().setCurrentView(currentCharacter.getPerson().getProperty().getLocation());
        main.exploreMapController.updateExploreTab();
    }

    private void openCharacterProfile(Character character) {
        setCurrentCharacter(character);
        updateCharacterTab();
    }

    private void updatePreviousButtonState() {
        previousBtn.setDisable(characterHistory.isEmpty());
    }

    public MainController getMain() {
        return main;
    }
}
