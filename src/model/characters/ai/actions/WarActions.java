package model.characters.ai.actions;

import model.buildings.properties.MilitaryProperty;
import model.characters.Person;
import model.characters.Trait;
import model.characters.ai.Aspiration;
import model.characters.ai.actionCircle.WeightedObject;
import model.resourceManagement.TransferPackage;
import model.stateSystem.MessageTracker;
import model.war.Army;
import model.war.ArmyCost;
import model.war.Military;

import java.util.List;
import java.util.Map;

public class WarActions extends BaseActions {

    public WarActions(Person person, NPCActionLogger npcActionLogger, Map<Trait, Integer> profile) {
        super(person, npcActionLogger, profile);
    }

    @Override
    protected void createAllActions() {
        HireSoldiers hireSoldiers = new HireSoldiers(person, npcActionLogger,5,profile);
        TrainAttack trainAttack = new TrainAttack(person, npcActionLogger,5,profile);
        TrainDefence trainDefence = new TrainDefence(person, npcActionLogger,5,profile);

        allActions.add(hireSoldiers);
        allActions.add(trainAttack);
        allActions.add(trainDefence);
    }

    class HireSoldiers extends WeightedObject{

        public HireSoldiers(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        @Override
        public void defaultAction(){
            hireSoldiers(this);
        }
        @Override
        public void liberalAction(){
            hireSoldiers(this);
        }
        @Override
        public void aggressiveAction(){
            increaseOffence(this);
        }
    }

    class TrainAttack extends WeightedObject {

        public TrainAttack(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }
        @Override
        public void defaultAction() {
            increaseOffence(this);
        }
        @Override
        public void defenderAction() {
            increaseDefence(this);
        }
        @Override
        public void slaverAction(){
            hireSoldiers(this);
        }
        @Override
        public void aggressiveAction(){
            increaseOffence(this);
        }

    }



    class TrainDefence extends WeightedObject {

        public TrainDefence(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        @Override
        public void defaultAction() {
            increaseDefence(this);
        }
        @Override
        public void slaverAction(){
            hireSoldiers(this);
        }
        @Override
        public void aggressiveAction(){
            increaseOffence(this);
        }
    }

    private Army getCurrentArmy() {
        Military military = (Military)person.getProperty();
        return military.getArmy();
    }

    private boolean notMilitaryProperty() {
        return !(person.getProperty() instanceof MilitaryProperty);
    }
    public List<WeightedObject> getAllActions() {
        return allActions;
    }

    private void increaseOffence(WeightedObject action) {
        Army army = getArmyAfterPowerChecks();
        if (army == null) return;

        if(army.increaseAttackPower()){

            action.logAction(String.format("Increased army attack power, total power now %d", army.getTotalAttackPower()));
        }
    }


    private void increaseDefence(WeightedObject action) {
        Army army = getArmyAfterPowerChecks();
        if (army == null) return;
        if(army.increaseDefencePower()) {
            action.logAction(String.format("Increased army defence power, total power now %d", army.getTotalDefencePower()));
        }
    }

    private Army getArmyAfterPowerChecks() {
        if (notMilitaryProperty()) {
            return null;
        }
        TransferPackage currentExpenses = person.getPaymentManager().getFullExpense();
        TransferPackage currentBalance = person.getWallet().getBalance();
        if (!currentBalance.isGreaterThanOrEqualTo(currentExpenses)) {
            return null;
        }
        TransferPackage netBalance = person.getPaymentManager().getNetBalance();

        if (!(netBalance.alloy() > ArmyCost.runningAlloy)) {
            person.addAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
            return null;
        }

        return getCurrentArmy();
    }

    private void hireSoldiers(WeightedObject action) {
        if (notMilitaryProperty()) return;

        MilitaryProperty property = (MilitaryProperty) person.getProperty();
        Army army = property.getArmy();

        TransferPackage netBalance = person.getPaymentManager().getNetBalance();

        TransferPackage currentExpenses = person.getPaymentManager().getFullExpense();
        TransferPackage currentBalance = person.getWallet().getBalance();
        if (!currentBalance.isGreaterThanOrEqualTo(currentExpenses)) {
            return;
        }

        if(!(netBalance.food() > ArmyCost.runningFood)){
            action.logAction(String.format("Didn't recruit new soldiers. Net food is: %d, required: %d", netBalance.food(), ArmyCost.runningFood));
            person.addAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
            return;
        }
        if(!(netBalance.gold() > ArmyCost.runningGold)){
            action.logAction(String.format("Didn't recruit new soldiers. Net gold is: %d, required: %d", netBalance.gold(), ArmyCost.runningGold));
            person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
            return;
        }
        // amount of soldiers to hire
        int amount =  Math.max(netBalance.food() / ArmyCost.runningFood - 1, 1) ;
        amount =  Math.min(netBalance.gold() / ArmyCost.runningGold - 1, amount) ;

        if(army.recruitSoldier(amount)){
            person.getEventTracker().addEvent(MessageTracker.Message("Major", "Recruited " + amount + " new Soldier(s)"));

            action.logAction(String.format("Recruited %d new soldiers. Now total of %d soldiers", amount, army.getNumOfSoldiers()));

        }else{
            person.getEventTracker().addEvent(MessageTracker.Message("Major", "Recruiting new Soldiers went wrong"));
        }
    }

}
