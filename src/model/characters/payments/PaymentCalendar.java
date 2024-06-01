package model.characters.payments;

import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.time.Time;

import java.util.*;

public class PaymentCalendar {


    // A class to hold payment information
    public class PaymentInfo {
        public Payment name;
        public TransferPackage amount;
        public int day;

        public PaymentInfo(Payment name, TransferPackage amount, int day) {
            this.name = name;
            this.amount = amount;
            this.day = day;
        }

        public boolean canMakePayment() {
            return wallet.hasEnoughResources(amount);
        }

        private TransferPackage resourcesNeeded() {
            int neededFood = Math.max(0, amount.food() - wallet.getFood());
            int neededAlloy = Math.max(0, amount.alloy() - wallet.getAlloy());
            int neededGold = Math.max(0, amount.gold() - wallet.getGold());
            return new TransferPackage(neededFood, neededAlloy, neededGold);
        }

        private int getDaysLeft() {
            int currentDay = Time.getDay();
            return (day >= currentDay) ? day - currentDay : (day - currentDay + 30);
        }
        public String toStringWithDaysLeft() {
            return String.format("%s on day %d: %s (%d days left)", name, day, amount.toString(), getDaysLeft());
        }
        @Override
        public String toString() {

            if(canMakePayment()) {
                return String.format("%s %s in %s days", name, amount.toShortString(), getDaysLeft());
            } else {
                return String.format("%s %s in %s days. Resources required: %s", name, amount.toShortString(), getDaysLeft(), resourcesNeeded().toShortString());
            }
        }
    }




    // Map that holds information of all payments
    private final Map<Payment, PaymentInfo> expenses = new HashMap<>();
    private final Map<Payment, PaymentInfo> incomes = new HashMap<>();
    protected Wallet wallet;

    public PaymentCalendar(Wallet wallet) {
        this.wallet = wallet;
    }


    /**
     * @param type Income or Expense
     * @param name Type of payment
     * @param payment transferPackage with amounts
     * @param day day the payment occurs
     */
    public void addPayment(PaymentType type, Payment name, TransferPackage payment, int day) {
        PaymentInfo newPaymentInfo = new PaymentInfo(name, payment, day);
        Map<Payment, PaymentInfo> payments = (type == PaymentType.EXPENSE) ? expenses : incomes;

        payments.put(name, newPaymentInfo);
    }

    public enum PaymentType {
        EXPENSE,
        INCOME
    }

    public PaymentInfo getAnyExpense(Payment payment){
        return expenses.get(payment);
    }
    public PaymentInfo getAnyIncome(Payment payment){
        return incomes.get(payment);
    }

    public TransferPackage getFullIncome(){
        return getTransferPackage(incomes);
    }

    public TransferPackage getFullExpense(){
        return getTransferPackage(expenses);
    }

    private TransferPackage getTransferPackage(Map<Payment, PaymentInfo> payments) {
        int[] fullAmount = {0,0,0};
        payments.forEach((payment, info) -> {
            fullAmount[0] += info.amount.food();
            fullAmount[1] += info.amount.alloy();
            fullAmount[2] += info.amount.gold();
        });
        return new TransferPackage(fullAmount[0], fullAmount[1], fullAmount[2]);
    }

    public PaymentInfo getNextPayment(){
        int currentDay = Time.getDay();
        PaymentInfo nextPayment = null;
        int minDayDifference = Integer.MAX_VALUE;

        // Check expenses
        for (PaymentInfo info : expenses.values()) {
            int dayDifference = (info.day >= currentDay) ? info.day - currentDay : (info.day - currentDay + 30);
            if (dayDifference < minDayDifference) {
                minDayDifference = dayDifference;
                nextPayment = info;
            }
        }
        // Check incomes
        for (PaymentInfo info : incomes.values()) {
            int dayDifference = (info.day >= currentDay) ? info.day - currentDay : (info.day - currentDay + 30);
            if (dayDifference < minDayDifference) {
                minDayDifference = dayDifference;
                nextPayment = info;
            }
        }
        return nextPayment;
    }

    public List<PaymentInfo> getAllPaymentsInOrder() {
        int currentDay = Time.getDay();

        List<PaymentInfo> allPayments = new ArrayList<>();
        allPayments.addAll(expenses.values());
        allPayments.addAll(incomes.values());

        allPayments.sort((p1, p2) -> {
            int dayDifference1 = (p1.day >= currentDay) ? p1.day - currentDay : (p1.day - currentDay + 30);
            int dayDifference2 = (p2.day >= currentDay) ? p2.day - currentDay : (p2.day - currentDay + 30);
            return Integer.compare(dayDifference1, dayDifference2);
        });

        return allPayments;
    }


}

