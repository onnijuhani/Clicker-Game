package model.characters.payments;

import model.characters.payments.PaymentCalendar;

public interface Tracker {
    void updatePaymentCalendar(PaymentCalendar calendar);
}
