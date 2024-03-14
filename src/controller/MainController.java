package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.Model;
import model.stateSystem.EventTracker;
import model.time.Time;
import model.worldCreation.Quarter;

import java.util.ArrayList;
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


    @FXML
    protected Button clickMeButton;
    @FXML
    private ListView<String> eventList;
    private Timeline updateTimeline;

    @FXML
    private CheckBox generateMessages;
    @FXML
    protected CheckBox incrementClicker;
    @FXML
    protected Button pauseBtn;

    private List<String> lastEvents = new ArrayList<>();

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
            workWalletController.updateWorkWallet();
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
            characterController.setCurrentCharacter(model.accessPlayer());
            characterController.updateCharacterTab();
            propertyController.updatePropertyTab();
            clickerShopController.setShop(model.accessPlayer().getNation().getShop());
        } else {
            System.out.println("CharacterController is null");
        }
    }



    @Override
    public void initialize() {
        Platform.runLater(() -> clickMeButton.requestFocus());
        Platform.runLater(() -> generateStartingMessage());
        Platform.runLater(() -> updateEventList());

        Platform.runLater(() -> exploreMapController.updateExploreTab());
        Platform.runLater(() -> clickerShopController.updateClickerShopPrices());
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateEventList()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    @FXML
    void getPlayer(ActionEvent event) {
        characterController.setCurrentCharacter(model.accessPlayer());
        characterController.updateCharacterTab();
        model.accessCurrentView().setCurrentView(model.accessWorld().getSpawnQuarter());
        exploreMapController.updateExploreTab();
    }


    private TopSectionController getTopSectionController() {
        return topSectionController;
    }

    // New method without MouseEvent parameter
    public void generateResourcesAction() {
        if (!model.accessTime().isSimulationRunning()) {
            if (model.accessTime().isManualSimulation()) {
                model.accessTime().incrementByClick();
                model.accessPlayer().getClicker().generateResources();
                topSectionController.updateWallet();
                topSectionController.updateTopSection();
                workWalletController.updateWorkWallet();
            } else {
                model.accessPlayer().getEventTracker().addEvent(EventTracker.Message("Error", "Simulation is paused. Cannot generate resources."));
                updateEventList();
                workWalletController.updateWorkWallet();
            }
        } else {
            model.accessPlayer().getClicker().generateResources();
            topSectionController.updateWallet();
            updateEventList();
            workWalletController.updateWorkWallet();
        }
    }

    @FXML
    public void generateResources(MouseEvent event) {
        generateResourcesAction();
    }


   void generateStartingMessage(){
        EventTracker tracker = model.accessPlayer().getEventTracker();
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
        String cleanName = name.replaceAll(pattern, "");
        return cleanName;
    }

    public void updateEventList() {
        updatePauseBtnText();
        List<String> events = model.accessPlayer().getEventTracker().getCombinedEvents();

        boolean newMessagesAdded = eventList.getItems().size() != events.size();

        // How many messages should be shown
        int start = Math.max(0, events.size() - 500);


        eventList.getItems().clear();
        for (int i = start; i < events.size(); i++) {
            eventList.getItems().add(events.get(i));
        }

        // Auto-scroll to the last message only if new messages have been added
        if (newMessagesAdded) {
            eventList.scrollTo(eventList.getItems().size() - 1);
        }

    }

    @FXML
    void updateExchange(){
        exchangeController.updateExchange();
    }

    @FXML
    void incrementByClick(ActionEvent event) {
        boolean isChecked = incrementClicker.isSelected();
        model.accessTime().setManualSimulation(isChecked);
        model.accessTime().incrementByClick();
        model.accessTime().stopSimulation();

        topSectionController.stopTimeBtn.setDisable(true); // Disable the stop button
        topSectionController.startTimeBtn.setDisable(false); // Enable the start button


    }
    public void toggleSimulation() {
        if(getModel().accessTime().isSimulationRunning()) {
            model.accessTime().stopSimulation();
            topSectionController.startTimeBtn.setDisable(false);
            topSectionController.stopTimeBtn.setDisable(true);
            incrementClicker.setDisable(false);
            incrementClicker.setSelected(false);
        } else {
            model.accessTime().startSimulation();
            topSectionController.startTimeBtn.setDisable(true);
            topSectionController.stopTimeBtn.setDisable(false);
            incrementClicker.setDisable(true);
            incrementClicker.setSelected(false);
        }
        clickMeButton.requestFocus();
        model.accessTime().setManualSimulation(false);
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
        model.accessPlayer().getEventTracker().getPreferences().setShowResourceEvents(!isChecked);
        updateEventList();
    }
    public Button getClickMeButton() {
        return clickMeButton;
    }
}
