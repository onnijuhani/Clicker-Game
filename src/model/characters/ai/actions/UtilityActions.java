package model.characters.ai.actions;

import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.Person;
import model.characters.Status;
import model.characters.Trait;
import model.characters.ai.Aspiration;
import model.characters.ai.actionCircle.WeightedObject;
import model.shop.UtilityShop;
import model.stateSystem.State;
import model.time.Time;

import java.util.List;
import java.util.Map;

/**
 * THE IMPORTANCE OF THESE SHOULD BE VERY LOW.
 */
@SuppressWarnings("CallToPrintStackTrace")
public class UtilityActions extends BaseActions{
    private int counter = 0; // the point of this counter is to make sure every utility building gets upgraded slowly even if no other method does it.
    private int counterTarget = Time.getYear(); // getting the year from Time makes the Target higher so that later levels these "forced" upgrades happen more rarely

    public UtilityActions(Person person, NPCActionLogger npcActionLogger, Map<Trait, Integer> profile) {
        super(person, npcActionLogger, profile);
    }

    @Override
    protected void createAllActions() {
        try {
            Meadowlands meadowlands = new Meadowlands(person, npcActionLogger,1, profile);
            AlloyMine alloyMine = new AlloyMine(person, npcActionLogger,1, profile);
            GoldMine goldMine = new GoldMine(person, npcActionLogger,1, profile);
            MysticMine mysticMine = new MysticMine(person, npcActionLogger,1, profile);
            SlaveFacility slaveFacility = new SlaveFacility(person, npcActionLogger,1, profile);
            WorkerCenter workerCenter = new WorkerCenter(person, npcActionLogger,1, profile);

            allActions.add(meadowlands);
            allActions.add(alloyMine);
            allActions.add(goldMine);
            allActions.add(mysticMine);
            allActions.add(slaveFacility);
            allActions.add(workerCenter);
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
    public List<WeightedObject> getAllActions() {
        return allActions;
    }
    /**
     * Quick return immediately if they are low on resources, this should prevent some problems
     * @return wallet balance information
     */
    private boolean lowBalanceReturn() {
        try {
            return person.getWallet().isLowBalance();
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
    class GoldMine extends WeightedObject {
        public GoldMine(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }
        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            try {
                counter++;
                if (lowBalanceReturn()) return;

                if (!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.GoldMine)) {
                    if(UtilityShop.buyBuilding(UtilityBuildings.GoldMine, person)) {
                        logAction(String.format("Bought %s", this.getClass().getSimpleName()));
                    }
                } else {
                    if(counter > counterTarget) {
                        counter = 0;
                        counterTarget = 15;
                        upgradeBuilding(UtilityBuildings.GoldMine, this);
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
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

            try {
                if(amountsReached < 5){ // make sure farmer doesn't build Meadowlands as his first building
                    if(person.getRole().getStatus() == Status.Farmer){
                        amountsReached++;
                        return;
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
            }
            defaultAction();
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            try {
                counter++;
                if (lowBalanceReturn()) return;
                if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

                if (!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                    if(UtilityShop.buyBuilding(UtilityBuildings.MeadowLands, person)) {
                        logAction(String.format("Bought %s", this.getClass().getSimpleName()));
                    }
                } else {
                    if (counter > counterTarget) {
                        counter = 0;
                        counterTarget = 15;
                        upgradeBuilding(UtilityBuildings.MeadowLands, this);
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
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

            try {
                if(amountsReached < 5){ // make sure miner doesn't build Alloys as his first building
                    if(person.getRole().getStatus() == Status.Miner){
                        amountsReached++;
                        return;
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
            }

            defaultAction();
        }
        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            try {
                counter++;
                if (lowBalanceReturn()) return;
                if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

                if (!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.AlloyMine)) {
                    if (UtilityShop.buyBuilding(UtilityBuildings.AlloyMine, person)){
                        logAction(String.format("Bought %s", this.getClass().getSimpleName()));
                };
                } else {
                    if(counter > counterTarget) {
                        counter = 0;
                        counterTarget = 15;
                        upgradeBuilding(UtilityBuildings.AlloyMine, this);
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
            }
        }
    }
    class MysticMine extends WeightedObject {


        public MysticMine(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            try {
                counter++;
                if (lowBalanceReturn()) return;
                if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

                if (!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MysticMine)) {
                    if(UtilityShop.buyBuilding(UtilityBuildings.MysticMine, person)) {
                        logAction(String.format("Bought %s", this.getClass().getSimpleName()));
                    }
                } else {
                    if(counter > counterTarget) {
                        counter = 0;
                        counterTarget = 15;
                        upgradeBuilding(UtilityBuildings.MysticMine, this);
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
            }
        }
    }

    class SlaveFacility extends WeightedObject {
        boolean joinedSlaverGuild;
        public SlaveFacility(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            try {
                counter++;
                if (lowBalanceReturn()) return;
                if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

                // liberals never build this one
                if (profile.containsKey(Trait.Liberal)) {
                    return;
                }

                if (!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.SlaveFacility)) {
                    if(UtilityShop.buyBuilding(UtilityBuildings.SlaveFacility, person)) {
                        logAction(String.format("Bought %s", this.getClass().getSimpleName()));
                    }
                } else {
                    if(counter > counterTarget) {
                        counter = 0;
                        counterTarget = 15;
                        upgradeBuilding(UtilityBuildings.SlaveFacility, this);

                        // non slavers join the guild after level 5
                        if (person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.SlaveFacility).getUpgradeLevel() == 6) {
                            if(profile.containsKey(Trait.Disloyal) && profile.get(Trait.Disloyal) < 15) {
                                person.getRole().getNation().joinSlaverGuild(person);
                                logAction("Joined Slaver Guild");
                                joinedSlaverGuild = true;
                                person.addState(State.MEMBER_OF_SLAVER);
                            }
                        }
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
            }
        }
        public void slaverAction() {
            try {
                if (lowBalanceReturn()) return;
                if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;


                // slavers join guild immediately
                if (profile.containsKey(Trait.Slaver) && !joinedSlaverGuild) {
                    person.getRole().getNation().joinSlaverGuild(person);
//                    this.setImportance(10);
                    logAction("Joined Slaver Guild");
                    joinedSlaverGuild = true;
                    person.addState(State.MEMBER_OF_SLAVER);
                    if (profile.containsKey(Trait.Liberal)) {
//                        this.setImportance(0);
                    }
                }

                defaultAction();
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
            }
        }
    }

    class WorkerCenter extends WeightedObject {

        boolean joinedLiberalGuild;
        public WorkerCenter(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }
        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            try {
                counter++;
                if (lowBalanceReturn()) return;
                if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

                // slavers never build this one
                if (profile.containsKey(Trait.Slaver)) {
                    return;
                }

                if (!person.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.WorkerCenter)) {
                    if(UtilityShop.buyBuilding(UtilityBuildings.WorkerCenter, person)) {
                        logAction(String.format("Bought %s", this.getClass().getSimpleName()));
                    }
                } else {
                    if(counter > counterTarget) {
                        counter = 0;
                        counterTarget = 15;
                        upgradeBuilding(UtilityBuildings.WorkerCenter, this);

                        // non liberals join the guild after level 5
                        if (person.getProperty().getUtilitySlot().getUtilityBuilding(UtilityBuildings.WorkerCenter).getUpgradeLevel() == 6) {
                            if(profile.containsKey(Trait.Loyal) && profile.get(Trait.Loyal) < 15) {
                                person.getRole().getNation().joinLiberalGuild(person);
                                logAction("Joined Liberal Guild");
                                joinedLiberalGuild = true;
                                person.addState(State.MEMBER_OF_FREEDOM);
                            }
                        }
                    }
                }
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
            }
        }
        public void liberalAction() {
            try {
                if (lowBalanceReturn()) return;
                if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;

                // liberals join immediately
                if (profile.containsKey(Trait.Liberal) && !joinedLiberalGuild) {
                        person.getRole().getNation().joinLiberalGuild(person);
//                        this.setImportance(10);
                        logAction("Joined Liberal Guild");
                        joinedLiberalGuild = true;
                        person.addState(State.MEMBER_OF_FREEDOM);
                    if (profile.containsKey(Trait.Slaver)) {
//                        this.setImportance(0);
                    }
                }
                defaultAction();
            } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
            }
        }
    }
    private void upgradeBuilding(UtilityBuildings building, WeightedObject logger){
        try {
            if (UtilityShop.upgradeBuilding(building, person)) {
                logger.logAction(String.format("Upgraded %s to level %d",
                        building.getClass().getSimpleName(), person.getProperty().getUtilitySlot().getUtilityBuilding(building).getUpgradeLevel()));
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
}



