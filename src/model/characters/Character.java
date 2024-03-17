package model.characters;

import model.GameManager;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
import model.shop.Exchange;
import model.shop.Ownable;
import model.stateSystem.EventTracker;
import model.time.NpcManager;
import model.time.NpcObserver;
import model.time.TaxEventManager;
import model.time.TaxObserver;
import model.worldCreation.Details;

public class Character implements TaxObserver, NpcObserver, Details, Ownable {

    @Override
    public void taxUpdate(int day, int month, int year) { //implemented at subclasses
    }
    @Override
    public void npcUpdate(int day, int month, int year) {
        if (day == GameManager.getFoodConsumptionDay()) {
            foodConsumption(this);
//            if (person.isPlayer()) {
//                this.getEventTracker().addEvent(EventTracker.Message("Minor", GameManager.getFoodConsumptionDay() + " Food Consumed."));
//                return;
//            }
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
    protected boolean shouldSubscribeToTaxEvent() {
        return true;
    }
    protected boolean shouldSubscribeToNpcEvent() {
        return true;
    }

    public Character() {
        this.person = new Person(true);
        this.role = new Role(Status.Peasant);

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


