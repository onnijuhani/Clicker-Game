package model.resourceManagement.wallets;

import model.shop.Ownable;

public class WorkWallet extends Wallet {
    private boolean taxedOrNot;
    private final Wallet mainWallet;
    public WorkWallet(final Ownable owner, Wallet mainWallet) {
        super(owner);
        this.taxedOrNot = false;
        this.mainWallet = mainWallet;
    }

    public void cashOutSalary() {
        if (isTaxed()) {
            mainWallet.depositAll(this);
            setTaxedOrNot(false);
        }
    }

    public boolean isTaxed(){
        return taxedOrNot;
    }
    public void setTaxedOrNot(Boolean state){
        taxedOrNot = state;
    }
}
