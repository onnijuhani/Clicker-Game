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
import model.characters.Status;
import model.characters.npc.Farmer;
import model.characters.npc.Merchant;
import model.characters.npc.Miner;
import model.characters.npc.Noble;
import model.characters.player.EventTracker;

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
    private ExploreMapController exploreMapController;

    @FXML
    private Button clickMeButton;
    @FXML
    private ListView<String> eventList;
    private Timeline updateTimeline;

    @FXML
    private CheckBox generateMessages;

    public MainController() {
        super();
    }
    @Override
    public void setModel(Model model) {
        super.setModel(model);
        if (topSectionController != null) {
            topSectionController.setModel(model);
        } else {
            System.out.println("TopSectionController is null");
        }
        if (clickerShopController != null) {
            clickerShopController.setModel(model);
            clickerShopController.updateClickerShopPrices();
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
            exploreMapController.updateExploreTab();
        } else {
            System.out.println("ExchangeController is null");
        }
    }

    @Override
    public void initialize() {
        Platform.runLater(() -> clickMeButton.requestFocus());
        Platform.runLater(() -> generateStartingMessage());
        Platform.runLater(() -> updateEventList());
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateEventList()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }


    private TopSectionController getTopSectionController() {
        return topSectionController;
    }

    @FXML
    void generateResources(MouseEvent event) {
        if (model.accessPlayer().getStatus() == Status.Peasant) {
            if (!model.accessTime().isSimulationRunning()) {
                model.accessPlayer().getEventTracker().addEvent(EventTracker.Message("Error", "Simulation is paused. Cannot generate resources."));
                updateEventList();
                workWalletController.updateWorkWallet();
                return;
            }
            model.accessPlayer().getClicker().generateResources();
            topSectionController.updateWallet();
            updateEventList();
            workWalletController.updateWorkWallet();
        } else {
            if (!model.accessTime().isSimulationRunning()) {
                model.accessPlayer().getEventTracker().addEvent(EventTracker.Message("Error", "Simulation is paused. Cannot generate resources."));
                updateEventList();
                return;
            }
            model.accessPlayer().getClicker().generateResources();
            topSectionController.updateWallet();
            updateEventList();
        }
    }
   void generateStartingMessage(){
        model.accessPlayer().getEventTracker().addEvent(EventTracker.Message("Major","New Game Started"));
       System.out.println(Farmer.totalAmount);
       System.out.println(Noble.totalAmount);
       System.out.println(Merchant.totalAmount);
       System.out.println(Miner.totalAmount);
    }

    public void updateEventList() {
        List<String> events = model.accessPlayer().getEventTracker().getCombinedEvents();
        eventList.getItems().clear();
        for (String event : events) {
            eventList.getItems().add(event);
        }
        eventList.scrollTo(eventList.getItems().size() - 1);
    }

    @FXML
    void updateExchange(){
        exchangeController.updateExchangePrices();
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
