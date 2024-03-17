package model.characters;

import model.buildings.Property;
import model.characters.combat.CombatStats;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.State;

import java.util.List;

public interface PersonalAttributes {
    String getName();
    Wallet getWallet();
    WorkWallet getWorkWallet();
    Property getProperty();
    RelationshipManager getRelationshipManager();
    EventTracker getEventTracker();
    CombatStats getCombatStats();
    State getState();
    List<GameEvent> getOngoingEvents();
    PaymentCalendar getPaymentCalendar();
    StrikesTracker getStrikesTracker();


    void setWallet(Wallet wallet);
    void setWorkWallet(WorkWallet workWallet);
    void setProperty(Property property);

    void setState(State state);
    void addEvent(GameEvent gameEvent);

}

