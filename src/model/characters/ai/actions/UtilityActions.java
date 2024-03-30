package model.characters.ai.actions;

import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Person;
import model.characters.Status;
import model.characters.Trait;
import model.characters.ai.Aspiration;
import model.characters.ai.actionCircle.WeightedObject;
import model.shop.UtilityShop;
import model.time.Time;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * THE IMPORTANCE OF THESE SHOULD BE VERY LOW.
 */
public class UtilityActions {
    private final Person person;
    private final UtilitySlot utilitySlot;
    private final UtilityShop utilityShop;
    private final List<WeightedObject> allActions = new LinkedList<>();
    private final Map<Trait, Integer> profile;

    private int counter = 0; // the point of this counter is to make sure every utility building gets upgraded slowly even if no other method does it.
    private int counterTarget = 10; // getting the year from Time makes the Target higher so that later levels these "forced" upgrades happen more rarely
    public UtilityActions(Person person, Map<Trait, Integer> profile ) {
        this.person = person;
        Property property = person.getProperty();
        this.utilitySlot = property.getUtilitySlot();
        this.utilityShop = property.getLocation().getNation().getShop().getUtilityShop();
        this.profile = profile;
        createAllActions();
    }


    private void createAllActions() {
        Meadowlands meadowlands = new Meadowlands(5, profile);
        AlloyMine alloyMine = new AlloyMine(10, profile);
        GoldMine goldMine = new GoldMine(15, profile);
        MysticMine mysticMine = new MysticMine(10, profile);
        SlaveFacility slaveFacility = new SlaveFacility(10, profile);
        WorkerCenter workerCenter = new WorkerCenter(10, profile);

        allActions.add(meadowlands);
        allActions.add(alloyMine);
        allActions.add(goldMine);
        allActions.add(mysticMine);
        allActions.add(slaveFacility);
        allActions.add(workerCenter);
    }



    public List<WeightedObject> getAllActions() {
        return allActions;
    }

    /**
     * Quick return immediately if they are low on resources, this should prevent some problems
     * @return wallet balance information
     */
    private boolean lowBalanceReturn() {
        return person.getWallet().isLowBalance();
    }

    class GoldMine extends WeightedObject {

        public GoldMine(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }

        @Override
        public void execute(){
            defaultAction();
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            counter++;
            if (lowBalanceReturn()) return;

            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.GoldMine)) {
                utilityShop.buyBuilding(UtilityBuildings.GoldMine, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget = 10 + Time.getYear() * 2;
                    utilityShop.upgradeBuilding(UtilityBuildings.GoldMine, person);
                }
            }

        }
    }



    class Meadowlands extends WeightedObject {
        public Meadowlands(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }
        private int amountsReached = 0;
        @Override
        public void execute() {

            if(amountsReached < 5){ // make sure farmer doesn't build Meadowlands as his first building
                if(person.getRole().getStatus() == Status.Farmer){
                    amountsReached++;
                    return;
                }
            }

            defaultAction();
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            counter++;
            if (lowBalanceReturn()) return;
            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                utilityShop.buyBuilding(UtilityBuildings.MeadowLands, person);
            } else {
                if (counter > counterTarget) {
                    counter = 0;
                    counterTarget = 10 + Time.getYear() * 2;
                    utilityShop.upgradeBuilding(UtilityBuildings.MeadowLands, person);
                }
            }
        }
    }

    class AlloyMine extends WeightedObject {
        public AlloyMine(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }
        private int amountsReached = 0;
        @Override
        public void execute(){

            if(amountsReached < 5){ // make sure miner doesn't build Alloys as his first building
                if(person.getRole().getStatus() == Status.Miner){
                    amountsReached++;
                    return;
                }
            }

            defaultAction();
        }
        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            counter++;
            if (lowBalanceReturn()) return;
            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.AlloyMine)) {
                utilityShop.buyBuilding(UtilityBuildings.AlloyMine, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget = 10 + Time.getYear() * 2;
                    utilityShop.upgradeBuilding(UtilityBuildings.AlloyMine, person);
                }
            }
        }
    }
    class MysticMine extends WeightedObject {
        public MysticMine(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }
        @Override
        public void execute(){
            defaultAction();
        }
        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            counter++;
            if (lowBalanceReturn()) return;
            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.MysticMine)) {
                utilityShop.buyBuilding(UtilityBuildings.MysticMine, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget = 10 + Time.getYear() * 2;
                    utilityShop.upgradeBuilding(UtilityBuildings.MysticMine, person);
                }
            }
        }
    }

    class SlaveFacility extends WeightedObject {
        public SlaveFacility(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }


        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            counter++;
            if (lowBalanceReturn()) return;
            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

            // liberals never build this one
            if (person.getAiEngine().getProfile().containsKey(Trait.Liberal)) {
                return;
            }

            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.SlaveFacility)) {
                utilityShop.buyBuilding(UtilityBuildings.SlaveFacility, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget = 10 + Time.getYear() * 2;
                    utilityShop.upgradeBuilding(UtilityBuildings.SlaveFacility, person);

                    // non slavers join the guild after level 5
                    if (utilitySlot.getUtilityBuilding(UtilityBuildings.SlaveFacility).getUpgradeLevel() > 5) {
                        person.getRole().getNation().joinSlaverGuild(person);
                    }
                }
            }
        }

        public void slaverAction() {
            if (lowBalanceReturn()) return;
            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

            // slavers join immediately
            if (person.getAiEngine().getProfile().containsKey(Trait.Slaver)) {
                person.getRole().getNation().joinSlaverGuild(person);
            }
            defaultAction();
        }

    }

    class WorkerCenter extends WeightedObject {

        public WorkerCenter(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            counter++;
            if (lowBalanceReturn()) return;
            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

            // slavers never build this one
            if (person.getAiEngine().getProfile().containsKey(Trait.Slaver)) {
                return;
            }

            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.WorkerCenter)) {
                utilityShop.buyBuilding(UtilityBuildings.WorkerCenter, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget =  10 + Time.getYear() * 2;
                    utilityShop.upgradeBuilding(UtilityBuildings.WorkerCenter, person);

                    // non liberals join the guild after level 5
                    if (utilitySlot.getUtilityBuilding(UtilityBuildings.WorkerCenter).getUpgradeLevel() > 5) {
                        person.getRole().getNation().joinLiberalGuild(person);
                    }
                }
            }
        }

        public void liberalAction() {
            if (lowBalanceReturn()) return;
            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

            // liberals join immediately
            if (person.getAiEngine().getProfile().containsKey(Trait.Liberal)) {
                    person.getRole().getNation().joinLiberalGuild(person);
            }
            defaultAction();
        }
    }

}



