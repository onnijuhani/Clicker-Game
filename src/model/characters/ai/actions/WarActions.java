package model.characters.ai.actions;

import model.buildings.properties.MilitaryProperty;
import model.characters.Person;
import model.characters.Trait;
import model.characters.ai.actionCircle.WeightedObject;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;
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

            if (notMilitaryProperty()) return;

            MilitaryProperty property = (MilitaryProperty) person.getProperty();
            Army army = property.getArmy();


            TransferPackage netBalance = person.getPaymentManager().getNetBalance();

            if(!(netBalance.food() > ArmyCost.runningFood)){
                return;
            }

            // amount of soldiers to hire
            int amount =  Math.max(netBalance.food() / ArmyCost.runningFood - 1, 1) ;

            if(army.recruitSoldier(amount)){
                person.getEventTracker().addEvent(EventTracker.Message("Major", "Recruited " + amount + " new Soldier(s)"));

                logAction(String.format("Recruited %d new soldiers. Now total of %d soldiers", amount, army.getNumOfSoldiers()));

            }else{
                person.getEventTracker().addEvent(EventTracker.Message("Major", "Recruiting new Soldiers went wrong"));
            }



        }
    }
    class TrainAttack extends WeightedObject {

        public TrainAttack(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        @Override
        public void defaultAction() {
            if (notMilitaryProperty()) {
                return;
            }

            TransferPackage netBalance = person.getPaymentManager().getNetBalance();

            if (!(netBalance.alloy() > ArmyCost.runningAlloy)) {
                return;
            }

            Army army = getCurrentArmy();

            if(army.increaseAttackPower()){

            logAction(String.format("Increased army attack power, total power now %d", army.getTotalAttackPower()));
            }

        }
    }

    class TrainDefence extends WeightedObject {

        public TrainDefence(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        @Override
        public void defaultAction() {
            if (notMilitaryProperty()) {
                return;
            }

            TransferPackage netBalance = person.getPaymentManager().getNetBalance();

            if (!(netBalance.alloy() > ArmyCost.runningAlloy)) {
                return;
            }

            Army army = getCurrentArmy();

            if(army.increaseDefencePower()) {

                logAction(String.format("Increased army defence power, total power now %d", army.getTotalDefencePower()));
            }
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

}
