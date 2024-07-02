package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import model.Model;
import model.characters.Person;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
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
        try {
            super.initialize();
            Time.setTradeExecutor(this);
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
    @Override
    public void update(){
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
        update();
    }

    @FXML
    void decreasePrices(MouseEvent event) {
        Exchange exchange = getExchange();
        if (exchange.getDefaultGold() > 1 && exchange.getDefaultFood() > 1) {
            exchange.decreaseDefaultPrices();
        }
        update();
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

        TransferPackage netBalance = person.getPaymentManager().getNetBalance();
        TransferPackage netCash = person.getPaymentManager().getNetCash();
        TransferPackage expenses = person.getPaymentManager().getFullExpense();
        TransferPackage nextExpense = person.getPaymentManager().getNextExpense().getAmount();

        int minimum = Time.getYear() * 10;

        Wallet wallet = person.getWallet();




        if(monthlyFood.isSelected()){
            if(!(netCash.food() < 0) && !(expenses.food() * 2 > wallet.getFood())){
                int amountSell = netCash.food() - minimum;
                if((wallet.getFood() - amountSell > nextExpense.food())) {
                    getExchange().sellResource(amountSell, Resource.Food, person.getCharacter());
                }
            }

        }

        if(monthlyAlloys.isSelected()){
            if(!(netCash.alloy() < 0) && !(expenses.alloy() * 2 > wallet.getAlloy())){
                int amountSell = netCash.alloy() - minimum;
                if((wallet.getAlloy() - amountSell > nextExpense.alloy())) {
                    getExchange().sellResource(amountSell, Resource.Alloy, person.getCharacter());
                }
            }

        }

        int amountGold = person.getWallet().getGold();

        if(amountGold < expenses.gold() * 3){
            System.out.println(1);
            return; // return if there isn't enough gold
        }
        if(amountGold < 2){
            System.out.println(2);
            return; // return if there is less than 2 gold
        }
        if(netCash.gold() * 2 < 0){
            System.out.println(3);
            return; // return if net cash is negative.
        }

        int amountSpendGold = netCash.gold();

        if(amountSpendGold < 2){
            System.out.println(4);
            return; // return if amount spend is very low
        }

        amountSpendGold = amountSpendGold / 4;


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
        update();
    }
    @FXML
    void buyGoldAlloysBtn(MouseEvent event) {
        int amountToBuy = getExchange().getDefaultGold();
        getExchange().exchangeResources(amountToBuy,Resource.Gold,Resource.Alloy,model.getPlayerCharacter());
        update();
    }

    @FXML
    void buyAlloysGoldBtn(MouseEvent event) {
       int amountToBuy = getExchange().getDefaultAlloys();
        getExchange().exchangeResources(amountToBuy,Resource.Alloy,Resource.Gold,model.getPlayerCharacter());
        update();
    }

    @FXML
    void buyFoodGoldBtn(MouseEvent event) {
        int amountToBuy = getExchange().getDefaultFood();
        getExchange().exchangeResources(amountToBuy,Resource.Food,Resource.Gold,model.getPlayerCharacter());
        update();
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