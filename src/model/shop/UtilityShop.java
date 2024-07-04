package model.shop;

import model.Settings;
import model.buildings.Property;
import model.buildings.utilityBuilding.*;
import model.characters.Character;
import model.characters.Person;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.Event;
import model.stateSystem.MessageTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.SpecialEventsManager;
import model.time.EventManager;

import static model.stateSystem.State.SABOTEUR;

public class UtilityShop extends ShopComponents {
    public UtilityShop(Wallet wallet) {
        super(wallet);
    }

    public static boolean upgradeBuilding(UtilityBuildings type, Person person) {
        Property property = person.getProperty();
        UtilitySlot slot = property.getUtilitySlot();
        UtilityBuilding building = slot.getUtilityBuilding(type);

        if (building != null) {
            int upgradePrice = building.getUpgradePrice();
            if (person.getWallet().hasEnoughResource(Resource.Gold, upgradePrice)) {
                person.getWallet().subtractGold(upgradePrice);
                building.increaseLevel();
                if(person.isPlayer()) {
                    person.getEventTracker().addEvent(MessageTracker.Message("Utility",
                            "Successfully upgraded " + type + " to level " +
                                    building.getUpgradeLevel() + "!"));
                }
                property.getUtilitySlot().increaseTotalLevels();
                building.updatePaymentCalendar(person.getPaymentManager());
                return true; // Upgrade was successful
            } else {
                if(person.isPlayer()) {
                    person.getEventTracker().addEvent(MessageTracker.Message("Error", "Insufficient gold to upgrade " + type));
                }
                building.updatePaymentCalendar(person.getPaymentManager());
                return false; // Upgrade failed

            }
        } else {
            person.getEventTracker().addEvent(MessageTracker.Message("Error", type + " not owned."));
            return false; // Building not owned
        }
    }

    public static boolean buyBuilding(UtilityBuildings type, Person person) {

        // Prevent purchasing both SlaveFacility and WorkerCenter
        UtilitySlot slot = person.getProperty().getUtilitySlot();
        if ((type == UtilityBuildings.SlaveFacility && slot.isUtilityBuildingOwned(UtilityBuildings.WorkerCenter)) ||
                (type == UtilityBuildings.WorkerCenter && slot.isUtilityBuildingOwned(UtilityBuildings.SlaveFacility))) {
            if(person.isPlayer()) {
                person.getEventTracker().addEvent(MessageTracker.Message("Error", "Cannot own both " + UtilityBuildings.SlaveFacility + " and " + UtilityBuildings.WorkerCenter));
            }

            return false;
        }


        if(person.getProperty().getUtilitySlot().getSlotAmount() == person.getProperty().getUtilitySlot().usedSlotAmount()){
            if(person.isPlayer()) {
                person.getEventTracker().addEvent(MessageTracker.Message("Error", "No more available utility slots"));
            }
            return false;
        }
        int price = getBuildingPrice(type);
        if (!person.getWallet().hasEnoughResource(Resource.Gold, price)) {
            if(person.isPlayer()) {
                person.getEventTracker().addEvent(MessageTracker.Message("Error", "Insufficient gold to buy " + type));
            }
            return false;
        }

        UtilityBuilding newBuilding = createBuilding(type, price, person.getCharacter());
        if (newBuilding == null) {
            if(person.isPlayer()) {
                person.getEventTracker().addEvent(MessageTracker.Message("Error", "Unknown building type: " + type));
            }
            return false;
        }

        Property property = person.getProperty();
        property.getUtilitySlot().addUtilityBuilding(type, newBuilding);
        person.getWallet().subtractGold(price);
        if(person.isPlayer()) {
            person.getEventTracker().addEvent(MessageTracker.Message("Major", "Successfully purchased " + type + "!")); //This goes to major instead to see wtf npc is doing
        }
        property.getUtilitySlot().increaseTotalLevels();
        newBuilding.updatePaymentCalendar(person.getPaymentManager());

        if(person.isPlayer()){
            if(type == UtilityBuildings.SlaveFacility){
                SpecialEventsManager.triggerFirstSlaveFacilityMessage();
            }
            if(type == UtilityBuildings.WorkerCenter){
                SpecialEventsManager.triggerFirstWorkerFacilityMessage();
            }
            if(type == UtilityBuildings.MysticMine){
                SpecialEventsManager.triggerFirstMysticMineMessage();
            }
        }

        return true;
    }

    public static int getBuildingPrice(UtilityBuildings type) {
        return switch (type) {
            case MeadowLands -> Settings.getInt("meadowLandsCost");
            case AlloyMine -> Settings.getInt("alloyMineCost");
            case GoldMine -> Settings.getInt("goldMineCost");
            case MysticMine -> Settings.getInt("mysticMineCost");
            case SlaveFacility -> Settings.getInt("slaveFacilityCost");
            case WorkerCenter -> Settings.getInt("workerCenterCost");
            default -> Integer.MAX_VALUE; // Unknown type
        };
    }

    public static UtilityBuilding createBuilding(UtilityBuildings type, int price, Character character) {
        return switch (type) {
            case MeadowLands -> new Meadowlands(price, character.getPerson());
            case AlloyMine -> new AlloyMine(price, character.getPerson());
            case GoldMine -> new GoldMine(price, character.getPerson());
            case MysticMine -> new MysticMine(price, character.getPerson());
            case SlaveFacility -> new SlaveFacility(price, character.getPerson());
            case WorkerCenter -> new WorkerCenter(price, character.getPerson());
            default -> null; // Unknown type
        };
    }

    public static void sabotage(UtilityBuildings type, Person owner, Person saboteur) {
        UtilityBuilding utilityBuilding = owner.getProperty().getUtilitySlot().getUtilityBuilding(type);

        if(saboteur.hasState(SABOTEUR)){
            String timeLeft = saboteur.getAnyOnGoingEvent(Event.SABOTEUR).getTimeLeftString();
            saboteur.getEventTracker().addEvent(MessageTracker.Message("Error", timeLeft +" until sabotage is available"));
            return;
        }

        if(utilityBuilding == null){
            saboteur.getEventTracker().addEvent(MessageTracker.Message("Error", owner + " does not own " + type));
            return;
        }

        int currentLevel = utilityBuilding.getUpgradeLevel();

        if(currentLevel == 1){
            saboteur.getEventTracker().addEvent(MessageTracker.Message("Error", "Cannot sabotage level 1 " + type));
            return;
        }

        int sabotagePower = saboteur.getCombatStats().getOffenseLevel();
        int defencePower = owner.getProperty().getDefenceStats().getUpgradeLevel();

        int minimum = Settings.getRandom().nextInt(3,500);

        int sabotagePoints = Math.max(1, (int)Math.ceil((sabotagePower - 0.5 * defencePower) / 2.0));
        sabotagePoints = Math.max(Math.min(sabotagePoints, currentLevel), minimum);
        sabotagePoints = Math.min(sabotagePoints, currentLevel);

        for(int i = 0; i < sabotagePoints; i++) {
            GameEvent gameEvent = new GameEvent(Event.SABOTEUR, owner);
            int time = 5 * i + 5;
            EventManager.scheduleEvent(() -> commitSabotage(utilityBuilding, owner.getEventTracker()), time , gameEvent);
        }

        int days = 90;

        GameEvent gameEvent = new GameEvent(Event.SABOTEUR, saboteur);

        EventManager.scheduleEvent(() -> removeSaboteur(saboteur), days, gameEvent);

        saboteur.addState(SABOTEUR);
        saboteur.getEventTracker().addEvent(MessageTracker.Message("Minor",
                String.format("Sabotage action committed against %s.\nTheir %s will be reduced by %d levels." +
                "\nYou are unable to make new sabotage actions for next %s days",
                        owner, type, sabotagePoints, days)));

    }

    public static void removeSaboteur(Person saboteur){
        saboteur.removeState(SABOTEUR);
        saboteur.getEventTracker().addEvent(MessageTracker.Message("Minor", "Sabotage is now available."));
    }

    public static void commitSabotage(UtilityBuilding sabotagedBuilding, MessageTracker tracker){
        sabotagedBuilding.decreaseLevel();
        tracker.addEvent(MessageTracker.Message("Minor", "Your " + sabotagedBuilding + " was sabotaged."));
    }


}
