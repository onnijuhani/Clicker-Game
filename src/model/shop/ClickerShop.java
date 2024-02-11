package model.shop;

import model.Settings;
import model.characters.player.EventTracker;
import model.characters.player.Player;
import model.characters.player.clicker.AlloyClicker;
import model.characters.player.clicker.Clicker;
import model.characters.player.clicker.ClickerTools;
import model.characters.player.clicker.GoldClicker;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;

public class ClickerShop extends ShopComponents {


    public ClickerShop(Wallet wallet) {
        super(wallet);
    }
    public boolean buyClicker(Resource type, Player player) {
        ClickerTools newTool = createClickerTool(type);
        int price = newTool.getUpgradePrice();  // Get the base price from the newTool
        TransferPackage transfer = new TransferPackage(0 ,0, price);

        if (player.getWallet().hasEnoughResource(Resource.Gold, price)) {
            this.wallet.deposit(player.getWallet(), transfer);
            player.getClicker().addClickerTool(type, newTool);
            player.getEventTracker().addEvent(EventTracker.Message("Shop", "Successfully purchased " + type + " Clicker!"));
            return true; // Purchase was successful
        } else {
            player.getEventTracker().addEvent(EventTracker.Message("Error", "Insufficient gold to buy " + type + " clicker."));
            return false; // Purchase failed
        }
    }



    public ClickerTools createClickerTool(Resource type) {
        switch (type) {
            case Alloy:
                return new AlloyClicker(Settings.get("alloyClicker"), Resource.Alloy);
            case Gold:
                return new GoldClicker(Settings.get("goldClicker"), Resource.Gold);
            case Food:
                return new GoldClicker(Settings.get("foodClicker"), Resource.Food);
            default:
                throw new IllegalArgumentException("Invalid clicker type.");
        }
    }

    public boolean buyUpgrade(Resource type, Player player) {
        Clicker clicker = player.getClicker();
        UpgradeSystem item = clicker.getClickerTool(type);
        int upgradePrice = item.getUpgradePrice();
        TransferPackage transfer = new TransferPackage(0 ,0, upgradePrice);

        if (player.getWallet().hasEnoughResource(Resource.Gold, upgradePrice)) {
            this.wallet.deposit(player.getWallet(), transfer);
            item.upgrade(); // Upgrade the item

            player.getEventTracker().addEvent(EventTracker.Message("Shop", "Successfully upgraded " + type + " Clicker to level " + item.getUpgradeLevel() + "!"));
            return true;
        } else {
            player.getEventTracker().addEvent(EventTracker.Message("Error", "Insufficient gold to upgrade " + type + " clicker to level " + (item.getUpgradeLevel() + 1) + "."));
            return false;
        }
    }


}
