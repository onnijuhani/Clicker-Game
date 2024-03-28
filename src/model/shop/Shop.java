package model.shop;

import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.EventTracker;

public class Shop implements Ownable {
    private Wallet wallet;
    private final Exchange exchange;
    private final ClickerShop clickerShop;
    private final UtilityShop utilityShop;
    public Shop(){
        initializeWallet();
        this.exchange = new Exchange(wallet);
        this.clickerShop = new ClickerShop(wallet);
        this.utilityShop = new UtilityShop(wallet);
    }

    private void initializeWallet(){
        this.wallet = new Wallet(this);
        TransferPackage startingBalance = new TransferPackage(100000,50000,10000);
        wallet.addResources(startingBalance);
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
    public Wallet getWallet() {
        return wallet;
    }
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override // maybe add event Tracker later
    public EventTracker getEventTracker() {
        return null;
    }


}





