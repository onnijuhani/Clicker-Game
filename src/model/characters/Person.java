package model.characters;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.characters.ai.AiEngine;
import model.characters.ai.Aspiration;
import model.characters.combat.CombatStats;
import model.characters.payments.PaymentManager;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.shop.Ownable;
import model.stateSystem.*;
import model.time.Time;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private final PaymentManager paymentManager;
    private final StrikesTracker strikesTracker;
    private Character character;
    private Role role;
    private boolean isPlayer;
    private AiEngine aiEngine;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Person(Boolean isNpc) {
        this.isPlayer = !isNpc;
        this.wallet = new Wallet(this);
        this.workWallet = new WorkWallet(this, wallet);
        this.relationsManager = new RelationsManager(this);
        this.name = NameCreation.generateCharacterName(isNpc);
        this.eventTracker = new EventTracker(isNpc);
        this.combatStats = new CombatStats(Settings.getInt("offenceBasePrice"),Settings.getInt("defenceBasePrice"), this);
        this.paymentManager = new PaymentManager(wallet);
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
        scheduler.schedule(() -> {
            try {
                startingMsgAndAiEngine();
            } catch (Exception e) {
                System.out.println("Something went wrong with delaying methods in Person " + e);
            }
        }, 1, TimeUnit.SECONDS); // Delay before executing the task
    }
    public void loseStrike(){
        getStrikesTracker().loseStrike();
        int strikesLeft = getStrikesTracker().getStrikes();
        if (strikesLeft < 1) {
            triggerGameOver();
        }else {
            getEventTracker().addEvent(EventTracker.Message("Major", "Lost a Strike! Strikes left: " + strikesLeft));
        }
        decreasePersonalPower();
    }

    private void decreasePersonalPower() {
        if(!character.getPerson().isPlayer){
            character.getPerson().getCombatStats().decreaseOffense();
            character.getPerson().getCombatStats().decreaseDefence();
        }
    }

    public GameEvent getAnyOnGoingEvent(Event event){
        return getOngoingEvents().stream()
                .filter(any -> any.getEvent() == event)
                .findFirst()
                .orElse(null);
    }
                                        /**
     * Player loses all strikes triggers game over and simulation stops.
     * NPC's gain back 20 strikes and lose attack and defence points instead weakening them.
     */
    private void triggerGameOver(){
        if(character.getPerson().isPlayer()) {
            getEventTracker().addEvent(EventTracker.Message("Major","GAME OVER. No Strikes left."));
            Time.setGameOver(true);
            PopUpMessageTracker.sendMessage("ALL STRIKES LOST\n\nGAME OVER");
            PopUpMessageTracker.sendGameOver();
        }else{
            getStrikesTracker().gainStrike(20);
            for(int i = 0; i < 5; i++){
                decreasePersonalPower();
            }
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
    public PaymentManager getPaymentManager() {return paymentManager;}
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

