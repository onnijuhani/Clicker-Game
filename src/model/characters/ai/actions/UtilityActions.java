package model.characters.ai.actions;

import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Person;
import model.characters.Trait;
import model.characters.ai.actionCircle.WeightedObject;
import model.shop.UtilityShop;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UtilityActions {
    private final Person person;
    private final UtilitySlot utilitySlot;
    private final UtilityShop utilityShop;
    private final List<WeightedObject> allActions = new LinkedList<>();
    private final Map<Trait, Integer> profile;

    public UtilityActions(Person person, Map<Trait, Integer> profile ) {
        this.person = person;
        Property property = person.getProperty();
        this.utilitySlot = property.getUtilitySlot();
        this.utilityShop = property.getLocation().getNation().getShop().getUtilityShop();
        this.profile = profile;
        createAllActions();
    }


    private void createAllActions() {
        Meadowlands meadowlands = new Meadowlands(1, profile);
        AlloyMine alloyMine = new AlloyMine(1, profile);
        GoldMine goldMine = new GoldMine(1, profile);
        MysticMine mysticMine = new MysticMine(2, profile);
        SlaveFacility slaveFacility = new SlaveFacility(1, profile);
        WorkerCenter workerCenter = new WorkerCenter(1, profile);

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


        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            if (lowBalanceReturn()) return;
            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.GoldMine)) {
                utilityShop.buyBuilding(UtilityBuildings.GoldMine, person);
            } else {
                utilityShop.upgradeBuilding(UtilityBuildings.GoldMine, person);
            }
        }
    }



    class Meadowlands extends WeightedObject {
        public Meadowlands(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            if (lowBalanceReturn()) return;
            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                utilityShop.buyBuilding(UtilityBuildings.MeadowLands, person);
            } else {
                utilityShop.upgradeBuilding(UtilityBuildings.MeadowLands, person);
            }
        }
    }
    class AlloyMine extends WeightedObject {
        public AlloyMine(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            if (lowBalanceReturn()) return;
            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.AlloyMine)) {
                utilityShop.buyBuilding(UtilityBuildings.AlloyMine, person);
            } else {
                utilityShop.upgradeBuilding(UtilityBuildings.AlloyMine, person);
            }
        }
    }
    class MysticMine extends WeightedObject {
        public MysticMine(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            if (lowBalanceReturn()) return;
            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.MysticMine)) {
                utilityShop.buyBuilding(UtilityBuildings.MysticMine, person);
            } else {
                utilityShop.upgradeBuilding(UtilityBuildings.MysticMine, person);
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
            if (lowBalanceReturn()) return;
            // liberals never build this one
            if (person.getAiEngine().getProfile().containsKey(Trait.Liberal)) {
                return;
            }

            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.SlaveFacility)) {
                utilityShop.buyBuilding(UtilityBuildings.SlaveFacility, person);
            } else {
                utilityShop.upgradeBuilding(UtilityBuildings.SlaveFacility, person);

                // non slavers join the guild after level 5
                if (utilitySlot.getUtilityBuilding(UtilityBuildings.SlaveFacility).getUpgradeLevel() > 5) {
                    person.getRole().getNation().joinSlaverGuild(person);
                }
            }
        }

        public void slaverAction() {
            if (lowBalanceReturn()) return;
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
            if (lowBalanceReturn()) return;
            // slavers never build this one
            if (person.getAiEngine().getProfile().containsKey(Trait.Slaver)) {
                return;
            }

            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.WorkerCenter)) {
                utilityShop.buyBuilding(UtilityBuildings.WorkerCenter, person);
            } else {
                utilityShop.upgradeBuilding(UtilityBuildings.WorkerCenter, person);

                // non liberals join the guild after level 5
                if (utilitySlot.getUtilityBuilding(UtilityBuildings.WorkerCenter).getUpgradeLevel() > 5) {
                    person.getRole().getNation().joinLiberalGuild(person);
                }
            }
        }

        public void liberalAction() {
            if (lowBalanceReturn()) return;
            // liberals join immediately
            if (person.getAiEngine().getProfile().containsKey(Trait.Liberal)) {
                    person.getRole().getNation().joinLiberalGuild(person);
            }
            defaultAction();
        }
    }

}



