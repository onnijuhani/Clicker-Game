package model.shop;

import model.characters.payments.PaymentManager;
import model.stateSystem.EventTracker;

public interface Ownable {
    EventTracker getEventTracker();
    PaymentManager getPaymentManager();
}

