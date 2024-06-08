package controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.Model;
import model.characters.Person;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.shop.Exchange;
import model.stateSystem.SpecialEventsManager;
import model.time.MonthlyTradeExecutor;
import model.time.Time;

public class ExchangeController extends BaseController implements MonthlyTradeExecutor {

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

    @FXML
    private CheckBox monthlyAlloys;

    @FXML
    private CheckBox monthlyFood;

    @FXML
    private CheckBox monthlyGoldAlloys;

    @FXML
    private CheckBox monthlyGoldFood;





    @FXML
    public void initialize() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateExchange()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
        Time.setTradeExecutor(this);
    }

    void updateExchange(){
        updateShopWallet();
        updateMarketFee();
        updateExchangePrices();
    }

    @FXML
    void triggerInfo(ActionEvent event) {
        SpecialEventsManager.triggerMarketInfo();
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

    private static Exchange getExchange(){
        return Model.getPlayerAsCharacter().getRole().getNation().getShop().getExchange();
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

    @Override
    public void executeMonthlyTrades(){

        Person person = Model.getPlayerAsPerson();

        TransferPackage netCash = person.getPaymentManager().getNetCash();

        int minimum = Time.getYear() * 10;

        if(monthlyFood.isSelected()){
            int amountSell = netCash.food() - minimum;
            getExchange().sellResource(amountSell, Resource.Food, person.getCharacter());
        }
        if(monthlyAlloys.isSelected()){
            int amountSell = netCash.alloy() - minimum;
            getExchange().sellResource(amountSell, Resource.Alloy, person.getCharacter());
        }

        int amountSpendGold = netCash.gold();

        if(!(netCash.gold() > person.getWallet().getGold() * 1.5)){
            return; // return if there isn't a lot of gold available.
        }

        if(monthlyGoldFood.isSelected()){
            int amountSpend = amountSpendGold / (monthlyGoldAlloys.isSelected() ? 2 : 1);
            int amountBuy = amountSpend * 10;
            getExchange().exchangeResources(amountBuy, Resource.Food, Resource.Gold, person.getCharacter());

        }
        if(monthlyGoldAlloys.isSelected()){
            int amountSpend = amountSpendGold / (monthlyGoldFood.isSelected() ? 2 : 1);
            int amountBuy = amountSpend * 5;
            getExchange().exchangeResources(amountBuy, Resource.Alloy, Resource.Gold, person.getCharacter());
        }


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