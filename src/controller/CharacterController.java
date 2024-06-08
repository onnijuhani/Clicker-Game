package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Model;
import model.buildings.properties.MilitaryProperty;
import model.characters.Character;
import model.characters.Person;
import model.characters.combat.CombatService;
import model.characters.combat.CombatSystem;
import model.stateSystem.Event;
import model.stateSystem.GameEvent;
import model.stateSystem.State;

import java.util.*;

import static model.Settings.formatNumber;

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

    @FXML
    private Label currentState;
    @FXML
    private Label stateTimeLeft;
    @FXML
    private VBox stateBox;

    @FXML
    private VBox opponentsBox;

    @FXML
    private Tooltip duelToolTip;
    @FXML
    private Tooltip challengeToolTip;


    public void updateCharacterTab(){
        propertyController.setCurrentProperty(currentCharacter.getPerson().getProperty());
        updateCharacterName();
        updateCharacterStatus();
        propertyController.updatePropertyTab();
        updateWalletInfo();
        updateAuthority();
        updateHomeQuarter();
        differentiatePlayer();
        updateCombatStats();
        main.resetBtn.setDisable(currentCharacter.getPerson() == Model.getPlayerAsPerson());
        disableArmyTab();
        getCurrentStates();
        main.updateCurrentlyViewing();
    }

    void setUpToolTips(){
        Person player = Model.getPlayerAsPerson();
        duelToolTip.setText("Winning Chance: " + String.format("%.2f", (CombatSystem.calculateWinningChance(Event.DUEL, player, currentCharacter.getPerson()))   * 100) + "%");
        challengeToolTip.setText("Winning Chance: " + String.format("%.2f", (CombatSystem.calculateWinningChance(Event.AuthorityBattle, player, currentCharacter.getPerson()))   * 100) + "%");
    }

    void getCurrentStates(){

        Person currentPerson = currentCharacter.getPerson();
        EnumSet states = currentPerson.getStates();


        if(states.contains(State.IN_BATTLE) || states.contains(State.IN_DEFENCE)){
            stateBox.setVisible(true);

            GameEvent authorityEvent = currentPerson.getAnyOnGoingEvent(Event.AuthorityBattle);
            GameEvent duelEvent = currentPerson.getAnyOnGoingEvent(Event.DUEL);
            GameEvent robberyEvent = currentPerson.getAnyOnGoingEvent(Event.ROBBERY);

            GameEvent onGoingEvent = null;

            List<Person> participants = new ArrayList<>();

            if (robberyEvent != null) {
                participants = robberyEvent.getParticipants();
                onGoingEvent = robberyEvent;
            }
            else if (authorityEvent != null) {
                participants = authorityEvent.getParticipants();
                onGoingEvent = authorityEvent;
            }
            else if (duelEvent != null) {
                participants = duelEvent.getParticipants();
                onGoingEvent = duelEvent;
            }

            List<Person> otherParticipants = participants.stream()
                    .filter(p -> !p.equals(currentPerson))
                    .toList();

            opponentsBox.getChildren().clear();

            if (!otherParticipants.isEmpty()) {
                for (Person participant : otherParticipants) {
                    Hyperlink link = new Hyperlink(participant.getCharacter().toString());
                    link.setOnAction(e -> handleParticipantClick(participant));
                    opponentsBox.getChildren().add(link);
                }
            } else {
                Hyperlink link = new Hyperlink("Unknown Character");
                opponentsBox.getChildren().add(link);
            }

            assert onGoingEvent != null;
            stateTimeLeft.setText(""+ Arrays.toString(onGoingEvent.timeLeftUntilExecution()));

            int[] timeLeft = onGoingEvent.timeLeftUntilExecution();
            stateTimeLeft.setText(String.format("%d days, %d months, %d years left", timeLeft[2], timeLeft[1], timeLeft[0]));

            currentState.setText("Currently in "+onGoingEvent.getEvent());

        }
        else{
            stateBox.setVisible(false);
        }

    }

    private void handleParticipantClick(Person participant) {
        openCharacterProfile(participant.getCharacter());
    }




    private void updateWalletInfo(){
        walletInfo.setText(currentCharacter.getPerson().getWallet().toStringValuesRows());
    }



    public void disableArmyTab(){
        main.armyTab.setDisable(!(currentCharacter.getPerson().getProperty() instanceof MilitaryProperty));
    }


    void updateCombatStats(){
        attackLevelLabel.setText("Level: "+currentCharacter.getPerson().getCombatStats().getOffenseLevel());
        defenseLevelLabel.setText("Level: "+currentCharacter.getPerson().getCombatStats().getDefenseLevel());

        attackTrainBtn.setText(formatNumber(currentCharacter.getPerson().getCombatStats().getOffense().getUpgradePrice())+" Gold");
        defenseTrainBtn.setText(formatNumber(currentCharacter.getPerson().getCombatStats().getDefense().getUpgradePrice())+" Gold");
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
        Platform.runLater(this::createTimeLine);
    }

    private void createTimeLine() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateCharacterTab()));
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
    void printEvents(ActionEvent event) {
        System.out.println("Ongoing Events : "+currentCharacter.getPerson().getOngoingEvents());
        System.out.println("Current States : "+currentCharacter.getPerson().getStates());
        System.out.println("Traits: "+ currentCharacter.getPerson().getAiEngine().getProfile());

        for(String string : currentCharacter.getPerson().getLoggerMessages()){
            System.out.println(string);
        }

        System.out.println("Current Aspirations: "+currentCharacter.getPerson().getAspirations());
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
        setUpToolTips();
        main.updateCurrentlyViewing();
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
        main.clickMeButton.requestFocus();
    }

    private void updatePreviousButtonState() {
        previousBtn.setDisable(characterHistory.isEmpty());
    }

    public MainController getMain() {
        return main;
    }
}
