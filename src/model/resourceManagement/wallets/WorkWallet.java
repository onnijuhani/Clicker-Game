package model.resourceManagement.wallets;

import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.payments.PaymentTracker;
import model.resourceManagement.TransferPackage;
import model.shop.Ownable;
import model.stateSystem.EventTracker;

public class WorkWallet extends Wallet implements PaymentTracker {
    private boolean taxedOrNot;
    private final Wallet mainWallet;
    private TransferPackage lastSalaryAmount;
    private int lastSalaryDay;

    public WorkWallet(final Ownable owner, Wallet mainWallet) {
        super(owner);
        this.taxedOrNot = false;
        this.mainWallet = mainWallet;
    }

    public void cashOutSalary(int day) {
        if (isTaxed()) {
            TransferPackage amount = new TransferPackage(getFood(), getAlloy(), getGold());
            mainWallet.depositAll(this);
            getOwner().getEventTracker().addEvent(EventTracker.Message("Minor","Salary received"));
            setTaxedOrNot(false);

            lastSalaryAmount = amount;
            lastSalaryDay = day;

            updatePaymentCalendar(getOwner().getPaymentManager());

        }
    }

    @Override
    public void updatePaymentCalendar(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.INCOME, Payment.EXPECTED_SALARY_INCOME, lastSalaryAmount, lastSalaryDay);
    }



    public boolean isTaxed(){
        return taxedOrNot;
    }
    public void setTaxedOrNot(Boolean state){
        taxedOrNot = state;
    }


}
