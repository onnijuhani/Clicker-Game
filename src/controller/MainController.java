package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.Model;
import model.characters.player.clicker.Clicker;
import model.stateSystem.EventTracker;
import model.time.Time;
import model.worldCreation.Quarter;

import java.util.List;

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
    protected CheckBox incrementClicker;
    @FXML
    protected Button pauseBtn;

    @FXML
    protected TabPane mainTabPane;
    @FXML
    protected Tab characterTab;



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
    }



    @Override
    public void initialize() {
        Platform.runLater(() -> clickMeButton.requestFocus());
        Platform.runLater(this::generateStartingMessage);

        Platform.runLater(() -> exploreMapController.updateExploreTab());
        Platform.runLater(() -> clickerShopController.updateClickerShopPrices());
        Platform.runLater(() -> characterController.setCurrentCharacter(model.getPlayerCharacter()));
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateEventList()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    @FXML
    void getPlayer(ActionEvent event) {
        Reset();
    }

    public void Reset() {
        characterController.setCurrentCharacter(model.getPlayerCharacter());
        characterController.updateCharacterTab();
        model.accessCurrentView().setCurrentView(model.accessWorld().getSpawnQuarter());
        relationsController.resetEverything();
        clickMeButton.requestFocus();
        relationsController.setCurrentCharacter(Model.getPlayerAsCharacter());
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
        List<String> newEvents = model.getPlayerCharacter().getEventTracker().getCombinedEvents();
        // Only add new events that are not already in the list
        if (!eventList.getItems().equals(newEvents)) {
            eventList.getItems().setAll(newEvents);
            eventList.scrollTo(eventList.getItems().size() - 1);
        }
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


    void updatePauseBtnText(){
        if(getModel().accessTime().isSimulationRunning()){
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
    public Button getClickMeButton() {
        return clickMeButton;
    }
}
