package model.characters;

import model.GameManager;
import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.authority.Authority;
import model.characters.combat.CombatStats;
import model.characters.player.PlayerPeasant;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Vault;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.shop.Exchange;
import model.shop.Ownable;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.State;
import model.time.*;
import model.worldCreation.Details;
import model.worldCreation.Nation;

import java.util.List;

public class Character implements TaxObserver, NpcObserver, Details, Ownable {

    @Override
    public void taxUpdate(int day, int month, int year) { //implemented at subclasses
    }
    @Override
    public void npcUpdate(int day, int month, int year) {
        if (day == GameManager.getFoodConsumptionDay()) {
            foodConsumption(this);
            if (this instanceof PlayerPeasant) {
                this.getEventTracker().addEvent(EventTracker.Message("Minor", GameManager.getFoodConsumptionDay() + " Food Consumed."));
                return;
            }
            if (!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                buyMeadowLandsTEST();
            }
            if (person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                upgrade();
            }
        }
    }

    protected Person person;
    protected Role role;


    public Character() {
        this.person = new Person(true);
        this.role = new Role();

        makeConnections();


        if (shouldSubscribeToTaxEvent()) {
            TaxEventManager.subscribe(this);
        }
        if (shouldSubscribeToNpcEvent()) {
            NpcManager.subscribe(this);
        }
    }

    protected void makeConnections() {
        person.setRole(role);
        person.setCharacter(this);

        role.setPerson(person);
        role.setCharacter(this);
    }

    protected void buyMeadowLandsTEST(){
        role.getNation().getShop().getUtilityShop().buyBuilding(UtilityBuildings.MeadowLands,this);
    }
    protected void upgrade(){
        if(person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
            role.getNation().getShop().getUtilityShop().upgradeBuilding(UtilityBuildings.MeadowLands, this);
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

        int foodNeeded = GameManager.getFoodConsumptionRate();

        Exchange exchange = role.getNation().getShop().getExchange();

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
            System.out.println("Something went wrong with food consumption");
            e.printStackTrace();
        }
    }
    @Override
    public String getDetails() {
        return (this.getClass().getSimpleName()+" "+ getName());
    }
    @Override
    public String toString() {
        return getStatus() +" "+ person.getName();
    }
    public String getName() {
        return person.getName();
    }
    public Nation getNation() {
        return role.getNation();
    }
    public void setNation(Nation nation) {
        role.setNation(nation);
    }
    public Status getStatus(){
        return role.getStatus();
    }
    public void setStatus(Status status) {
        role.setStatus(status);
    }
    public void setProperty(Property property){
        person.setProperty(property);
    }
    public Property getProperty(){
        return person.getProperty();
    }

    public Wallet getWallet() {
        return person.getWallet();
    }
    public void setWallet(Wallet wallet) {
        person.setWallet(wallet);
    }
    @Override
    public EventTracker getEventTracker() {
        return person.getEventTracker();
    }

    public Authority getAuthority() {
        return role.getAuthority();
    }
    public void setAuthority(Authority authority) {
        role.setAuthority(authority);
    }
    public CombatStats getCombatStats() {
        return person.getCombatStats();
    }

    public RelationshipManager getRelationshipManager() {
        return person.getRelationshipManager();
    }
    public State getState() {
        return person.getState();
    }
    public void setState(State state) {
        person.setState(state);
    }
    public void addEvent(GameEvent gameEvent) {
        person.addEvent(gameEvent);
    }
    public List<GameEvent> getOngoingEvents() {
        return person.getOngoingEvents();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public WorkWallet getWorkWallet(){
        return person.getWorkWallet();
    }
    public Vault getVault(){
        return person.getProperty().getVault();
    }

    public void loseStrike(){
        person.getStrikesTracker().loseStrike();
        int strikesLeft = person.getStrikesTracker().getStrikes();

        if (strikesLeft < 1) {
            triggerGameOver();
            getEventTracker().addEvent(EventTracker.Message("Major","GAME OVER. No Strikes left."));

        }else {
            getEventTracker().addEvent(EventTracker.Message("Major", "Lost a Strike! Strikes left: " + strikesLeft));
        }
    }

    private void triggerGameOver(){
        if (this instanceof PlayerPeasant){
            Time.setGameOver(true);
        }
    }

    public void decreaseOffense(int amountLevels) {
        for(int i = 0; i < amountLevels; i++) {
            getCombatStats().decreaseOffense();
        }
    }
}


