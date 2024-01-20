package model.resourceManagement.wallets;

public class WorkWallet extends Wallet {
    private boolean taxedOrNot;
    public WorkWallet() {
        this.taxedOrNot = false;
    }
    public boolean isTaxed(){
        return taxedOrNot;
    }
    public void setTaxedOrNot(Boolean state){
        taxedOrNot = state;
    }
}
