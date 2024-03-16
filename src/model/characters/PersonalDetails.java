package model.characters;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.characters.combat.CombatStats;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.shop.Ownable;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.State;

import java.util.ArrayList;
import java.util.List;
public class PersonalDetails implements PersonalAttributes, Ownable {

    private String name;
    private Wallet wallet;
    private WorkWallet workWallet;
    private Property property;
    private RelationshipManager relationshipManager;
    private EventTracker eventTracker;
    private CombatStats combatStats;
    private State state;
    private List<GameEvent> ongoingEvents = new ArrayList<>();
    private PaymentCalendar paymentCalendar;
    private StrikesTracker strikesTracker;


    public PersonalDetails(Boolean isNpc) {
        this.wallet = new Wallet(this);
        this.relationshipManager = new RelationshipManager();
        this.name = NameCreation.generateCharacterName();
        this.eventTracker = new EventTracker(isNpc);
        this.combatStats = new CombatStats(Settings.getInt("offenceBasePrice"),Settings.getInt("defenceBasePrice"), this);
        this.paymentCalendar = new PaymentCalendar();
        this.strikesTracker = new StrikesTracker(Settings.getInt("strikes"));
    }

    public void addEvent(GameEvent gameEvent) {
        ongoingEvents.add(gameEvent);
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Wallet getWallet() {
        return wallet;
    }

    @Override
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public WorkWallet getWorkWallet() {
        return workWallet;
    }

    @Override
    public void setWorkWallet(WorkWallet workWallet) {
        this.workWallet = workWallet;
    }

    @Override
    public Property getProperty() {
        return property;
    }

    @Override
    public void setProperty(Property property) {
        this.property = property;
    }

    @Override
    public RelationshipManager getRelationshipManager() {
        return relationshipManager;
    }

    @Override
    public void setRelationshipManager(RelationshipManager relationshipManager) {
        this.relationshipManager = relationshipManager;
    }

    @Override
    public EventTracker getEventTracker() {
        return eventTracker;
    }

    @Override
    public void setEventTracker(EventTracker eventTracker) {
        this.eventTracker = eventTracker;
    }

    @Override
    public CombatStats getCombatStats() {
        return combatStats;
    }

    @Override
    public void setCombatStats(CombatStats combatStats) {
        this.combatStats = combatStats;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

    @Override
    public List<GameEvent> getOngoingEvents() {
        return ongoingEvents;
    }

    public void setOngoingEvents(List<GameEvent> ongoingEvents) {
        this.ongoingEvents = ongoingEvents;
    }

    @Override
    public PaymentCalendar getPaymentCalendar() {
        return paymentCalendar;
    }

    public void setPaymentCalendar(PaymentCalendar paymentCalendar) {
        this.paymentCalendar = paymentCalendar;
    }

    @Override
    public StrikesTracker getStrikesTracker() {
        return strikesTracker;
    }
    public void setStrikesTracker(StrikesTracker strikesTracker) {
        this.strikesTracker = strikesTracker;
    }
}

