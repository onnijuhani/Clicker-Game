package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.Model;
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
    private Button clickMeButton;
    @FXML
    private ListView<String> eventList;
    private Timeline updateTimeline;

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
    }
    @Override
    public void initialize() {
        Platform.runLater(() -> clickMeButton.requestFocus());
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.05), e -> updateUI()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
    }
    public void updateUI() {
        updateEventList();
    }

    private TopSectionController getTopSectionController() {
        return topSectionController;
    }

    @FXML
    void generateResources(MouseEvent event) {
        if (!model.accessTime().isSimulationRunning()) {
            model.accessPlayer().getEventTracker().addEvent(EventTracker.Message("Error","Simulation is paused. Cannot generate resources."));
            updateEventList();
            return;
        }
        model.accessPlayer().getClicker().generateResources();
        topSectionController.updateWallet();
        updateEventList();
        System.out.println(model.accessPlayer().getEventTracker().getResourceEvents());
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
}
