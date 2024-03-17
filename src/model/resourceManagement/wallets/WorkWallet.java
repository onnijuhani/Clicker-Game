package model.resourceManagement.wallets;

import model.shop.Ownable;
import model.stateSystem.EventTracker;

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
            getOwner().getEventTracker().addEvent(EventTracker.Message("Minor","Salary received"));
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
