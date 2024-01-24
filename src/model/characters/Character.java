package model.characters;

import model.NameCreation;
import model.buildings.Property;
import model.characters.npc.Slave;
import model.characters.player.EventTracker;
import model.characters.player.Player;
import model.resourceManagement.resources.Resource;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.shop.Exchange;
import model.worldCreation.Details;
import model.worldCreation.Nation;
import time.FoodManager;
import time.FoodObserver;
import time.TimeEventManager;
import time.TimeObserver;

import java.util.LinkedList;

public class Character implements TimeObserver, FoodObserver, Details {

    @Override
    public void timeUpdate(int day, int month, int year) {
    }

    @Override
    public void foodUpdate() {
        foodConsumption(this);
        if (this instanceof Player){
            this.getEventTracker().addEvent(EventTracker.Message("Minor",foodConsumption+" Food Consumed."));
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

    public Status getStatus(){
        return status;
    }


    protected static int totalAmount;
    protected LinkedList<Slave> slaves;
    protected  Nation nation;
    public String name;
    protected Wallet wallet;
    protected WorkWallet workWallet;
    protected Property property;
    protected LinkedList<Character> allies;
    protected LinkedList<Character> enemies;
    protected EventTracker eventTracker;
    protected int foodConsumption = 10;
    protected Status status;
    public Character() {
        this.wallet = new Wallet();
        this.slaves = new LinkedList<>();
        this.allies = new LinkedList<>();
        this.enemies = new LinkedList<>();
        this.name = NameCreation.generateCharacterName();
        this.eventTracker = new EventTracker();
        if (shouldSubscribeToTimeEvent()) {
            TimeEventManager.subscribe(this);
        }
        FoodManager.subscribe(this);
    }

    protected boolean shouldSubscribeToTimeEvent() {
        return true;
    }

    public void foodConsumption(Character character) {

        Wallet wallet = character.getWallet();
        int foodNeeded = foodConsumption;
        foodConsumption+=5;
        Exchange exchange = nation.getExchange();


        try {
            if (wallet.hasEnoughResource(Resource.Food, foodNeeded)) {
                wallet.subtractFood(foodNeeded);
            } else {
                // Calculate how much more food is needed
                double additionalFoodNeeded = foodNeeded - wallet.getFood().getAmount();

                // Check if enough gold is available to buy the required food
                double costInGold = exchange.calculateExchangeCost(additionalFoodNeeded, Resource.Food, Resource.Gold);
                if (wallet.hasEnoughResource(Resource.Gold, costInGold)) {

                    exchange.exchangeResources(additionalFoodNeeded, Resource.Food, Resource.Gold, character);
                } else {
                    // Check if enough alloys are available and can be converted to gold for food
                    double costInAlloys = exchange.calculateExchangeCost(costInGold, Resource.Gold, Resource.Alloy);
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

}


