package model.characters;

import model.GameManager;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.ai.AiEngine;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
import model.shop.Exchange;
import model.shop.Ownable;
import model.stateSystem.EventTracker;
import model.time.NpcManager;
import model.time.NpcObserver;
import model.worldCreation.Details;

public class Character implements NpcObserver, Details, Ownable {


    @Override
    public void npcUpdate(int day, int month, int year) {

        if (mandatoryFoodConsumption(day)) return; // food consumption should be the only 1 done here, should also be day1
        if (day == 2){
            System.out.println("alkaa");
            person.getRelationsManager().updateSets(); // updating relations should be the only one in day 2
            System.out.println("loppuu");
            return;
        }

        if(person.isPlayer()){
            return;
        }

        getPerson().getAiEngine().getCombatActions().execute();
    }

    private boolean mandatoryFoodConsumption(int day) {
        if (day == GameManager.getFoodConsumptionDay()) {
            foodConsumption(this);

            if (person.isPlayer()) {
                this.getEventTracker().addEvent(EventTracker.Message("Minor", GameManager.getFoodConsumptionDay() + " Food Consumed."));
                return true;
            }

            if (!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                buyMeadowLandsTEST();
            }
            if (person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                upgrade();
            }
        }
        return false;
    }

    protected Person person;
    protected Role role;
    protected AiEngine aiEngine;


    // Constructor for NPC (default)
    public Character() {
        this(false);
    }
    // Constructor with a flag for player or NPC
    public Character(boolean isPlayer) {
        this.person = new Person(!isPlayer); //person takes isNpc instead
        this.role = new Role(Status.Peasant);
        makeConnections();
        NpcManager.subscribe(this);

    }



    protected void makeConnections() {
        person.setRole(role);
        person.setCharacter(this);

        role.setPerson(person);
        role.setCharacter(this);
    }

    protected void buyMeadowLandsTEST(){
        role.getNation().getShop().getUtilityShop().buyBuilding(UtilityBuildings.MeadowLands,this.getPerson());
    }
    protected void upgrade(){
        if(person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
            role.getNation().getShop().getUtilityShop().upgradeBuilding(UtilityBuildings.MeadowLands, this.getPerson());
        }
    }


    public void foodConsumption(Character character) {

        Wallet wallet = character.getPerson().getWallet();

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
                        character.person.getStrikesTracker().loseStrike();
                        return;
                    }
                }

                // Now subtract the food after successful exchange
                wallet.subtractFood(foodNeeded);

            }
        } catch (Exception e) {
            System.out.println("Something went wrong with food consumption");
        }
    }
    @Override
    public String getDetails() {
        return (this.getClass().getSimpleName()+" "+ getName());
    }
    @Override
    public String toString() {
        return role.getStatus() +" "+ person.getName();
    }
    public String getName() {
        return person.getName();
    }

    @Override
    public EventTracker getEventTracker() {
        return person.getEventTracker();
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

}


