package model.shop;

import model.Settings;
import model.characters.Person;
import model.characters.player.clicker.AlloyClicker;
import model.characters.player.clicker.Clicker;
import model.characters.player.clicker.ClickerTools;
import model.characters.player.clicker.GoldClicker;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.EventTracker;

public class ClickerShop extends ShopComponents {


    public ClickerShop(Wallet wallet) {
        super(wallet);
    }
    public boolean buyClicker(Resource type, Person person) {
        ClickerTools newTool = createClickerTool(type);
        int price = newTool.getUpgradePrice();  // Get the base price from the newTool
        TransferPackage transfer = new TransferPackage(0 ,0, price);

        if (person.getWallet().hasEnoughResource(Resource.Gold, price)) {
            this.wallet.deposit(person.getWallet(), transfer);
            Clicker.getInstance().addClickerTool(type, newTool);
            person.getEventTracker().addEvent(EventTracker.Message("Shop", "Successfully purchased " + type + " Clicker!"));
            return true; // Purchase was successful
        } else {
            person.getEventTracker().addEvent(EventTracker.Message("Error", "Insufficient gold to buy " + type + " clicker."));
            return false; // Purchase failed
        }
    }



    public ClickerTools createClickerTool(Resource type) {
        return switch (type) {
            case Alloy -> new AlloyClicker(Settings.getInt("alloyClicker"), Resource.Alloy);
            case Gold -> new GoldClicker(Settings.getInt("goldClicker"), Resource.Gold);
            case Food -> new GoldClicker(Settings.getInt("foodClicker"), Resource.Food);
            default -> throw new IllegalArgumentException("Invalid clicker type.");
        };
    }

    public boolean buyUpgrade(Resource type, Person player) {
        Clicker clicker = Clicker.getInstance();
        UpgradeSystem item = clicker.getClickerTool(type);
        int upgradePrice = item.getUpgradePrice();
        TransferPackage transfer = new TransferPackage(0 ,0, upgradePrice);

        if (player.getWallet().hasEnoughResource(Resource.Gold, upgradePrice)) {
            this.wallet.deposit(player.getWallet(), transfer);
            item.upgradeLevel(); // Upgrade the item

            player.getEventTracker().addEvent(EventTracker.Message("Shop", "Successfully upgraded " + type + " Clicker to level " + item.getUpgradeLevel() + "!"));
            return true;
        } else {
            player.getEventTracker().addEvent(EventTracker.Message("Error", "Insufficient gold to upgrade " + type + " clicker to level " + (item.getUpgradeLevel() + 1) + "."));
            return false;
        }
    }


}
