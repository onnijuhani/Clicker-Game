package model.characters;

import model.buildings.Property;
import model.characters.combat.CombatStats;
import model.characters.payments.PaymentManager;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.stateSystem.MessageTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.State;

import java.util.EnumSet;
import java.util.List;

public interface PersonalAttributes {
    String getName();
    Wallet getWallet();
    WorkWallet getWorkWallet();
    Property getProperty();
    RelationsManager getRelationsManager();
    MessageTracker getEventTracker();
    CombatStats getCombatStats();
    EnumSet<State> getStates();
    List<GameEvent> getOngoingEvents();
    PaymentManager getPaymentCalendar();
    StrikesTracker getStrikesTracker();


    void setProperty(Property property);
    void addState(State state);
    void addEvent(GameEvent gameEvent);
}

