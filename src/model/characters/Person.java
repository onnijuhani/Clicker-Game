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
    private boolean isPlayer = true;
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

    private void generateStartingMessage() {

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

        // this is delayed because of eventTracker
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(new KeyFrame(
                Duration.millis(1000), // Delay before executing the task
                ae -> {
                    try {
                        Platform.runLater(this::generateStartingMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
        timeline.setCycleCount(1);
        timeline.play();
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

