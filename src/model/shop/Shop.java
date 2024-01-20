package model.shop;

public class Shop {
    private Exchange exchange;
    private ClickerShop clickerShop;
    public Shop(){
        this.exchange = new Exchange();
        this.clickerShop = new ClickerShop();

    }
    public Exchange getExchange() {
        return exchange;
    }
    public ClickerShop getClickerShop() {
        return clickerShop;
    }
}





