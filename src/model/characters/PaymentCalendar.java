package model.characters;

import model.resourceManagement.TransferPackage;

import java.util.*;

public class PaymentCalendar {

    // A class to hold payment information
    public static class PaymentInfo {
        public Payment name;
        public TransferPackage amount;
        public int day;

        public PaymentInfo(Payment name, TransferPackage amount, int day) {
            this.name = name;
            this.amount = amount;
            this.day = day;
        }

        @Override
        public String toString() {
            return String.format("%s on day %d: %s", name, day, amount.toString());
        }
    }

    // Map that holds information of all payments
    private final Map<Payment, PaymentInfo> expenses = new HashMap<>();
    private final Map<Payment, PaymentInfo> incomes = new HashMap<>();


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

    public PaymentInfo getExpense(Payment payment){
        return expenses.get(payment);
    }

}

