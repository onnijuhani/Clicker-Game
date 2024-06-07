package model.characters.ai.actions;

import model.buildings.utilityBuilding.UtilityBuildings;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Person;
import model.characters.Status;
import model.characters.Trait;
import model.characters.ai.Aspiration;
import model.characters.ai.actionCircle.WeightedObject;
import model.shop.UtilityShop;

import java.util.List;
import java.util.Map;

/**
 * THE IMPORTANCE OF THESE SHOULD BE VERY LOW.
 */
public class UtilityActions extends BaseActions{


    private int counter = 0; // the point of this counter is to make sure every utility building gets upgraded slowly even if no other method does it.
    private int counterTarget = 1; // getting the year from Time makes the Target higher so that later levels these "forced" upgrades happen more rarely

    UtilitySlot utilitySlot;

    public UtilityActions(Person person, NPCActionLogger npcActionLogger, Map<Trait, Integer> profile) {
        super(person, npcActionLogger, profile);
        this.utilitySlot = person.getProperty().getUtilitySlot();
    }

    @Override
    protected void createAllActions() {
        Meadowlands meadowlands = new Meadowlands(person, npcActionLogger,5,profile);
        AlloyMine alloyMine = new AlloyMine(person, npcActionLogger,5,profile);
        GoldMine goldMine = new GoldMine(person, npcActionLogger,5,profile);
        MysticMine mysticMine = new MysticMine(person, npcActionLogger,5,profile);
        SlaveFacility slaveFacility = new SlaveFacility(person, npcActionLogger,5,profile);
        WorkerCenter workerCenter = new WorkerCenter(person, npcActionLogger,5,profile);

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


        public GoldMine(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
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
                UtilityShop.buyBuilding(UtilityBuildings.GoldMine, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget = 15;
                    UtilityShop.upgradeBuilding(UtilityBuildings.GoldMine, person);
                }
            }

        }
    }



    class Meadowlands extends WeightedObject {

        private int amountsReached = 0;

        public Meadowlands(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }


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
                UtilityShop.buyBuilding(UtilityBuildings.MeadowLands, person);
            } else {
                if (counter > counterTarget) {
                    counter = 0;
                    counterTarget = 15;
                    UtilityShop.upgradeBuilding(UtilityBuildings.MeadowLands, person);
                }
            }
        }
    }

    class AlloyMine extends WeightedObject {

        private int amountsReached = 0;

        public AlloyMine(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }


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
                UtilityShop.buyBuilding(UtilityBuildings.AlloyMine, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget = 15;
                    UtilityShop.upgradeBuilding(UtilityBuildings.AlloyMine, person);
                }
            }
        }
    }
    class MysticMine extends WeightedObject {


        public MysticMine(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
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
                UtilityShop.buyBuilding(UtilityBuildings.MysticMine, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget = 15;
                    UtilityShop.upgradeBuilding(UtilityBuildings.MysticMine, person);
                }
            }
        }
    }

    class SlaveFacility extends WeightedObject {


        public SlaveFacility(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
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
                UtilityShop.buyBuilding(UtilityBuildings.SlaveFacility, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget = 15;
                    UtilityShop.upgradeBuilding(UtilityBuildings.SlaveFacility, person);

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


        public WorkerCenter(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
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
                UtilityShop.buyBuilding(UtilityBuildings.WorkerCenter, person);
            } else {
                if(counter > counterTarget) {
                    counter = 0;
                    counterTarget = 15;
                    UtilityShop.upgradeBuilding(UtilityBuildings.WorkerCenter, person);

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



