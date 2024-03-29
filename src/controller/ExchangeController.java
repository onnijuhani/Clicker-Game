package controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
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
    @FXML
    private Label shopWalletBalance;

    private MainController main;


    @FXML
    public void initialize() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateExchange()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    void updateExchange(){
        updateShopWallet();
        updateMarketFee();
        updateExchangePrices();
    }



    @FXML
    void increasePrices(MouseEvent event) {
        getExchange().increaseDefaultPrices();
        updateExchange();
    }

    @FXML
    void decreasePrices(MouseEvent event) {
        Exchange exchange = getExchange();
        if (exchange.getDefaultGold() > 1 && exchange.getDefaultFood() > 1) {
            exchange.decreaseDefaultPrices();
        }
        updateExchange();
    }

    private Exchange getExchange(){
        return model.getPlayerCharacter().getRole().getNation().getShop().getExchange();
    }
    void updateExchangePrices(){
        int defaultGold = getExchange().getDefaultGold();

        String[] foodGold = getExchange().getExchangeCost(defaultGold,Resource.Gold,Resource.Food);
        foodToGoldPrice.setText(foodGold[1]);
        foodGoldBtn.setText(foodGold[0]);

        String[] alloyGold = getExchange().getExchangeCost(defaultGold,Resource.Gold,Resource.Alloy);
        alloyToGoldPrice.setText(alloyGold[1]);
        alloyGoldBtn.setText(alloyGold[0]+"s");

        int defaultFood =getExchange().getDefaultFood();
        int defaultAlloys =getExchange().getDefaultAlloys();

        String[] goldFood = getExchange().getExchangeCost(defaultFood,Resource.Food,Resource.Gold);
        goldToFoodPrice.setText(goldFood[1]);
        goldFoodBtn.setText(goldFood[0]);

        String[] goldAlloy = getExchange().getExchangeCost(defaultAlloys,Resource.Alloy,Resource.Gold);
        goldToAlloysPrice.setText(goldAlloy[1]+"s");
        goldAlloyBtn.setText(goldAlloy[0]);
    }
    @FXML
    void buyGoldFoodBtn(MouseEvent event) {
        int amountToBuy = getExchange().getDefaultGold();
        getExchange().exchangeResources(amountToBuy, Resource.Gold,Resource.Food,model.getPlayerCharacter());
        updateExchange();
    }
    @FXML
    void buyGoldAlloysBtn(MouseEvent event) {
        int amountToBuy = getExchange().getDefaultGold();
        getExchange().exchangeResources(amountToBuy,Resource.Gold,Resource.Alloy,model.getPlayerCharacter());
        updateExchange();
    }

    @FXML
    void buyAlloysGoldBtn(MouseEvent event) {
       int amountToBuy = getExchange().getDefaultAlloys();
        getExchange().exchangeResources(amountToBuy,Resource.Alloy,Resource.Gold,model.getPlayerCharacter());
        updateExchange();
    }

    @FXML
    void buyFoodGoldBtn(MouseEvent event) {
        int amountToBuy = getExchange().getDefaultFood();
        getExchange().exchangeResources(amountToBuy,Resource.Food,Resource.Gold,model.getPlayerCharacter());
        updateExchange();
    }

    public void updateMarketFee(){
        marketFee.setText("Fee: "+getExchange().getMarketFee()*100+"%");
    }
    public void updateShopWallet(){
        shopWalletBalance.setText(getExchange().getWallet().getRatioString());
    }

    public void setMain(MainController main) {
        this.main = main;
    }
}