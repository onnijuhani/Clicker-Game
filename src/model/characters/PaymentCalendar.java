package model.characters;

import model.resourceManagement.TransferPackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentCalendar {

    // A class to hold payment information
    public static class PaymentInfo {
        String name;
        TransferPackage payment;
        int day;

        public PaymentInfo(String name, TransferPackage payment, int day) {
            this.name = name;
            this.payment = payment;
            this.day = day;
        }

        @Override
        public String toString() {
            return String.format("%s on day %d: %s", name, day, payment.toString());
        }
    }

    // Map that holds information of all payments
    private final Map<Integer, List<PaymentInfo>> paymentSchedule = new HashMap<>();


    public void addPayment(String name, TransferPackage payment, int day) {
        PaymentInfo paymentInfo = new PaymentInfo(name, payment, day);

        // If there's already a payment scheduled for this day, add to the list. Otherwise, create a new list.
        paymentSchedule.computeIfAbsent(day, k -> new ArrayList<>()).add(paymentInfo);
    }

    public List<PaymentInfo> getPaymentsForDay(int day) {
        return paymentSchedule.getOrDefault(day, new ArrayList<>());
    }
}

