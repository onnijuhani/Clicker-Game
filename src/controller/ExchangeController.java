package controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import model.resourceManagement.resources.Resource;

public class ExchangeController extends BaseController {

    @FXML
    private Label alloysToGoldPrice;

    @FXML
    private Label foodToGoldPrice;

    @FXML
    private Label goldToAlloysPrice;

    @FXML
    private Label goldToFoodPrice;

    public void setMain(MainController main) {
        this.main = main;
    }
    private MainController main;

    @FXML
    public void initialize() {

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
    void updateExchangePrices(){
        int defaultGold = model.accessShop().getExchange().getDefaultGold();
        foodToGoldPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultGold,Resource.Gold,Resource.Food));
        alloysToGoldPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultGold,Resource.Gold,Resource.Alloy));

        int defaultFoodAlloys = model.accessShop().getExchange().getDefaultFoodAlloys();
        goldToFoodPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultFoodAlloys,Resource.Food,Resource.Gold));
        goldToAlloysPrice.setText(model.accessShop().getExchange().getExchangeCostString(defaultFoodAlloys,Resource.Alloy,Resource.Gold));
    }
    @FXML
    void buyGoldFoodBtn(MouseEvent event) {
        int amountToBuy = model.accessShop().getExchange().getDefaultGold();
        model.accessShop().getExchange().exchangeResources(amountToBuy, Resource.Gold,Resource.Food,model.accessPlayer());
        main.updateEventList();
    }

    @FXML
    void buyAlloysGoldBtn(MouseEvent event) {
       int amountToBuy = model.accessShop().getExchange().getDefaultFoodAlloys();
        model.accessShop().getExchange().exchangeResources(amountToBuy,Resource.Alloy,Resource.Gold,model.accessPlayer());
        main.updateEventList();
    }

    @FXML
    void buyFoodGoldBtn(MouseEvent event) {
        int amountToBuy = model.accessShop().getExchange().getDefaultGold();
        model.accessShop().getExchange().exchangeResources(amountToBuy,Resource.Food,Resource.Gold,model.accessPlayer());
        main.updateEventList();
    }

    @FXML
    void buyGoldAlloysBtn(MouseEvent event) {
        int amountToBuy = model.accessShop().getExchange().getDefaultGold();
        model.accessShop().getExchange().exchangeResources(amountToBuy,Resource.Gold,Resource.Alloy,model.accessPlayer());
        main.updateEventList();
    }

}