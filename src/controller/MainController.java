package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import model.characters.player.clicker.Clicker;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;
import model.stateSystem.PopUpMessageTracker;
import model.stateSystem.SpecialEventsManager;
import model.time.Time;
import model.worldCreation.Quarter;

import java.util.HashSet;
import java.util.List;

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
    @FXML
    private CharacterController characterController;
    @FXML
    private PropertyController propertyController;
    @FXML
    private InformationController informationController;
    @FXML
    private ArmyController armyController;
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





    boolean autoContinue = false;

    public MainController() {
        super();
    }


    @Override
    public void setModel(Model model) {
        super.setModel(model);
        if (topSectionController != null) {
            topSectionController.setModel(model);
            topSectionController.setMain(this);
        } else {
            System.out.println("TopSectionController is null");
        }
        if (clickerShopController != null) {
            clickerShopController.setModel(model);
        } else {
            System.out.println("ClickerShopController is null");
        }
        if (exchangeController != null) {
            exchangeController.setModel(model);
            exchangeController.setMain(this);
        } else {
            System.out.println("ExchangeController is null");
        }
        if (workWalletController != null) {
            workWalletController.setModel(model);
        } else {
            System.out.println("WorkWalletController is null");
        }
        if (exploreMapController != null) {
            exploreMapController.setModel(model);
            exploreMapController.setMain(this);
            exploreMapController.setCharacterController(characterController);
        } else {
            System.out.println("ExploreController is null");
        }
        if (characterController != null) {
            characterController.setModel(model);
            characterController.setMain(this);
            characterController.setPropertyController(propertyController);
        } else {
            System.out.println("CharacterController is null");
        }
        if (propertyController != null) {
            propertyController.setModel(model);
            propertyController.setMain(this);
            propertyController.setCharacterController(characterController);
        } else {
            System.out.println("PropertyController is null");
        }
        if (relationsController != null) {
            relationsController.setMain(this);
            relationsController.setCharacterController(characterController);
            relationsController.setModel(model);
            relationsController.resetEverything();
        } else {
            System.out.println("RelationsController is null");
        }
        if (informationController != null) {
            informationController.setMain(this);
            informationController.setModel(model);
        }
        else {
            System.out.println("InformationController is null");
        }
        if (armyController != null) {
            armyController.setMain(this);
            armyController.setModel(model);
            armyController.characterController = characterController;
        }
        else {
            System.out.println("armyController is null");
        }
    }



    @Override
    public void initialize() {
        Platform.runLater(() -> clickMeButton.requestFocus());
        Platform.runLater(this::generateStartingMessage);

        Platform.runLater(() -> exploreMapController.updateExploreTab());
        Platform.runLater(() -> clickerShopController.updateClickerShopPrices());
        Platform.runLater(() -> characterController.setCurrentCharacter(model.getPlayerCharacter()));
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateEventList()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();

        PopUpMessageTracker.messageProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openPopUp();
            }
        });

        PopUpMessageTracker.gameOverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                openPopUp();
                closePopUpBtn.setDisable(true);
            }
        });
    }

    @FXML
    void getPlayer(ActionEvent event) {
        Reset();
    }

    public void Reset() {
        // Save the currently selected tab
        Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();

        // Perform the reset operations
        characterController.setCurrentCharacter(model.getPlayerCharacter());
        characterController.updateCharacterTab();
        model.accessCurrentView().setCurrentView(model.accessWorld().getSpawnQuarter());
        relationsController.resetEverything();
        clickMeButton.requestFocus();
        relationsController.setCurrentCharacter(Model.getPlayerAsCharacter());

        // Switch back to characterTab only if relationsTab was selected
        if (selectedTab == relationsTab) {
            mainTabPane.getSelectionModel().select(characterTab);
        }
    }

    public void openExploreMapTab(){
        informationTabPane.getSelectionModel().select(exploreMapTab);
    }


    public void generateResourcesAction() {
        if (!model.accessTime().isSimulationRunning()) {
            if (model.accessTime().isManualSimulation()) {
                Time.incrementByClick();
                Clicker.getInstance().generateResources();
                topSectionController.updateWallet();
                topSectionController.updateTopSection();
                workWalletController.updateWorkWallet();
            } else {
                model.getPlayerCharacter().getEventTracker().addEvent(EventTracker.Message("Error", "Simulation is paused. Cannot generate resources."));
                workWalletController.updateWorkWallet();
            }
        } else {
            Clicker.getInstance().generateResources();
            topSectionController.updateWallet();
            workWalletController.updateWorkWallet();
        }
        updateEventList();
    }

    @FXML
    public void generateResources(MouseEvent event) {
        generateResourcesAction();
    }


   void generateStartingMessage(){
        triggerStartingMessage();
        EventTracker tracker = model.getPlayerCharacter().getEventTracker();
        tracker.addEvent(EventTracker.Message("Major","New Game Started"));

        Quarter spawn = model.accessWorld().getSpawnQuarter();
        tracker.addEvent(EventTracker.Message("Major","Starting District is: "+cleanName(spawn.toString())));
        tracker.addEvent(EventTracker.Message("Major","Starting City is: "+cleanName(spawn.getHigher().toString())));
        tracker.addEvent(EventTracker.Message("Major","Starting Province is: "+cleanName(spawn.getHigher().getHigher().toString())));
        tracker.addEvent(EventTracker.Message("Major","Starting Nation is: "+cleanName(spawn.getNation().toString())));
        tracker.addEvent(EventTracker.Message("Major","Starting Continent is: "+cleanName(spawn.getNation().getHigher().toString())));

    }
    private String cleanName(String name) {
        String pattern = " \\(.*?\\)"; //regular expressions that should remove (Home) or (King) or both to display just the name
        return name.replaceAll(pattern, "");
    }


    public void updateEventList() {

        updatePauseBtnText();
        Platform.runLater(() -> {
            List<String> newEvents = model.getPlayerCharacter().getEventTracker().getCombinedEvents();
            ObservableList<String> currentEvents = eventList.getItems();

            // Check if the lists are different in size or content
            if (currentEvents.size() != newEvents.size() || !new HashSet<>(currentEvents).containsAll(newEvents)) {
                // Only update the items if there are changes
                currentEvents.setAll(newEvents);
                // Scroll to the last item
                eventList.scrollTo(currentEvents.size() - 1);
            }
        });
    }




    @FXML
    void updateExchange(){
        exchangeController.updateExchange();
    }

    @FXML
    void setUpRelationsTab() {
        Model.getPlayerAsPerson().getRelationsManager().updateSets();
        relationsController.setCurrentCharacter(characterController.getCurrentCharacter());
    }
    @FXML
    void setUpArmyTab() {
        armyController.setCurrentCharacter();
        armyController.differentiatePlayer();
    }

    @FXML
    void exploreMapOpen() {
        SpecialEventsManager.triggerExploreMapInfo();
    }
    @FXML
    void setUpPropertyTab() {
        propertyController.updatePropertyTab();
    }

    @FXML
    void incrementByClick(ActionEvent event) {
        boolean isChecked = incrementClicker.isSelected();
        Time.setManualSimulation(isChecked);
        Time.stopSimulation();

        topSectionController.stopTimeBtn.setDisable(true); // Disable the stop button
        topSectionController.startTimeBtn.setDisable(false); // Enable the start button

    }
    public void toggleSimulation() {
        if(getModel().accessTime().isSimulationRunning()) {
            Time.stopSimulation();
            topSectionController.startTimeBtn.setDisable(false);
            topSectionController.stopTimeBtn.setDisable(true);
            incrementClicker.setDisable(false);
            incrementClicker.setSelected(false);
        } else {
            Time.startSimulation();
            topSectionController.startTimeBtn.setDisable(true);
            topSectionController.stopTimeBtn.setDisable(false);
            incrementClicker.setDisable(true);
            incrementClicker.setSelected(false);
        }
        clickMeButton.requestFocus();
        Time.setManualSimulation(false);
    }

    @FXML
    void pauseTime(MouseEvent event) {
        toggleSimulation();
    }

    @FXML
    void closePopUp(ActionEvent event) {
        PopUpMessageTracker.resetMessage();
        popUpBox.setVisible(false);
        mainLayout.setDisable(false);
        mainLayout.setBlendMode(BlendMode.SRC_OVER);
        topSection.setDisable(false);
        clickMeButton.requestFocus();
        if(Time.isFirstDay){
            clickMeButton.requestFocus();
            return;
        }
        if(!pausePopBtn.isSelected()) {
            topSectionController.startTimeFuntion();
        }
        clickMeButton.requestFocus();
    }

    private void openPopUp() {
        mainLayout.setDisable(true);
        topSectionController.stopTimeFunction();
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


    void updatePauseBtnText(){
        if(Time.isIsSimulationRunning()){
            pauseBtn.setText(" ▶ " + Time.getSpeed().toString() + " Speed");
        } else if (!getModel().accessTime().isSimulationRunning()) {
            pauseBtn.setText(" ⏸ Paused");
        }
    }
    @FXML
    void hideGenerateMessages(ActionEvent event) {
        boolean isChecked = generateMessages.isSelected();
        model.getPlayerCharacter().getEventTracker().getPreferences().setShowClickerEvents(!isChecked);
    }
    @FXML
    void hideMinorMessages(ActionEvent event) {
        boolean isChecked = minorMessages.isSelected();
        model.getPlayerCharacter().getEventTracker().getPreferences().setShowMinorEvents(!isChecked);
    }
    @FXML
    void hideErrorMessages(ActionEvent event) {
        boolean isChecked = errorMessages.isSelected();
        model.getPlayerCharacter().getEventTracker().getPreferences().setShowErrorEvents(!isChecked);
    }
    @FXML
    void hideShopMessages(ActionEvent event) {
        boolean isChecked = shopMessages.isSelected();
        model.getPlayerCharacter().getEventTracker().getPreferences().setShowShopEvents(!isChecked);
    }

    @FXML
    void pauseAfterPop(ActionEvent event) {
        pausePopBtn.setSelected(pausePopBtn.isSelected());
    }

    @FXML
    void hideUtilityMessages(ActionEvent event) {
        boolean isChecked = hideUtility.isSelected();
        model.getPlayerCharacter().getEventTracker().getPreferences().setShowUtilityEvents(!isChecked);
    }

    @FXML
    void openSettings(ActionEvent event) {
        settingsBox.setVisible(!settingsBox.isVisible());
    }

    @FXML
    void cheat(ActionEvent event) {
        TransferPackage transferPackage = new TransferPackage(10000,10000, 100000);
        model.getPlayerPerson().getWallet().addResources(transferPackage);
    }

    @FXML
    void setFastSpeed(ActionEvent event) {
        Time.fastSpeed = 50;
    }



    public void updateCurrentlyViewing() {
        Character currentCharacter = characterController.getCurrentCharacter();
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
