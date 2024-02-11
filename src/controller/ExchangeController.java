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

    @FXML
    private Label marketFee;
    private MainController main;


    @FXML
    public void initialize() {
    }

    public void updateMarketFee(){
        marketFee.setText("Fee: "+getExchange().getMarketFee()*100+"%");
    }

    @FXML
    void increasePrices(MouseEvent event) {
        getExchange().increaseDefaultPrices();
        updateExchangePrices();
    }

    @FXML
    void decreasePrices(MouseEvent event) {
        Exchange exchange = getExchange();
        if (exchange.getDefaultGold() > 1 && exchange.getDefaultFoodAlloys() > 1) {
            exchange.decreaseDefaultPrices();
        }
        updateExchangePrices();
    }

    private Exchange getExchange(){
        return model.accessPlayer().getNation().getShop().getExchange();
    }
    void updateExchangePrices(){
        int defaultGold = getExchange().getDefaultGold();

        String[] foodGold = getExchange().getExchangeCost(defaultGold,Resource.Gold,Resource.Food);
        foodToGoldPrice.setText(foodGold[1]);
        foodGoldBtn.setText(foodGold[0]);

        String[] alloyGold = getExchange().getExchangeCost(defaultGold,Resource.Gold,Resource.Alloy);
        alloyToGoldPrice.setText(alloyGold[1]);
        alloyGoldBtn.setText(alloyGold[0]+"s");



        int defaultFoodAlloys =getExchange().getDefaultFoodAlloys();

        String[] goldFood = getExchange().getExchangeCost(defaultFoodAlloys,Resource.Food,Resource.Gold);
        goldToFoodPrice.setText(goldFood[1]);
        goldFoodBtn.setText(goldFood[0]);

        String[] goldAlloy = getExchange().getExchangeCost(defaultFoodAlloys,Resource.Alloy,Resource.Gold);
        goldToAlloysPrice.setText(goldAlloy[1]+"s");
        goldAlloyBtn.setText(goldAlloy[0]);



    }
    @FXML
    void buyGoldFoodBtn(MouseEvent event) {
        int amountToBuy = getExchange().getDefaultGold();
        getExchange().exchangeResources(amountToBuy, Resource.Gold,Resource.Food,model.accessPlayer());
        main.updateEventList();
    }
    @FXML
    void buyGoldAlloysBtn(MouseEvent event) {
        int amountToBuy = getExchange().getDefaultGold();
        getExchange().exchangeResources(amountToBuy,Resource.Gold,Resource.Alloy,model.accessPlayer());
        main.updateEventList();
    }

    @FXML
    void buyAlloysGoldBtn(MouseEvent event) {
       int amountToBuy = getExchange().getDefaultFoodAlloys();
        getExchange().exchangeResources(amountToBuy,Resource.Alloy,Resource.Gold,model.accessPlayer());
        main.updateEventList();
    }

    @FXML
    void buyFoodGoldBtn(MouseEvent event) {
        int amountToBuy = getExchange().getDefaultFoodAlloys();
        getExchange().exchangeResources(amountToBuy,Resource.Food,Resource.Gold,model.accessPlayer());
        main.updateEventList();
    }


    public void setMain(MainController main) {
        this.main = main;
    }
}