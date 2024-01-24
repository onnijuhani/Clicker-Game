package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.Model;
import model.characters.player.EventTracker;
import model.resourceManagement.resources.Resource;
import model.worldCreation.Area;

import java.util.List;

import static model.NameCreation.generateWorldName;

public class Control {
    private Model model = new Model();
    @FXML
    private ListView<String> eventList;
    @FXML
    private Label foodLabel;
    @FXML
    private Label alloysLabel;
    @FXML
    private Label goldLabel;
    @FXML
    private Label timeView;
    @FXML
    private ImageView homeImageView;
    private Timeline updateTimeline;

    @FXML
    private Button buyAlloyClickerButton;
    @FXML
    private Button buyGoldClickerButton;

    @FXML
    private Label goldToAlloysPrice;
    @FXML
    private Label goldToFoodPrice;
    @FXML
    private Label foodToGoldPrice;
    @FXML
    private Label alloysToGoldPrice;
    @FXML
    private ListView<Button> areasList;
    @FXML
    private Label currentViewLabel;
    @FXML
    private Button higherViewButton;
    @FXML
    private Button clickMeButton;

    private int lastEventCount = 0;

    public Control(){
    }
    @FXML
    public void initialize() {
        Platform.runLater(() -> clickMeButton.requestFocus());

        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> updateUI()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);


        updateClickerShopPrices();
        updateUI();
    }

    private void updateClickerShopPrices() {
        int alloyPrice = model.accessShop().getClickerShop().getPrice(Resource.Alloy);
        int goldPrice = model.accessShop().getClickerShop().getPrice(Resource.Gold);
        buyAlloyClickerButton.setText(alloyPrice + " Gold");
        buyGoldClickerButton.setText(goldPrice + " Gold");
    }

    void updateExchangePrices(){
        int defaultGold = model.accessShop().getExchange().getDefaultGold();
        foodToGoldPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultGold,Resource.Gold,Resource.Food));
        alloysToGoldPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultGold,Resource.Gold,Resource.Alloy));

        int defaultFoodAlloys = model.accessShop().getExchange().getDefaultFoodAlloys();
        goldToFoodPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultFoodAlloys,Resource.Food,Resource.Gold));
        goldToAlloysPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultFoodAlloys,Resource.Alloy,Resource.Gold));
    }
    private void updateUI() {
        timeView.setText(model.accessTime().getClock());
        updateHomeImage();
        updateWallet();
        updateEventList();
        updateExchangePrices();
    }
    @FXML
    void generateResources(MouseEvent event) {
        if (!model.accessTime().isSimulationRunning()) {
            model.accessPlayer().getEventTracker().addEvent(EventTracker.Message("Error","Simulation is paused. Cannot generate resources."));
            updateEventList();
            return;
        }

        model.accessPlayer().getClicker().generateResources();
        updateWallet();
        for (int i = 0; i < 100; i++) {
            System.out.println(generateWorldName());
        }
    }
    private void updateEventList() {
        List<String> events = model.accessPlayer().getEventTracker().getCombinedEvents();
        eventList.getItems().clear();
        for (String event : events) {
            eventList.getItems().add(event);
        }
        eventList.scrollTo(eventList.getItems().size() - 1);
    }

    void updateWallet(){
        int[] values = model.accessPlayer().getWallet().getWalletValues();
        foodLabel.setText(String.valueOf(values[0]));
        alloysLabel.setText(String.valueOf(values[1]));
        goldLabel.setText(String.valueOf(values[2]));
    }

    public void updateHomeImage() {
        Image newImage = model.accessPlayer().getProperty().getImage();
        homeImageView.setImage(newImage);
    }

    @FXML
    void startTime(MouseEvent event) {
        model.accessTime().startSimulation();
        updateTimeline.play();
    }

    @FXML
    void stopTime(MouseEvent event) {
        model.accessTime().stopSimulation();
        updateTimeline.stop();
    }
    @FXML
    void buyAlloyClicker(MouseEvent event) {
        try {
            model.accessShop().getClickerShop().buyClicker(Resource.Alloy, model.accessPlayer());
            buyAlloyClickerButton.setDisable(true);
            buyAlloyClickerButton.setText("Owned!");
        } catch (IllegalArgumentException e) {
            model.accessPlayer().getEventTracker().addEvent(EventTracker.Message("Error","Not Enough Gold To Buy Alloy Clicker"));
        }
    }

    @FXML
    void buyGoldClicker(MouseEvent event) {
        try {
            model.accessShop().getClickerShop().buyClicker(Resource.Gold, model.accessPlayer());
            buyGoldClickerButton.setDisable(true);
            buyGoldClickerButton.setText("Owned!");
        } catch (IllegalArgumentException e) {
            model.accessPlayer().getEventTracker().addEvent(EventTracker.Message("Error","Not Enough Gold To Buy Gold Clicker"));
        }
    }
    @FXML
    void upgradeAlloyClicker(MouseEvent event) {

    }

    @FXML
    void upgradeFoodClicker(MouseEvent event) {

    }

    @FXML
    void upgradeGoldClicker(MouseEvent event) {

    }

    @FXML
    void increasePrices(MouseEvent event) {
        model.accessShop().getExchange().increaseDefaultPrices();
        updateExchangePrices();
    }
    @FXML
    void decreasePrices(MouseEvent event) {
        model.accessShop().getExchange().decreaseDefaultPrices();
        updateExchangePrices();
    }
//    @FXML
//    void buyGoldFoodBtn(MouseEvent event) {
//        double amountToBuy = model.accessShop().getExchange().getDefaultGold();
//        Wallet wallet = model.accessPlayer().getWallet();
//        model.accessShop().getExchange().exchangeResources(amountToBuy,Resource.Gold,Resource.Food,wallet);
//    }
//
//    @FXML
//    void buyAlloysGoldBtn(MouseEvent event) {
//        double amountToBuy = model.accessShop().getExchange().getDefaultFoodAlloys();
//        Wallet wallet = model.accessPlayer().getWallet();
//        model.accessShop().getExchange().exchangeResources(amountToBuy,Resource.Alloy,Resource.Gold,wallet);
//    }
//
//    @FXML
//    void buyFoodGoldBtn(MouseEvent event) {
//        double amountToBuy = model.accessShop().getExchange().getDefaultGold();
//        Wallet wallet = model.accessPlayer().getWallet();
//        model.accessShop().getExchange().exchangeResources(amountToBuy,Resource.Food,Resource.Gold,wallet);
//    }
//
//    @FXML
//    void buyGoldAlloysBtn(MouseEvent event) {
//        double amountToBuy = model.accessShop().getExchange().getDefaultGold();
//        Wallet wallet = model.accessPlayer().getWallet();
//        model.accessShop().getExchange().exchangeResources(amountToBuy,Resource.Gold,Resource.Alloy,wallet);
//    }

    @FXML
    void updateExploreTab(){
        updateAreasList();
        updateCurrentViewLabel();
        updateHigherButtonText();
    }
    void updateCurrentViewLabel(){
        this.currentViewLabel.setText(model.accessCurrentView().getCurrentView().toString() +" "+model.accessCurrentView().getCurrentView().getClass().getSimpleName());
    }
    @FXML
    void getHigherView() {
        model.accessCurrentView().setCurrentView(model.accessCurrentView().getCurrentView().getHigher());
        updateHigherButtonText();
        updateCurrentViewLabel();
        updateAreasList();
        checkAndDisableHigherViewButton(); // Check if the higher view button should be disabled
    }
    void updateHigherButtonText(){
        higherViewButton.setText(model.accessCurrentView().getCurrentView().toString());
        clickMeButton.requestFocus();
    }
    void updateAreasList() {
        areasList.getItems().clear(); // Clear the list before adding new buttons
        for (Area area : model.accessCurrentView().getContents()) {
            Button button = new Button(area.toString());
            button.setOnAction(event -> {
                model.accessCurrentView().setCurrentView(area);
                updateHigherButtonText();
                updateCurrentViewLabel();
                updateAreasList();
                checkAndDisableHigherViewButton(); // Check if the higher view button should be disabled
                clickMeButton.requestFocus();
            });
            areasList.getItems().add(button);
        }
    }
    void checkAndDisableHigherViewButton() {
        Area currentView = model.accessCurrentView().getCurrentView();
        Area higherView = currentView.getHigher();
        higherViewButton.setDisable(currentView.equals(higherView));
    }

}
