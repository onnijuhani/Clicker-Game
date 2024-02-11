package model.shop;

import model.resourceManagement.wallets.Wallet;

public class ShopComponents {
    protected Wallet wallet;

    public ShopComponents(Wallet wallet) {
        this.wallet = wallet;
    }

    public Wallet getWallet() {
        return wallet;
    }

}


