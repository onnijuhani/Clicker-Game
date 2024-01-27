package model.shop;

public class Shop {
    private Exchange exchange;
    private ClickerShop clickerShop;
    private UtilityBuildingShop utilityBuildingShop;

    public Shop(){
        this.exchange = new Exchange();
        this.clickerShop = new ClickerShop();
        this.utilityBuildingShop = new UtilityBuildingShop();
    }
    public Exchange getExchange() {
        return exchange;
    }
    public ClickerShop getClickerShop() {
        return clickerShop;
    }
    public UtilityBuildingShop getUtilityBuildingShop() {
        return utilityBuildingShop;
    }



}





