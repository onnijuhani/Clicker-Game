package model.shop;

public class Shop {
    private Exchange exchange;
    private ClickerShop clickerShop;
    private UtilityShop utilityShop;
    public Shop(){
        this.exchange = new Exchange();
        this.clickerShop = new ClickerShop();
        this.utilityShop = new UtilityShop();
    }
    public Exchange getExchange() {
        return exchange;
    }
    public ClickerShop getClickerShop() {
        return clickerShop;
    }
    public UtilityShop getUtilityShop() {
        return utilityShop;
    }
}





