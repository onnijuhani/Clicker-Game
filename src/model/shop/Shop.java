package model.shop;

import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;

public class Shop {
    private Wallet wallet;
    private Exchange exchange;
    private ClickerShop clickerShop;
    private UtilityShop utilityShop;
    public Shop(){
        initializeWallet();
        this.exchange = new Exchange(wallet);
        this.clickerShop = new ClickerShop(wallet);
        this.utilityShop = new UtilityShop(wallet);
    }

    private void initializeWallet(){
        this.wallet = new Wallet();
        TransferPackage startingBalance = new TransferPackage(1000,1000,500);
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

}





