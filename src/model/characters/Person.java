package model.characters;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.characters.ai.AiEngine;
import model.characters.ai.Aspiration;
import model.characters.combat.CombatStats;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.shop.Ownable;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.State;
import model.time.Time;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
public class Person implements PersonalAttributes, Ownable {
    private final String name;
    private final Wallet wallet;
    private final WorkWallet workWallet;
    private Property property;
    private final RelationsManager relationsManager;
    private final EventTracker eventTracker;
    private final CombatStats combatStats;
    private final EnumSet<State> states;

    private final EnumSet<Aspiration> aspirations;
    private final List<GameEvent> ongoingEvents = new ArrayList<>();
    private final PaymentCalendar paymentCalendar;
    private final StrikesTracker strikesTracker;
    private Character character;
    private Role role;
    private boolean isPlayer = false;
    private final AiEngine aiEngine;


    public Person(Boolean isNpc) {
        this.wallet = new Wallet(this);
        this.workWallet = new WorkWallet(this, wallet);
        this.relationsManager = new RelationsManager(this);
        this.name = NameCreation.generateCharacterName(isNpc);
        this.eventTracker = new EventTracker(isNpc);
        this.combatStats = new CombatStats(Settings.getInt("offenceBasePrice"),Settings.getInt("defenceBasePrice"), this);
        this.paymentCalendar = new PaymentCalendar();
        this.strikesTracker = new StrikesTracker(Settings.getInt("strikes"));
        states = EnumSet.noneOf(State.class);
        aspirations = EnumSet.noneOf(Aspiration.class);

        this.aiEngine = new AiEngine(this);
    }

    @Override
    public String toString() {
        return getName();
    }
    public boolean isPlayer() {
        return isPlayer;
    }
    public void setPlayer(boolean player) {
        isPlayer = player;
    }
    public void addEvent(GameEvent gameEvent) {
        ongoingEvents.add(gameEvent);
    }
    @Override
    public String getName() {
        return name;
    }

    public AiEngine getAiEngine() {
        return aiEngine;
    }
    @Override
    public Wallet getWallet() {
        return wallet;
    }
    @Override
    public WorkWallet getWorkWallet() {
        return workWallet;
    }
    @Override
    public RelationsManager getRelationsManager() {
        return relationsManager;
    }
    @Override
    public EventTracker getEventTracker() {
        return eventTracker;
    }
    @Override
    public CombatStats getCombatStats() {
        return combatStats;
    }
    @Override
    public EnumSet<State> getStates() {
        return states.clone();
    }
    @Override
    public void addState(State state) {
        states.add(state);
    }
    public void removeState(State state) {
        states.remove(state);
    }
    public boolean hasState(State state) {
        return states.contains(state);
    }
    public void clearStates() {
        states.clear();
    }
    public EnumSet<Aspiration> getAspirations() {
        return aspirations;
    }
    public void addAspiration(Aspiration aspiration) {
        aspirations.add(aspiration);
    }
    public void removeAspiration(Aspiration aspiration) {
        aspirations.remove(aspiration);
    }
    public boolean hasAspiration(Aspiration aspiration) {
        return aspirations.contains(aspiration);
    }
    @Override
    public List<GameEvent> getOngoingEvents() {
        return ongoingEvents;
    }
    @Override
    public PaymentCalendar getPaymentCalendar() {
        return paymentCalendar;
    }
    @Override
    public StrikesTracker getStrikesTracker() {
        return strikesTracker;
    }
    @Override
    public Property getProperty() {
        return property;
    }
    @Override
    public void setProperty(Property property) {
        this.property = property;
    }
    public Character getCharacter() {
        return character;
    }
    public void setCharacter(Character character) {
        this.character = character;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void loseStrike(){
        getStrikesTracker().loseStrike();
        int strikesLeft = getStrikesTracker().getStrikes();
        if (strikesLeft < 1) {
            triggerGameOver();
            getEventTracker().addEvent(EventTracker.Message("Major","GAME OVER. No Strikes left."));
        }else {
            getEventTracker().addEvent(EventTracker.Message("Major", "Lost a Strike! Strikes left: " + strikesLeft));
        }

    }
    private void triggerGameOver(){
        if(character.getPerson().isPlayer()) {
            Time.setGameOver(true);
        }
    }

    public void decreaseOffense(int x) {
        for(int i = 0; i < x; i++) {
            combatStats.decreaseOffense();
        }
    }
    public Person getPerson(){
        return this;
    }

}

