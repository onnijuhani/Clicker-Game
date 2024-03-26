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

public class UtilityActions {
    private final Person person;
    private final UtilitySlot utilitySlot;
    private final UtilityShop utilityShop;

    private final List<WeightedObject> allActions = new LinkedList<>();

    public UtilityActions(Person person) {
        this.person = person;
        Property property = person.getProperty();
        this.utilitySlot = property.getUtilitySlot();
        this.utilityShop = property.getLocation().getNation().getShop().getUtilityShop();
        createAllActions();
    }


    private void createAllActions() {
        GoldMine goldMine = new GoldMine(1);
        SlaveFacility slaveFacility = new SlaveFacility(1);
        WorkerCenter workerCenter = new WorkerCenter(1);


        allActions.add(goldMine);
        allActions.add(slaveFacility);
        allActions.add(workerCenter);
    }

    public List<WeightedObject> getAllActions() {
        return allActions;
    }


    class GoldMine extends WeightedObject {
        public GoldMine(int weight) {
            super(weight);
        }

        @Override
        public void execute() {
            defaultAction();
            defaultSkip();
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
            if (!utilitySlot.isUtilityBuildingOwned(UtilityBuildings.GoldMine)) {
                utilityShop.buyBuilding(UtilityBuildings.GoldMine, person);
            } else {
                utilityShop.upgradeBuilding(UtilityBuildings.GoldMine, person);
            }
        }
    }

    class SlaveFacility extends WeightedObject {

        public SlaveFacility(int weight) {
            super(weight);
        }

        @Override
        public void execute() {

            defaultAction();
            defaultSkip();
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
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
            // slavers join immediately
            if (person.getAiEngine().getProfile().containsKey(Trait.Slaver)) {
                person.getRole().getNation().joinSlaverGuild(person);
            }
            defaultAction();
        }

    }

    class WorkerCenter extends WeightedObject {

        public WorkerCenter(int weight) {
            super(weight);
        }

        @Override
        public void execute() {
            defaultAction();
            defaultSkip();
        }

        /**
         * default is to buy or upgrade the building. Default Skip is to do nothing.
         */
        @Override
        public void defaultAction() {
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
            // liberals join immediately
            if (person.getAiEngine().getProfile().containsKey(Trait.Liberal)) {
                    person.getRole().getNation().joinLiberalGuild(person);
            }
            defaultAction();
        }
    }

}



