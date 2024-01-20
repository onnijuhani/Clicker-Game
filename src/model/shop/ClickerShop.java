package model.shop;

import model.characters.player.clicker.AlloyClicker;
import model.characters.player.clicker.Clicker;
import model.characters.player.clicker.ClickerTools;
import model.characters.player.clicker.GoldClicker;
import model.resourceManagement.resources.Resource;
import model.resourceManagement.wallets.Wallet;

public class ClickerShop {
    private int alloyClickerPrice = 100;
    private int goldClickerPrice = 1000;

    public ClickerShop() {
    }
    public void buyClicker(Resource type, Clicker clicker, Wallet wallet) {
        int price = getPrice(type);
        if (wallet.hasEnoughResource(Resource.Gold, price)) {
            wallet.subtractGold(price);
            ClickerTools newTool = createClickerTool(type);
            clicker.addClickerTool(type, newTool);
        } else {
            throw new IllegalArgumentException("Insufficient gold to buy clicker.");
        }
    }

    public int getPrice(Resource type) {
        switch (type) {
            case Alloy:
                return alloyClickerPrice;
            case Gold:
                return goldClickerPrice;
            default:
                throw new IllegalArgumentException("Clicker type not available for purchase.");
        }
    }

    public ClickerTools createClickerTool(Resource type) {
        switch (type) {
            case Alloy:
                return new AlloyClicker();
            case Gold:
                return new GoldClicker();
            default:
                throw new IllegalArgumentException("Invalid clicker type.");
        }
    }

    public void setAlloyClickerPrice(int price) {
        this.alloyClickerPrice = price;
    }
    public void setGoldClickerPrice(int price) {
        this.goldClickerPrice = price;
    }
}
