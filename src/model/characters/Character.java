package model.characters;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.authority.Authority;
import model.characters.decisions.MarketActions;
import model.characters.npc.Slave;
import model.characters.player.EventTracker;
import model.characters.player.Player;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.shop.Exchange;
import model.time.NpcManager;
import model.time.NpcObserver;
import model.time.TaxEventManager;
import model.time.TaxObserver;
import model.worldCreation.Details;
import model.worldCreation.Nation;

import java.util.LinkedList;

public class Character implements TaxObserver, NpcObserver, Details {

    @Override
    public void taxUpdate(int day, int month, int year) {
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
        System.out.println("market actions started");
        MarketActions.decideMarketActions(this);
    }

    @Override
    public String getDetails() {
        return (this.getClass().getSimpleName()+" "+ getName());
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName() +" "+ name;
    }
    protected Authority authority;
    protected static int totalAmount;
    protected LinkedList<Slave> slaves;
    protected Nation nation;
    protected String name;
    protected Wallet wallet;
    protected WorkWallet workWallet;
    protected Property property;
    protected LinkedList<Character> allies;
    protected LinkedList<Character> enemies;
    protected EventTracker eventTracker;
    protected int[] foodConsumption = {5,0};
    protected Status status;
    protected int foodUpdateDay;
    public Character() {
        this.wallet = new Wallet();
        this.slaves = new LinkedList<>();
        this.allies = new LinkedList<>();
        this.enemies = new LinkedList<>();
        this.foodUpdateDay = Settings.get("foodConsumption");
        this.name = NameCreation.generateCharacterName();
        this.eventTracker = new EventTracker();
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

    public void addSlave(Slave slave){
        slaves.add(slave);
    }
    public void deleteSlave(Slave slave){
        slaves.remove(slave);
    }
    public void addAlly(Character ally){
        allies.add(ally);
    }
    public void deleteAlly(Character ally){
        allies.remove(ally);
    }
    public void addEnemy(Character enemy){
        enemies.add(enemy);
    }
    public void deleteEnemy(Character enemy){
        enemies.remove(enemy);
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

}


