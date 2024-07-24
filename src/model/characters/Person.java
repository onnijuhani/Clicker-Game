package model.characters;

import model.NameCreation;
import model.Settings;
import model.buildings.GrandFoundry;
import model.buildings.Property;
import model.characters.ai.AiEngine;
import model.characters.ai.Aspiration;
import model.characters.ai.actions.NPCActionLogger;
import model.characters.combat.CombatStats;
import model.characters.npc.*;
import model.characters.payments.PaymentManager;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.shop.Ownable;
import model.stateSystem.*;
import model.time.EventManager;
import model.time.Time;
import model.war.Military;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static model.stateSystem.SpecialEventsManager.triggerFirstGameOverWarning;
import static model.stateSystem.SpecialEventsManager.triggerGameOverWarning;

public class Person implements Ownable {
    private final String name;
    private final Wallet wallet;
    private final WorkWallet workWallet;
    private Property property;
    private final RelationsManager relationsManager;
    private final MessageTracker messageTracker;
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

    public int getCharacterPic() {
        return characterPic;
    }

    private int characterPic = 0;



    private GrandFoundry grandFoundry;

    public Person(Boolean isNpc) {
        this.isPlayer = !isNpc;
        this.wallet = new Wallet(this);
        this.workWallet = new WorkWallet(this, wallet);
        this.relationsManager = new RelationsManager(this);
        this.name = NameCreation.generateCharacterName(isNpc);
        this.messageTracker = new MessageTracker(isNpc);
        this.combatStats = new CombatStats(Settings.getInt("offenceBasePrice"),Settings.getInt("defenceBasePrice"), this);
        this.paymentManager = new PaymentManager(wallet);
        this.strikesTracker = new StrikesTracker(Settings.getInt("strikes"));
        states = EnumSet.noneOf(State.class);
        aspirations = EnumSet.noneOf(Aspiration.class);

        delayMethods();
    }

    public void generatePicture() {
        if(isPlayer){
            this.characterPic = 15;
            return;
        }

        if(character instanceof King){
            this.characterPic = 16;
            return;
        }
        if(character instanceof Vanguard){
            this.characterPic = 17;
            return;
        }
        if (character instanceof Miner || character instanceof Farmer) {
            if(Settings.getRandom().nextBoolean()) {
                this.characterPic = Settings.getRandom().nextBoolean() ? 18 : 4;
            }else{
                this.characterPic = Settings.getRandom().nextInt(6) + 1;
            }
            return;
        }
        if(character instanceof Mercenary){
            this.characterPic = 19;
            return;
        }
        if(character instanceof Noble){
            this.characterPic = 22;
            return;
        }
        this.characterPic = Settings.getRandom().nextInt(9) + 1;
    }

    private void startingMsgAndAiEngine() {
        this.aiEngine = new AiEngine(this);
        if(isPlayer){
            this.aiEngine.setRunning(false);
            return;
        }
        messageTracker.addMessage(MessageTracker.Message("Major",
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

    /**
     * Only method that should ever be called to lose strikes
     */
    public void loseStrike(String message){
        logAction(this, "Lose Strike",message);
        getStrikesTracker().loseStrike();
        int strikesLeft = getStrikesTracker().getStrikes();
        if (strikesLeft == 9 && isPlayer){
            triggerFirstGameOverWarning();
        }
        if (strikesLeft == 1 && isPlayer){
            triggerGameOverWarning();
        }
        if (strikesLeft < 1) {
            triggerGameOver(message);
        }else {
            if(isPlayer) {
                getMessageTracker().addMessage(MessageTracker.Message("Major", "Lost a Strike! " + message));
                getMessageTracker().addMessage(MessageTracker.Message("Major", "Strikes left: " + strikesLeft));
            }
        }
    }



    private void triggerGameOver(String message){
        if(character.getPerson().isPlayer()) {
            getMessageTracker().addMessage(MessageTracker.Message("Major","GAME OVER. No Strikes left."));
            Time.setGameOver(true);

            PopUpMessageTracker.PopUpMessage gameOverMessage = new PopUpMessageTracker.PopUpMessage(
                    "Game Over",
                    "ALL STRIKES LOST\n\n"+message+"\n\nYour journey ends here.\n\n",
                    "Properties/gameOver.jpg",
                    "R.I.P"
            );
            PopUpMessageTracker.sendMessage(gameOverMessage);
            PopUpMessageTracker.sendGameOver();
        }else{
            getStrikesTracker().gainStrike(10);
            getMessageTracker().addMessage(MessageTracker.Message("Major", "Bankrupt"));
            for(int i = 0; i < 5; i++){
                decreasePersonalPower();
            }
        }
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

    public void decreasePersonalOffence(int x) {
        for(int i = 0; i < x; i++) {
            combatStats.decreaseOffense();
        }
    }

    public GrandFoundry getGrandFoundry() {
        return grandFoundry;
    }

    public void setGrandFoundry(final GrandFoundry grandFoundry) {
        if(property instanceof Military) {
            this.grandFoundry = grandFoundry;
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
    public MessageTracker getMessageTracker() {
        return messageTracker;
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

    /**
     * @param npc NPC Person that executed the method
     * @param action Class that the method belongs to. Should always be "this"
     * @param details The actual action and relevant information
     */
    public void logAction(Person npc, String action, String details){
        getAiEngine().getNpcActionLogger().logAction(npc, action, details);
    }
    public void logAction(Aspiration asp, String details){
        getAiEngine().getNpcActionLogger().logAction(this, asp, details);
    }
    public void logAction(String method, String details){
        getAiEngine().getNpcActionLogger().logAction(this, method, details);
    }

    public NPCActionLogger getNpcLogger() {
        return getAiEngine().getNpcActionLogger();
    }
    public List<String> getLoggerMessages(){
        return getAiEngine().getNpcActionLogger().getLogs();
    }

    private boolean grandFoundryOpened = false;
    public void openGrandFoundry() {
        try {
            if(property instanceof Military){
                if(!grandFoundryOpened) {
                    GameEvent gameEvent = new GameEvent(Event.GRAND_FOUNDRY, this);
                    EventManager.scheduleEvent(this::setGrandFoundry, 90, gameEvent);
                    grandFoundryOpened = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    private void setGrandFoundry() {
        try {
            this.grandFoundry = new GrandFoundry(this);
            if(isPlayer) {
                SpecialEventsManager.triggerGrandFoundryInfo();
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
}

