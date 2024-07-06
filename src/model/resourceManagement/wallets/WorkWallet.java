package model.resourceManagement.wallets;

import model.characters.Person;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.payments.PaymentTracker;
import model.resourceManagement.TransferPackage;
import model.shop.Ownable;
import model.stateSystem.MessageTracker;
import model.worldCreation.Nation;

public class WorkWallet extends Wallet implements PaymentTracker {
    private boolean taxedOrNot;
    private final Wallet mainWallet;
    private TransferPackage lastSalaryAmount;
    private int lastSalaryDay;


    private Boolean warTax = false;
    private Nation conqueror;
    private Nation nation;



    public WorkWallet(final Ownable owner, Wallet mainWallet) {
        super(owner);
        this.taxedOrNot = false;
        this.mainWallet = mainWallet;
    }

    public void cashOutSalary(int day) {
        if (isTaxed()) {
            TransferPackage amount = new TransferPackage(getFood(), getAlloy(), getGold());

            if(conqueror != null){
                TransferPackage forcedTax = amount.multiply(0.25);
                if(getOwner() instanceof Person p){
                    if(p.isPlayer()){
                        p.getMessageTracker().addMessage(MessageTracker.Message("Minor", "Forced tax paid to: " + conqueror + "\n" + "Amount: " + forcedTax));
                    }
                }
                amount = amount.subtract(forcedTax);

                conqueror.getWallet().addResources(forcedTax);
            }

            if(warTax){
                TransferPackage warTax = amount.multiply(0.1);
                amount = amount.subtract(warTax);

                nation.getWallet().addResources(warTax);
            }

            mainWallet.depositAll(this);
            getOwner().getMessageTracker().addMessage(MessageTracker.Message("Minor","Salary received"));
            setTaxedOrNot(false);

            lastSalaryAmount = amount;
            lastSalaryDay = day;

            if(getOwner() instanceof Person person){
                if(person.isPlayer()){
                    return;
                }
            }
            updatePaymentManager(getOwner().getPaymentManager());

        }
    }

    @Override
    public void updatePaymentManager(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.INCOME, Payment.EXPECTED_SALARY_INCOME, lastSalaryAmount, lastSalaryDay);
    }



    public boolean isTaxed(){
        return taxedOrNot;
    }
    public void setTaxedOrNot(Boolean state){
        taxedOrNot = state;
    }
    public TransferPackage getLastSalaryAmount() {
        return lastSalaryAmount;
    }
    public void setConqueror(Nation conqueror) {
        this.conqueror = conqueror;
    }

    public void setWarTax(boolean warTax, Nation nation){
        this.warTax = warTax;
        this.nation = nation;
    }
    public void setWarTax(boolean warTax){
        this.warTax = warTax;
    }

}
