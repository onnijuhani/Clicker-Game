package model.resourceManagement.wallets;

public class WorkWallet extends Wallet {
    private boolean taxedOrNot;
    private Wallet mainWallet;
    public WorkWallet(Wallet mainWallet) {
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
