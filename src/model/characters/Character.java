package model.characters;

import model.GameManager;
import model.characters.ai.AiEngine;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.shop.Exchange;
import model.shop.Ownable;
import model.stateSystem.EventTracker;
import model.time.NpcManager;
import model.time.NpcObserver;
import model.worldCreation.Details;

public class Character implements NpcObserver, Details, Ownable {


    @Override
    public void npcUpdate(int day, int month, int year){

        if( day == 6 && month == 0 && year == 0){
            updateFoodConsumption(GameManager.getFoodConsumptionRate());
        }

        if (mandatoryFoodConsumption(day)) return; // food consumption should be the only 1 done here, should also be day1
        if (day == 2){
            person.getRelationsManager().updateSets(); // updating relations should be the only one done in day 2
            return;
        }

            if(person.isPlayer()){
            return;
        }



        getPerson().getAiEngine().executeAiEngine(); // AI engine is executed rest of the time
    }

    private boolean mandatoryFoodConsumption(int day) {
        if (day == GameManager.getFoodConsumptionDay()) {
            foodConsumption(this);

            if (person.isPlayer()) {
                this.getEventTracker().addEvent(EventTracker.Message("Minor", GameManager.getFoodConsumptionRate() + " Food Consumed."));
                return true;
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

    private void updateFoodConsumption(int FoodConsumptionRate) {
        person.getPaymentCalendar().addPayment(PaymentManager.PaymentType.EXPENSE, Payment.FOOD_EXPENSE, new TransferPackage(FoodConsumptionRate, 0, 0), 1);
    }


    protected void makeConnections() {
        person.setRole(role);
        person.setCharacter(this);

        role.setPerson(person);
        role.setCharacter(this);
    }



    public void foodConsumption(Character character) {


        Wallet wallet = character.getPerson().getWallet();
        int foodNeeded = GameManager.getFoodConsumptionRate();
        Exchange exchange = role.getNation().getShop().getExchange();

        updateFoodConsumption(foodNeeded);

        try {
            if (wallet.hasEnoughResource(Resource.Food, foodNeeded)) {
                wallet.subtractFood(foodNeeded);
                // success consuming food
            } else {
                // Calculate how much more food is needed
                int additionalFoodNeeded = foodNeeded - wallet.getFood();

                // Check if enough gold is available to buy the required food
                int costInGold = exchange.calculateExchangeCost(additionalFoodNeeded, Resource.Food, Resource.Gold);
                if (wallet.hasEnoughResource(Resource.Gold, costInGold)) {
                    // Perform exchange and food subtraction
                    if (exchange.exchangeResources(additionalFoodNeeded, Resource.Food, Resource.Gold, character)) {
                        wallet.subtractFood(wallet.getFood()); // Subtract only the remaining food
                    }
                } else {
                    // Check if enough alloys are available and can be converted to gold for food
                    int costInAlloys = exchange.calculateExchangeCost(costInGold, Resource.Gold, Resource.Alloy);
                    if (wallet.hasEnoughResource(Resource.Alloy, costInAlloys)) {
                        // Perform exchanges and food subtraction
                        if (exchange.exchangeResources(costInGold, Resource.Gold, Resource.Alloy, character) &&
                                exchange.exchangeResources(additionalFoodNeeded, Resource.Food, Resource.Gold, character)) {
                            wallet.subtractFood(wallet.getFood()); // Subtract only the remaining food
                        }
                    } else {
                        // Case where neither gold nor alloys are sufficient
                        String errorMessage = EventTracker.Message("Error", "Not enough resources to cover food consumption.");
                        character.getPerson().getEventTracker().addEvent(errorMessage);
                        character.getPerson().getStrikesTracker().loseStrike();
                        return;
                    }
                }
                // subtract the remaining food after successful exchanges
                wallet.subtractFood(wallet.getFood());
            }
        } catch (Exception e) {
            System.out.println("Something went wrong with food consumption: " + e.getMessage());
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


