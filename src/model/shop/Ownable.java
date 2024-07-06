package model.shop;

import model.characters.payments.PaymentManager;
import model.stateSystem.MessageTracker;

public interface Ownable {
    MessageTracker getMessageTracker();
    PaymentManager getPaymentManager();

}

