package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Model;
import model.characters.Character;
import model.characters.Trait;
import model.characters.player.TraitSelection;
import model.characters.player.clicker.Clicker;
import model.resourceManagement.TransferPackage;
import model.stateSystem.MessageTracker;
import model.stateSystem.PopUpMessageTracker;
import model.stateSystem.SpecialEventsManager;
import model.time.Time;
import model.worldCreation.Quarter;

import java.util.List;

import static model.stateSystem.SpecialEventsManager.triggerAutoplayWarning;
import static model.stateSystem.SpecialEventsManager.triggerStartingMessage;

public class MainController extends BaseController {

    @FXML
    private TopSectionController topSectionController;
    @FXML
    private ClickerShopController clickerShopController;
    @FXML
    private ExchangeController exchangeController;
    @FXML
    private WorkWalletController workWalletController;
    @FXML
    protected ExploreMapController exploreMapController;
    private static MainController instance;
    public CharacterController getCharacterController() {
        return characterController;
    }

    @FXML
    private CharacterController characterController;
    @FXML
    private PropertyController propertyController;
    @FXML
    private InformationController informationController;
    @FXML
    private OverviewController overviewController;
    @FXML
    private ArmyController armyController;
    @FXML
    private SiegeController siegeController;
    @FXML
    private WarController warController;
    @FXML
    private VBox traitBox;
    @FXML
    private HBox traitButtonsBox;

    private void createTraitButtons(){
        int i = 0;
        VBox vBox = new VBox();
        for (Trait trait : Trait.values()) {
            if(i == 0){
                vBox = new VBox();
                traitButtonsBox.getChildren().add(vBox);
            }
            i++;if(i == 2){i=0;}

            Button button = new Button(trait.toString());
            vBox.getChildren().add(button);
            VBox finalVBox = vBox;
            finalVBox.setSpacing(3);
            finalVBox.setAlignment(Pos.TOP_CENTER);
            button.setOnAction(e -> handleTraitSelection(trait, finalVBox));

        }
    }

    private void handleTraitSelection(Trait trait, VBox vbox) {
        clickMeButton.setBlendMode(BlendMode.DIFFERENCE);
        updateProfile(trait);
        vbox.setDisable(true);
        handleTraitSelectionEnding();
    }

    private void handleTraitSelectionEnding() {
        if(TraitSelection.selectedTraitsCount > 2){
            traitButtonsBox.setDisable(true);
            Label label = new Label(TraitSelection.getString());
            label.setStyle("-fx-text-fill: white; -fx-font-size: 30px;");
            Button button = new Button("Start Game");
            button.setOnAction(e -> traitBox.setVisible(false));
            traitBox.getChildren().add(label);
            traitBox.getChildren().add(button);
            TraitSelection.setProfile();
        }
    }

    private void updateProfile(Trait trait) {
        int[] probabilities = new int[]{50, 30, 20};
        TraitSelection.profile.put(trait, probabilities[TraitSelection.selectedTraitsCount]);
        TraitSelection.selectedTraitsCount++;
    }


    private boolean isIncompatible(Trait trait1, Trait trait2) {

        return (trait1 == Trait.Slaver && trait2 == Trait.Liberal) ||
                (trait1 == Trait.Liberal && trait2 == Trait.Slaver) ||
                (trait1 == Trait.Aggressive && trait2 == Trait.Passive) ||
                (trait1 == Trait.Loyal && trait2 == Trait.Disloyal) ||
                (trait1 == Trait.Attacker && trait2 == Trait.Defender);
    }


    @FXML
    private AnchorPane mainLayout;

    public RelationsController getRelationsController() {
        return relationsController;
    }

    @FXML
    private RelationsController relationsController;
    @FXML
    public Button resetBtn;

    @FXML
    protected Button clickMeButton;
    @FXML
    private ListView<String> eventList;

    @FXML
    private CheckBox generateMessages;
    @FXML
    private CheckBox minorMessages;
    @FXML
    private CheckBox errorMessages;
    @FXML
    private CheckBox shopMessages;
    @FXML
    protected CheckBox incrementClicker;

    @FXML
    private CheckBox hideUtility;

    @FXML
    protected Button pauseBtn;

    @FXML
    protected TabPane mainTabPane;
    @FXML
    protected TabPane informationTabPane;
    @FXML
    protected Tab exploreMapTab;
    @FXML
    protected Tab characterTab;
    @FXML
    protected Tab armyTab;
    @FXML
    protected Tab relationsTab;
    @FXML
    protected Button settingsBtn;

    @FXML
    private HBox settingsBox;
    @FXML
    private VBox popUpBox;
    @FXML
    private Label popUpMessage;
    @FXML
    private GridPane topSection;
    @FXML
    protected Button closePopUpBtn;
    @FXML
    private ImageView popUpImage;
    @FXML
    private Label popUpHeadline;

    @FXML
    private CheckBox pausePopBtn;
    @FXML
    private Label currentlyViewing;
    @FXML public CheckBox autoPlay;
    @FXML CheckBox popUps;


    boolean autoContinue = false;

    public MainController() {
        super();
        instance = this;
    }

    @Override
    public void setModel(Model model) {
        super.setModel(model);
        setControllerModels(model);
    }

    private void setControllerModels(Model model) {
        if (topSectionController != null) {
            topSectionController.setModel(model);
            topSectionController.setMain(this);
        } else {
            logError("TopSectionController is null");
        }
        if (clickerShopController != null) {
            clickerShopController.setModel(model);
        } else {
            logError("ClickerShopController is null");
        }
        if (exchangeController != null) {
            exchangeController.setModel(model);
            exchangeController.setMain(this);
        } else {
            logError("ExchangeController is null");
        }
        if (workWalletController != null) {
            workWalletController.setModel(model);
        } else {
            logError("WorkWalletController is null");
        }
        if (exploreMapController != null) {
            exploreMapController.setModel(model);
            exploreMapController.setMain(this);
            exploreMapController.setCharacterController(characterController);
        } else {
            logError("ExploreController is null");
        }
        if (characterController != null) {
            characterController.setModel(model);
            characterController.setMain(this);
            characterController.setPropertyController(propertyController);
        } else {
            logError("CharacterController is null");
        }
        if (propertyController != null) {
            propertyController.setModel(model);
            propertyController.setMain(this);
            propertyController.setCharacterController(characterController);
        } else {
            logError("PropertyController is null");
        }
        if (relationsController != null) {
            relationsController.setMain(this);
            relationsController.setCharacterController(characterController);
            relationsController.setModel(model);
            relationsController.resetEverything();
        } else {
            logError("RelationsController is null");
        }
        if (informationController != null) {
            informationController.setMain(this);
            informationController.setModel(model);
        } else {
            logError("InformationController is null");
        }
        if (armyController != null) {
            armyController.setMain(this);
            armyController.setModel(model);
            armyController.characterController = characterController;
            siegeController = armyController.getSiegeController();
        } else {
            logError("armyController is null");
        }
        if (overviewController != null) {
            overviewController.setMain(this);
            overviewController.setModel(model);
            overviewController.setPlayer(Model.getPlayerAsPerson());
        } else {
            logError("overviewController is null");
        }
        if (siegeController != null) {
            siegeController.setMain(this);
            siegeController.setModel(model);
            siegeController.setArmyController(armyController);
        } else {
            logError("siegeController is null");
        }
        if (warController != null) {
            warController.setMain(this);
            warController.setModel(model);
        } else {
            logError("warController is null");
        }
    }

    @Override
    public void initialize() {
        try {
            Platform.runLater(() -> clickMeButton.requestFocus());
            Platform.runLater(this::generateStartingMessage);
            Platform.runLater(() -> {
                if (exploreMapController != null) {
                    exploreMapController.updateExploreTab();
                }
            });
            Platform.runLater(() -> {
                if (clickerShopController != null) {
                    clickerShopController.updateClickerShopPrices();
                }
            });
            Platform.runLater(() -> {
                if (characterController != null) {
                    characterController.setCurrentCharacter(model.getPlayerCharacter());
                }
            });

            Platform.runLater(() -> {
                if (characterController != null) {
                    createTraitButtons();
                }
            });

            Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> update()));
            updateTimeline.setCycleCount(Timeline.INDEFINITE);
            updateTimeline.play();

            super.initialize();

            PopUpMessageTracker.messageProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    if(popUps.isSelected()) return;
                    openPopUp();
                }
            });

            PopUpMessageTracker.gameOverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    openPopUp();
                    closePopUpBtn.setDisable(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void logError(String message) {
        System.err.println("[MainController] ERROR: " + message);
    }

    @FXML
    void getPlayer(ActionEvent event) {
        Reset();
    }


    public static MainController getInstance() {
        return instance;
    }

    public void Reset() {
        Platform.runLater(() -> {
            Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();

            if (characterController != null) {
                characterController.setCurrentCharacter(model.getPlayerCharacter());
                characterController.update();
            }
            model.accessCurrentView().setCurrentView(model.accessWorld().getSpawnQuarter());
            if (relationsController != null) {
                relationsController.resetEverything();
            }
            clickMeButton.requestFocus();
            if (relationsController != null) {
                relationsController.setCurrentCharacter(model.getPlayerAsCharacter());
            }

            setUpArmyTab();

            if (selectedTab == relationsTab) {
                mainTabPane.getSelectionModel().select(characterTab);
            }
        });
    }

    public void openExploreMapTab() {
        informationTabPane.getSelectionModel().select(exploreMapTab);
    }

    public void generateResourcesAction() {
        if (!model.accessTime().isSimulationRunning()) {
            if (model.accessTime().isManualSimulation()) {
                Time.incrementByClick();
                Clicker.getInstance().generateResources();
                if (topSectionController != null) {
                    topSectionController.updateWallet();
                    topSectionController.update();
                }
                if (workWalletController != null) {
                    workWalletController.update();
                }
            } else {
                model.getPlayerCharacter().getMessageTracker().addMessage(MessageTracker.Message("Error", "Simulation is paused. Cannot generate resources."));
                if (workWalletController != null) {
                    workWalletController.update();
                }
            }
        } else {
            Clicker.getInstance().generateResources();
            if (topSectionController != null) {
                topSectionController.updateWallet();
            }
            if (workWalletController != null) {
                workWalletController.update();
            }
        }
        update();
    }

    @FXML
    public void generateResources(MouseEvent event) {
        generateResourcesAction();
    }

    void generateStartingMessage() {
        triggerStartingMessage();
        MessageTracker tracker = model.getPlayerCharacter().getMessageTracker();
        tracker.addMessage(MessageTracker.Message("Major", "New Game Started"));

        Quarter spawn = model.accessWorld().getSpawnQuarter();
        tracker.addMessage(MessageTracker.Message("Major", "Starting District is: " + cleanName(spawn.toString())));
        tracker.addMessage(MessageTracker.Message("Major", "Starting City is: " + cleanName(spawn.getHigher().toString())));
        tracker.addMessage(MessageTracker.Message("Major", "Starting Province is: " + cleanName(spawn.getHigher().getHigher().toString())));
        tracker.addMessage(MessageTracker.Message("Major", "Starting Nation is: " + cleanName(spawn.getNation().toString())));
        tracker.addMessage(MessageTracker.Message("Major", "Starting Continent is: " + cleanName(spawn.getNation().getHigher().toString())));
    }

    private String cleanName(String name) {
        String pattern = " \\(.*?\\)";
        return name.replaceAll(pattern, "");
    }

    @Override
    public void update() {
        updatePauseBtnText();
        Platform.runLater(() -> {
            List<String> newEvents = model.getPlayerCharacter().getMessageTracker().getCombinedEvents();
            ObservableList<String> currentEvents = eventList.getItems();

            if (!currentEvents.equals(newEvents)) {
                ObservableList<String> updatedEvents = FXCollections.observableArrayList(newEvents);
                eventList.setItems(updatedEvents);

                if (!updatedEvents.isEmpty()) {
                    eventList.scrollTo(updatedEvents.size() - 1);
                }
            }
        });
    }

    @FXML
    void updateExchange() {
        if (exchangeController != null) {
            exchangeController.update();
        }
    }

    @FXML
    void setUpRelationsTab() {
        Model.getPlayerAsPerson().getRelationsManager().updateSets();
        if (relationsController != null) {
            relationsController.setCurrentCharacter(characterController.getCurrentCharacter());
        }
    }

    @FXML
    void setUpArmyTab() {
        if (armyController != null) {
            armyController.differentiatePlayer();
        }
    }

    @FXML
    void exploreMapOpen() {
        SpecialEventsManager.triggerExploreMapInfo();
    }

    @FXML
    void setUpPropertyTab() {
        if (propertyController != null) {
            propertyController.updatePropertyTab();
        }
    }

    @FXML
    void incrementByClick(ActionEvent event) {
        boolean isChecked = incrementClicker.isSelected();
        Time.setManualSimulation(isChecked);
        Time.stopSimulation();

        if (topSectionController != null) {
            topSectionController.stopTimeBtn.setDisable(true);
            topSectionController.startTimeBtn.setDisable(false);
        }
    }

    public void toggleSimulation() {
        if (getModel().accessTime().isSimulationRunning()) {
            Time.stopSimulation();
            if (topSectionController != null) {
                topSectionController.startTimeBtn.setDisable(false);
                topSectionController.stopTimeBtn.setDisable(true);
            }
            incrementClicker.setDisable(false);
            incrementClicker.setSelected(false);
        } else {
            Time.startSimulation();
            if (topSectionController != null) {
                topSectionController.startTimeBtn.setDisable(true);
                topSectionController.stopTimeBtn.setDisable(false);
            }
            incrementClicker.setDisable(true);
            incrementClicker.setSelected(false);
        }
        clickMeButton.requestFocus();
        Time.setManualSimulation(false);
    }

    @FXML
    void pauseTime(MouseEvent event) {
        toggleSimulation();
        updatePauseBtnText();
    }

    @FXML
    void closePopUp(ActionEvent event) {
        PopUpMessageTracker.resetMessage();
        popUpBox.setVisible(false);
        mainLayout.setDisable(false);
        mainLayout.setBlendMode(BlendMode.SRC_OVER);
        topSection.setDisable(false);
        clickMeButton.requestFocus();
        if (Time.isFirstDay) {
            clickMeButton.requestFocus();
            return;
        }
        if(timeRunning) { // If simulation is stopped, closing pop up should not start the time even if player has set the option.
            if (!pausePopBtn.isSelected()) {
                topSectionController.startTimeFunction();
            }
        }


        clickMeButton.requestFocus();
    }

    private boolean timeRunning = false; // If simulation is stopped, closing pop up should not start the time.

    private void openPopUp() {

        timeRunning = Time.isIsSimulationRunning();

        mainLayout.setDisable(true);
        if (topSectionController != null) {
            topSectionController.stopTimeFunction();
        }
        PopUpMessageTracker.PopUpMessage message = PopUpMessageTracker.getMessage();

        if (message != null) {
            popUpHeadline.setText(message.getHeadline());
            popUpMessage.setText(message.getMainText());
            closePopUpBtn.setText(message.getButtonText());

            if (message.getImagePath() != null && !message.getImagePath().isEmpty()) {
                Image image = new Image(message.getImagePath());
                popUpImage.setImage(image);
                popUpImage.setVisible(true);
                popUpImage.setManaged(true);
            } else {
                popUpImage.setImage(null);
                popUpImage.setVisible(false);
                popUpImage.setManaged(false);
            }

            popUpBox.setVisible(true);
            topSection.setDisable(true);
        }
    }

    void updatePauseBtnText() {
        if (Time.isIsSimulationRunning()) {
            pauseBtn.setText(" ▶ " + Time.getSpeed().toString() + " Speed");
        } else if (!getModel().accessTime().isSimulationRunning()) {
            pauseBtn.setText(" ⏸ Paused");
        }
    }

    @FXML
    void hideGenerateMessages(ActionEvent event) {
        boolean isChecked = generateMessages.isSelected();
        model.getPlayerCharacter().getMessageTracker().getPreferences().setShowClickerEvents(!isChecked);
    }

    @FXML
    void hideMinorMessages(ActionEvent event) {
        boolean isChecked = minorMessages.isSelected();
        model.getPlayerCharacter().getMessageTracker().getPreferences().setShowMinorEvents(!isChecked);
    }

    @FXML
    void hideErrorMessages(ActionEvent event) {
        boolean isChecked = errorMessages.isSelected();
        model.getPlayerCharacter().getMessageTracker().getPreferences().setShowErrorEvents(!isChecked);
    }

    @FXML
    void hideShopMessages(ActionEvent event) {
        boolean isChecked = shopMessages.isSelected();
        model.getPlayerCharacter().getMessageTracker().getPreferences().setShowShopEvents(!isChecked);
    }


    @FXML
    void pauseAfterPop(ActionEvent event) {
        pausePopBtn.setSelected(pausePopBtn.isSelected());
    }

    @FXML
    void hideUtilityMessages(ActionEvent event) {
        boolean isChecked = hideUtility.isSelected();
        model.getPlayerCharacter().getMessageTracker().getPreferences().setShowUtilityEvents(!isChecked);
    }
    @FXML
    void triggerAutoPlay(ActionEvent event) {
        boolean isChecked = autoPlay.isSelected();
        triggerAutoplayWarning();
        Model.getPlayerAsPerson().getAiEngine().setRunning(isChecked);
    }

    @FXML
    void openSettings(ActionEvent event) {
        settingsBox.setVisible(!settingsBox.isVisible());
    }

    @FXML
    void cheat(ActionEvent event) {
        TransferPackage transferPackage = new TransferPackage(10000, 10000, 100000);
        model.getPlayerPerson().getWallet().addResources(transferPackage);
    }
    @FXML
    CheckBox extraSpeed;
    @FXML
    void setFastSpeed(ActionEvent event) {
        if(extraSpeed.isSelected()){
            Time.fastSpeed = 1;
        }else{
            Time.fastSpeed = 200;
        }
        topSectionController.slowerMethod();
        topSectionController.fasterMethod();
    }


    public void updateCurrentlyViewing() {
        Character currentCharacter = characterController != null ? characterController.getCurrentCharacter() : null;
        Character playerCharacter = Model.getPlayerAsCharacter();

        if (currentCharacter != null && currentCharacter.equals(playerCharacter)) {
            currentlyViewing.setText("Currently Viewing Yourself");
        } else if (currentCharacter != null) {
            currentlyViewing.setText("Currently Viewing: " + currentCharacter);
        } else {
            currentlyViewing.setText("Currently Viewing: Unknown");
        }
    }

    public Button getClickMeButton() {
        return clickMeButton;
    }
}

