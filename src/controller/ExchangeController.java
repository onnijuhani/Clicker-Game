package controller;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import model.resourceManagement.Resource;
import model.shop.Exchange;

public class ExchangeController extends BaseController {

    @FXML
    private Label alloyToGoldPrice;

    @FXML
    private Label foodToGoldPrice;

    @FXML
    private Label goldToAlloysPrice;

    @FXML
    private Label goldToFoodPrice;


    @FXML
    private Button foodGoldBtn;
    @FXML
    private Button alloyGoldBtn;
    @FXML
    private Button goldAlloyBtn;
    @FXML
    private Button goldFoodBtn;


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
        Exchange exchange = model.accessShop().getExchange();
        if (exchange.getDefaultGold() > 1 && exchange.getDefaultFoodAlloys() > 1) {
            exchange.decreaseDefaultPrices();
        }
        updateExchangePrices();
    }
    void updateExchangePrices(){
        int defaultGold = model.accessShop().getExchange().getDefaultGold();

        String[] foodGold = model.accessShop().getExchange().getExchangeCost(defaultGold,Resource.Gold,Resource.Food);
        foodToGoldPrice.setText(foodGold[1]);
        foodGoldBtn.setText(foodGold[0]);

        String[] alloyGold = model.accessShop().getExchange().getExchangeCost(defaultGold,Resource.Gold,Resource.Alloy);
        alloyToGoldPrice.setText(alloyGold[1]);
        alloyGoldBtn.setText(alloyGold[0]+"s");



        int defaultFoodAlloys = model.accessShop().getExchange().getDefaultFoodAlloys();

        String[] goldFood = model.accessShop().getExchange().getExchangeCost(defaultFoodAlloys,Resource.Food,Resource.Gold);
        goldToFoodPrice.setText(goldFood[1]);
        goldFoodBtn.setText(goldFood[0]);

        String[] goldAlloy = model.accessShop().getExchange().getExchangeCost(defaultFoodAlloys,Resource.Alloy,Resource.Gold);
        goldToAlloysPrice.setText(goldAlloy[1]+"s");
        goldAlloyBtn.setText(goldAlloy[0]);



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
    public void setMain(MainController main) {
        this.main = main;
    }
}