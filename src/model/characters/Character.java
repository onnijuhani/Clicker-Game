package model.characters;

import model.GameManager;
import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.authority.Authority;
import model.characters.combat.CombatStats;
import model.characters.player.Player;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
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
            if (this instanceof Player) {
                this.getEventTracker().addEvent(EventTracker.Message("Minor", GameManager.getFoodConsumptionDay() + " Food Consumed."));
                return;
            }
            if (!personalDetails.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                buyMeadowLandsTEST();
            }
            if (personalDetails.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                upgrade();
            }
        }
    }

    private PersonalDetails personalDetails;
    private RoleDetails roleDetails;


    public Character() {
        this.personalDetails = new PersonalDetails(true);
        this.roleDetails = new RoleDetails();
        if (shouldSubscribeToTaxEvent()) {
            TaxEventManager.subscribe(this);
        }
        if (shouldSubscribeToNpcEvent()) {
            NpcManager.subscribe(this);
        }
    }

    protected void buyMeadowLandsTEST(){
        roleDetails.getNation().getShop().getUtilityShop().buyBuilding(UtilityBuildings.MeadowLands,this);
    }
    protected void upgrade(){
        if(personalDetails.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
            roleDetails.getNation().getShop().getUtilityShop().upgradeBuilding(UtilityBuildings.MeadowLands, this);
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

        Exchange exchange = roleDetails.getNation().getShop().getExchange();

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
        return this.getClass().getSimpleName() +" "+ personalDetails.getName();
    }
    public String getName() {
        return personalDetails.getName();
    }
    public Nation getNation() {
        return roleDetails.getNation();
    }
    public void setNation(Nation nation) {
        roleDetails.setNation(nation);
    }
    public Status getStatus(){
        return roleDetails.getStatus();
    }
    public void setStatus(Status status) {
        roleDetails.setStatus(status);
    }
    public void setProperty(Property property){
        personalDetails.setProperty(property);
    }
    public Property getProperty(){
        return personalDetails.getProperty();
    }

    public Wallet getWallet() {
        return personalDetails.getWallet();
    }
    public void setWallet(Wallet wallet) {
        personalDetails.setWallet(wallet);
    }
    @Override
    public EventTracker getEventTracker() {
        return personalDetails.getEventTracker();
    }

    public Authority getAuthority() {
        return roleDetails.getAuthority();
    }
    public void setAuthority(Authority authority) {
        roleDetails.setAuthority(authority);
    }
    public CombatStats getCombatStats() {
        return personalDetails.getCombatStats();
    }
    public void setCombatStats(CombatStats combatStats) {
        personalDetails.setCombatStats(combatStats);
    }
    public RelationshipManager getRelationshipManager() {
        return personalDetails.getRelationshipManager();
    }
    public State getState() {
        return personalDetails.getState();
    }
    public void setState(State state) {
        personalDetails.setState(state);
    }
    public void addEvent(GameEvent gameEvent) {
        personalDetails.addEvent(gameEvent);
    }
    public List<GameEvent> getOngoingEvents() {
        return personalDetails.getOngoingEvents();
    }

    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    public RoleDetails getRoleDetails() {
        return roleDetails;
    }

    public void setRoleDetails(RoleDetails roleDetails) {
        this.roleDetails = roleDetails;
    }

    public void loseStrike(){
        personalDetails.getStrikesTracker().loseStrike();
        int strikesLeft = personalDetails.getStrikesTracker().getStrikes();

        if (strikesLeft < 1) {
            triggerGameOver();
            getEventTracker().addEvent(EventTracker.Message("Major","GAME OVER. No Strikes left."));

        }else {
            getEventTracker().addEvent(EventTracker.Message("Major", "Lost a Strike! Strikes left: " + strikesLeft));
        }
    }

    private void triggerGameOver(){
        if (this instanceof Player){
            Time.setGameOver(true);
        }
    }

}


