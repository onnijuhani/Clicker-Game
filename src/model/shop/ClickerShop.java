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
import model.stateSystem.MessageTracker;

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
            person.getMessageTracker().addMessage(MessageTracker.Message("Shop", "Successfully purchased " + type + " Clicker!"));
            return true; // Purchase was successful
        } else {
            person.getMessageTracker().addMessage(MessageTracker.Message("Error", "Insufficient gold to buy " + type + " clicker."));
            return false; // Purchase failed
        }
    }

    public boolean buyAutoClicker(Person person){
        TransferPackage amount = getAutoClickerPrice();

        if(person.getWallet().subtractResources(amount)){
            Clicker.getInstance().setAutoClickerOwned(true);
            Clicker.getInstance().decreaseAutoClickerLevel();
            return true;
        }
        return false;
    }

    public static TransferPackage getAutoClickerPrice() {
        TransferPackage amount = new TransferPackage(5, 5, 5);

        int level = Clicker.getInstance().getAutoClickerLevel();

        if(level == 3){
            amount = amount.multiply(500);
        }
        if(level == 2){
            amount = amount.multiply(10_000);
        }
        if(level == 1){
            amount = amount.multiply(0);
        }
        return amount;
    }


    public ClickerTools createClickerTool(Resource type) {
        return switch (type) {
            case Alloy -> new AlloyClicker(Settings.getInt("alloyClicker"));
            case Gold -> new GoldClicker(Settings.getInt("goldClicker"));
            case Food -> new GoldClicker(Settings.getInt("foodClicker"));
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
            item.increaseLevel(); // Upgrade the item

            player.getMessageTracker().addMessage(MessageTracker.Message("Shop", "Successfully upgraded " + type + " Clicker to level " + item.getUpgradeLevel() + "!"));
            return true;
        } else {
            player.getMessageTracker().addMessage(MessageTracker.Message("Error", "Insufficient gold to upgrade " + type + " clicker to level " + (item.getUpgradeLevel() + 1) + "."));
            return false;
        }
    }


}
