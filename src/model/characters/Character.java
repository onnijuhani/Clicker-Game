package model.characters;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.authority.Authority;
import model.characters.combat.CombatStats;
import model.characters.player.Player;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.shop.Exchange;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.State;
import model.time.NpcManager;
import model.time.NpcObserver;
import model.time.TaxEventManager;
import model.time.TaxObserver;
import model.worldCreation.Details;
import model.worldCreation.Nation;

import java.util.ArrayList;
import java.util.List;

public class Character implements TaxObserver, NpcObserver, Details {

    @Override
    public void taxUpdate(int day, int month, int year) { //implemented at subclasses
    }
    @Override
    public void npcUpdate(int day, int month, int year) {
        if (day == foodUpdateDay) {
            foodConsumption(this);
            if (this instanceof Player) {
                this.getEventTracker().addEvent(EventTracker.Message("Minor", foodConsumption[0] + " Food Consumed."));
                return;
            }
            if (!property.getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                buyMeadowLandsTEST();
            }
            if (property.getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                upgrade();
            }
        }
    }

    protected Authority authority;  // bound to class
    protected static int totalAmount; // tracks total amount of instances created
    protected Nation nation; // bound to class
    protected String name; //personal
    protected Wallet wallet; //personal
    protected WorkWallet workWallet; //personal
    protected Property property;  //personal
    protected RelationshipManager relationshipManager; //personal
    protected EventTracker eventTracker; //personal
    protected int[] foodConsumption = {5,0}; //bound to class
    protected Status status; // bound to class
    protected int foodUpdateDay; //bound to class
    protected CombatStats combatStats; //personal
    protected State state = State.NONE;  //personal
    protected List<GameEvent> ongoingEvents = new ArrayList<>(); //personal
    protected PaymentCalendar paymentCalendar; //personal
    protected StrikesTracker strikesTracker; //personal

    public Character() {
        this.wallet = new Wallet();
        this.relationshipManager = new RelationshipManager();
        this.foodUpdateDay = Settings.get("foodConsumption");
        this.name = NameCreation.generateCharacterName();
        this.eventTracker = new EventTracker();
        this.combatStats = new CombatStats(10,5, this);
        this.paymentCalendar = new PaymentCalendar();
        this.strikesTracker = new StrikesTracker(10);
        if (shouldSubscribeToTaxEvent()) {
            TaxEventManager.subscribe(this);
        }
        if (shouldSubscribeToNpcEvent()) {
            NpcManager.subscribe(this);
        }
    }

    protected void buyMeadowLandsTEST(){
        nation.getShop().getUtilityShop().buyBuilding(UtilityBuildings.MeadowLands,this);
    }
    protected void upgrade(){
        if(property.getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
            nation.getShop().getUtilityShop().upgradeBuilding(UtilityBuildings.MeadowLands, this);
        }
    }

    protected boolean shouldSubscribeToTaxEvent() {
        return true;
    }
    protected boolean shouldSubscribeToNpcEvent() {
        return true;
    }

    public void foodConsumption(Character character) {
        Wallet wallet = character.getWallet();
        //food consumption goes up every year by 5
        int foodNeeded = foodConsumption[0];
        foodConsumption[1] += 1;
        if(foodConsumption[1] == 11) {
            foodConsumption[0]+=5;
            foodConsumption[1] = 0;
        }
        Exchange exchange = nation.getShop().getExchange();

        try {
            if (wallet.hasEnoughResource(Resource.Food, foodNeeded)) {
                wallet.subtractFood(foodNeeded);
            } else {
                // Calculate how much more food is needed
                int additionalFoodNeeded = foodNeeded - wallet.getFood();

                // Check if enough gold is available to buy the required food
                int costInGold = exchange.calculateExchangeCost(additionalFoodNeeded, Resource.Food, Resource.Gold);
                if (wallet.hasEnoughResource(Resource.Gold, costInGold)) {

                    exchange.exchangeResources(additionalFoodNeeded, Resource.Food, Resource.Gold, character);
                } else {
                    // Check if enough alloys are available and can be converted to gold for food
                    int costInAlloys = exchange.calculateExchangeCost(costInGold, Resource.Gold, Resource.Alloy);
                    if (wallet.hasEnoughResource(Resource.Alloy, costInAlloys)) {
                        exchange.exchangeResources(costInGold, Resource.Gold, Resource.Alloy, character);
                        exchange.exchangeResources(additionalFoodNeeded, Resource.Food, Resource.Gold, character);
                    } else {
                        // Handle the case where neither gold nor alloys are sufficient
                        String errorMessage = EventTracker.Message("Error", "Not enough resources to cover food consumption.");
                        character.getEventTracker().addEvent(errorMessage);
                        return;
                    }
                }

                // Now subtract the food after successful exchange
                wallet.subtractFood(foodNeeded);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public String getDetails() {
        return (this.getClass().getSimpleName()+" "+ getName());
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName() +" "+ name;
    }
    public String getName() {
        return name;
    }
    public Nation getNation() {
        return nation;
    }
    public void setNation(Nation nation) {
        this.nation = nation;
    }
    public Status getStatus(){
        return status;
    }
    public void setProperty(Property property){
        this.property = property;
    }
    public Property getProperty(){
        return property;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public EventTracker getEventTracker() {
        return eventTracker;
    }
    public void setEventTracker(EventTracker eventTracker) {
        this.eventTracker = eventTracker;
    }
    public Authority getAuthority() {
        return authority;
    }
    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
    public CombatStats getCombatStats() {
        return combatStats;
    }

    public void setCombatStats(CombatStats combatStats) {
        this.combatStats = combatStats;
    }

    public RelationshipManager getLoyaltyManager() {
        return relationshipManager;
    }

    public void setLoyaltyManager(RelationshipManager relationshipManager) {
        this.relationshipManager = relationshipManager;
    }
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }
    public void addEvent(GameEvent gameEvent) {
        ongoingEvents.add(gameEvent);
    }
    public List<GameEvent> getOngoingEvents() {
        return ongoingEvents;
    }
}


