package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Model;
import model.buildings.properties.MilitaryProperty;
import model.characters.Character;
import model.characters.Peasant;
import model.characters.Person;
import model.characters.Support;
import model.characters.combat.CombatService;
import model.characters.combat.CombatSystem;
import model.stateSystem.Event;
import model.stateSystem.GameEvent;
import model.stateSystem.SpecialEventsManager;
import model.stateSystem.State;
import model.worldCreation.Nation;
import model.worldCreation.World;

import java.util.*;

import static model.Settings.formatNumber;

public class CharacterController extends BaseController  {

    @FXML
    private Label characterName;
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
    private ImageView characterImage;
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

    @Override
    public void update(){
        try {
            if(currentCharacter == null) return;
            differentiatePlayer();
            propertyController.setCurrentProperty(currentCharacter.getPerson().getProperty());
            updateCharacterName();
            updateCharacterStatus();
            propertyController.updatePropertyTab();
            updateWalletInfo();
            updateAuthority();
            updateHomeQuarter();
            updateCombatStats();
            main.resetBtn.setDisable(currentCharacter.getPerson() == Model.getPlayerAsPerson());
            disableArmyTab();
            getCurrentStates();
            main.updateCurrentlyViewing();
            updateCharacterImage();
            setCharacterPicture();
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }



    void setUpToolTips(){
        Person player = Model.getPlayerAsPerson();
        duelToolTip.setText("Winning Chance: " + String.format("%.2f", (CombatSystem.calculateWinningChance(Event.DUEL, player, currentCharacter.getPerson()))   * 100) + "%");
        challengeToolTip.setText("Winning Chance: " + String.format("%.2f", (CombatSystem.calculateWinningChance(Event.AuthorityBattle, player, currentCharacter.getPerson()))   * 100) + "%");
    }

    void getCurrentStates(){

        Person currentPerson = currentCharacter.getPerson();
        EnumSet<State> states = currentPerson.getStates();

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

            if(onGoingEvent == null) return;
            stateTimeLeft.setText(""+ Arrays.toString(onGoingEvent.getTimeLeftUntilExecution()));

            int[] timeLeft = onGoingEvent.getTimeLeftUntilExecution();
            stateTimeLeft.setText(String.format("%d days, %d months, %d years left", timeLeft[2], timeLeft[1], timeLeft[0]));

            currentState.setText("Currently in "+onGoingEvent.getEvent());

        }else{
            stateBox.setVisible(false);

        }
    }

    @FXML
    private Button duelBtn;
    @FXML
    private Button challengeBtn;

    void updateCombatButtons(){
        challengeBtn.setVisible(!CombatService.checkForError(Model.getPlayerAsCharacter(), currentCharacter));
        duelBtn.setVisible(!CombatService.checkForError(Model.getPlayerAsCharacter(), currentCharacter));
        CombatBox.setVisible(challengeBtn.isVisible() || duelBtn.isVisible());
    }


    private void handleParticipantClick(Person participant) {
        openCharacterProfile(participant.getCharacter());
    }

    private void updateCharacterImage(){
        EnumSet<model.stateSystem.State> states = currentCharacter.getPerson().getStates();
        String text = (states == null || states.isEmpty()) ? "None" : states.toString();
        Tooltip tooltip = new Tooltip("Current States: " + text);
        Tooltip tooltip2 = new Tooltip( currentCharacter + "\nCurrent States: " + text);
        Tooltip.install(characterName, tooltip);
        Tooltip.install(characterImage, tooltip2);
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
        for(String string : currentCharacter.getPerson().getLoggerMessages()){
            System.out.println(string);
        }
        System.out.println("Ongoing Events : "+currentCharacter.getPerson().getOngoingEvents());
        System.out.println("Current States : "+currentCharacter.getPerson().getStates());
        System.out.println("Current Aspirations: "+currentCharacter.getPerson().getAspirations());
        System.out.println("Traits: "+ currentCharacter.getPerson().getAiEngine().getProfile());
    }

    @FXML
    void executeDuel(){
        CombatService.executeDuel(model.getPlayerCharacter(), currentCharacter);
        main.update();
    }

    @FXML
    void executeAuthorityBattle(){
        CombatService.executeAuthorityBattle(model.getPlayerCharacter(), currentCharacter);
        main.update();
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
        if (currentCharacter == Model.getPlayerAsCharacter()){
            attackBox.setVisible(true);
            defenseBox.setVisible(true);
        }else{
            attackBox.setVisible(false);
            defenseBox.setVisible(false);
        }
        updateCombatButtons();
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
        update();
        updatePreviousButtonState();
        main.getRelationsController().resetEverything();
        setUpToolTips();
        main.updateCurrentlyViewing();
    }


    void updateAuthority(){
        if (currentCharacter.getRole().getAuthority().getSupervisor().equals(currentCharacter.getRole().getPosition())){
            authority.setText("No one but himself");
        }else {
            if(currentCharacter.getRole().getPosition() == null){
                authority.setText(currentCharacter.getRole().getAuthority().toString());
            }else {
                authority.setText(currentCharacter.getRole().getPosition().getSupervisor().toString());
            }
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
        main.openExploreMapTab();
    }

    private void openCharacterProfile(Character character) {
        setCurrentCharacter(character);
        update();
        main.clickMeButton.requestFocus();
    }

    @FXML
    private void triggerStatusInfo(ActionEvent event) {
        if(currentCharacter instanceof Peasant){
            SpecialEventsManager.triggerPeasantInfo();
        } else {
            if(currentCharacter instanceof Support){
                SpecialEventsManager.sentinelsInfoSent = false;
                SpecialEventsManager.triggerSentinelsInfo();
            } else {
                SpecialEventsManager.triggerAuthorityInfo();
            }
        }
    }

    private void setCharacterPicture(){

        if(currentCharacter.getPerson().getCharacterPic() == 0){
            currentCharacter.getPerson().generatePicture();
        }

        Image image;
        String number = String.valueOf(currentCharacter.getPerson().getCharacterPic());
        String imagePath = String.format("/characterImages/%s.png", number);
        image = new Image(imagePath);

        characterImage.setImage(image);
    }



    private void updatePreviousButtonState() {
        previousBtn.setDisable(characterHistory.isEmpty());
    }

    public MainController getMain() {
        return main;
    }



    public AnchorPane getSearchPane() {
        return searchPane;
    }

    @FXML
    private AnchorPane searchPane;
    private VBox resultsBox;

    @FXML
    void searchPerson(ActionEvent event) {
        if (searchPane.getChildren().isEmpty()) {
            resultsBox = new VBox();
            createSearchPanel(resultsBox);
        }
        searchPane.setVisible(true);

    }

    private void createSearchPanel(VBox resultsBox) {
        Button closeButton = new Button("Close");

        TextField searchField = new TextField();


        searchField.setPromptText("Type to search...");

        BorderPane searchPanel = new BorderPane();
        searchPanel.setCenter(searchField);
        searchPanel.setRight(closeButton);

        closeButton.setOnAction(e -> {
            searchPane.setVisible(false);
        });

        ScrollPane scrollPane = new ScrollPane(resultsBox);
        scrollPane.setMaxHeight(450);
        scrollPane.setMinWidth(250);
        scrollPane.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);

        searchPanel.setBottom(scrollPane);

        searchPane.getChildren().add(searchPanel);

        // Add listener to searchField to trigger search on Enter key or text change
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                performSearch(searchField.getText());
            }
        });

        // Or trigger search on each text change (comment out if using Enter key only)

    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        performSearch(newValue);
    });

    }

    private void performSearch(String query) {
        new Thread(() -> {
            List<Person> results = new ArrayList<>();

            // Search in the current player's nation
            Nation playerNation = Model.getPlayerRole().getNation();
            List<Person> playerNationResults = playerNation.searchCharactersByName(query);
            results.addAll(playerNationResults);

            // Extend search to other nations if player's nation finds less than 10-20 persons
            if (playerNationResults.size() < 10) {
                List<Nation> nationsToSearch = new ArrayList<>();
                nationsToSearch.addAll(World.getAllNonPlayerNations());

                for (Nation nation : nationsToSearch) {
                    List<Person> nationResults = nation.searchCharactersByName(query);
                    results.addAll(nationResults);

                    // Stop extending search if we find enough results
                    if (results.size() >= 20) {
                        break;
                    }
                }
            }

            Platform.runLater(() -> displayResults(results));
        }).start();
    }

    private void displayResults(List<Person> results) {
        resultsBox.getChildren().clear();
        for (Person person : results) {
            Hyperlink link = new Hyperlink(person + "  -  "+ person.getRole());
            link.setStyle("-fx-text-fill: white;");

            link.setOnAction(e -> setSearchedCharacter(person));
            resultsBox.getChildren().add(link);
        }
    }

    private void setSearchedCharacter(Person person) {
        setCurrentCharacter(person.getCharacter());
        searchPane.setVisible(false);
    }

}
