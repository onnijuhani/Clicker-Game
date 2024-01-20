import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.List;

public class Control {
    private Model model = new Model();
    @FXML
    private ListView<String> eventList;
    @FXML
    private Label foodLabel; // Changed to Label
    @FXML
    private Label alloysLabel; // Changed to Label
    @FXML
    private Label goldLabel; // Changed to Label
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

    private int lastEventCount = 0;

    public Control(){
    }
    @FXML
    public void initialize() {
        // Initialize the timeline for updating the UI
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
        double defaultGold = model.accessShop().getExchange().getDefaultGold();
        foodToGoldPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultGold,Resource.Gold,Resource.Food));
        alloysToGoldPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultGold,Resource.Gold,Resource.Alloy));

        double defaultFoodAlloys = model.accessShop().getExchange().getDefaultFoodAlloys();
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
            model.accessPlayer().getEventTracker().addEvent("Simulation is paused. Cannot generate resources.");
            updateEventList();
            return;
        }

        model.accessPlayer().getClicker().generateResources();
        updateWallet();
    }
    private void updateEventList() {
        List<String> events = model.accessPlayer().getEventTracker().getEvents();
        if (events.size() > lastEventCount) {
            for (int i = lastEventCount; i < events.size(); i++) {
                String event = events.get(i);
                eventList.getItems().add(event);
            }
            eventList.scrollTo(eventList.getItems().size() - 1);
            lastEventCount = events.size();
        }
    }

    void updateWallet(){
        double[] values = model.accessPlayer().getWallet().getWalletValues();
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
            model.accessShop().getClickerShop().buyClicker(Resource.Alloy, model.accessPlayer().getClicker(), model.accessPlayer().getWallet());
            buyAlloyClickerButton.setDisable(true);
            buyAlloyClickerButton.setText("Owned!");
        } catch (IllegalArgumentException e) {
            model.accessPlayer().getEventTracker().addEvent("Not Enough Gold To Buy Alloy Clicker");
        }
    }

    @FXML
    void buyGoldClicker(MouseEvent event) {
        try {
            model.accessShop().getClickerShop().buyClicker(Resource.Gold, model.accessPlayer().getClicker(), model.accessPlayer().getWallet());
            buyGoldClickerButton.setDisable(true);
            buyGoldClickerButton.setText("Owned!");
        } catch (IllegalArgumentException e) {
            model.accessPlayer().getEventTracker().addEvent("Not Enough Gold To Buy Gold Clicker");
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
    @FXML
    void buyGoldFoodBtn(MouseEvent event) {
        double amountToBuy = model.accessShop().getExchange().getDefaultGold();
        Wallet wallet = model.accessPlayer().getWallet();
        model.accessShop().getExchange().exchangeResources(amountToBuy,Resource.Gold,Resource.Food,wallet);
    }

    @FXML
    void buyAlloysGoldBtn(MouseEvent event) {

    }

    @FXML
    void buyFoodGoldBtn(MouseEvent event) {

    }

    @FXML
    void buyGoldAlloysBtn(MouseEvent event) {

    }

}
