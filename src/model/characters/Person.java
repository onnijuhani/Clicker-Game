package model.characters;

import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.util.Duration;
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

import java.util.*;

public class Person implements Ownable {
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
    private boolean isPlayer;
    private AiEngine aiEngine;

    public Person(Boolean isNpc) {
        this.isPlayer = !isNpc;
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

        delayMethods();
    }

    private void startingMsgAndAiEngine() {
        this.aiEngine = new AiEngine(this);
        if(isPlayer){
            return;
        }
        eventTracker.addEvent(EventTracker.Message("Major",
                "\nYou are "+character + "\n" +
                        "Traits: "+getAiEngine().getProfile() + "\n" +
                        "Starting Area: " + property.getLocation().getFullHierarchyInfo() + "\n" +
                        "Your Authority is " + role.getAuthority()
        ));
    }
    private void delayMethods() {
        // some methods need to be delayed to allow the simulation to load
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(new KeyFrame(
                Duration.millis(1000), // Delay before executing the task
                ae -> {
                    try {
                        Platform.runLater(this::startingMsgAndAiEngine);
                    } catch (Exception e) {
                        System.out.println("Something went wrong with delaying methods in Person " + e);
                    }
                }));
        timeline.setCycleCount(1);
        timeline.play();
    }
    public void loseStrike(){
        getStrikesTracker().loseStrike();
        int strikesLeft = getStrikesTracker().getStrikes();
        if (strikesLeft < 1) {
            triggerGameOver();
        }else {
            getEventTracker().addEvent(EventTracker.Message("Major", "Lost a Strike! Strikes left: " + strikesLeft));
        }
    }
    private void triggerGameOver(){
        if(character.getPerson().isPlayer()) {
            getEventTracker().addEvent(EventTracker.Message("Major","GAME OVER. No Strikes left."));
            Time.setGameOver(true);
        }
    }
    public void decreasePersonalOffence(int x) {
        for(int i = 0; i < x; i++) {
            combatStats.decreaseOffense();
        }
    }
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
    public String getName() {
        return name;
    }
    public AiEngine getAiEngine() {
        return aiEngine;
    }
    public Wallet getWallet() {
        return wallet;
    }
    public WorkWallet getWorkWallet() {
        return workWallet;
    }
    public RelationsManager getRelationsManager() {
        return relationsManager;
    }
    public EventTracker getEventTracker() {
        return eventTracker;
    }
    public CombatStats getCombatStats() {return combatStats;}
    public EnumSet<State> getStates() {
        return states.clone();
    }
    public void addState(State state) {
        states.add(state);
    }
    public void removeState(State state) {
        states.remove(state);
    }
    public boolean hasState(State state) {
        return states.contains(state);
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
    public List<GameEvent> getOngoingEvents() {
        return ongoingEvents;
    }
    public PaymentCalendar getPaymentCalendar() {return paymentCalendar;}
    public StrikesTracker getStrikesTracker() {
        return strikesTracker;
    }
    public Property getProperty() {
        return property;
    }
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
    public Person getPerson(){
        return this;
    }
}

