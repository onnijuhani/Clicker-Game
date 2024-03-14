package model.buildings;

import model.Settings;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Character;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.Event;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.time.PropertyManager;
import model.time.EventManager;
import model.worldCreation.Quarter;

public class Construct {

    public static void constructProperty(Character character) {

        Property oldHouse = character.getProperty();
        Quarter location = oldHouse.getLocation();
        UtilitySlot oldUtilitySlot = oldHouse.getUtilitySlot();
        String oldName = oldHouse.getName();

        Wallet wallet = character.getWallet();

        Properties currentType = Properties.valueOf(oldHouse.getClass().getSimpleName());

        if (currentType == Properties.Fortress) {
            System.out.println("Property is already at maximum level and cannot be upgraded.");
            return;
        }
        Properties newType = getNextProperty(character);
        TransferPackage cost = getCost(character);

        assert cost != null;
        if (wallet.hasEnoughResources(cost)) {

            wallet.subtractResources(cost);

            assert newType != null;

            // THIS SHOULDN'T BE CREATED HERE SINCE IT SUBSCRIBES TO MAINTENANCE PAY WITHOUT OWNER
            Property newHouse = initiateNewProperty(newType, oldName);
            newHouse.setFirstTimeReached(false);

            GameEvent gameEvent = new GameEvent(Event.CONSTRUCTION, character);
            character.getEventTracker().addEvent(EventTracker.Message("Major", "Construction Started"));

            int daysUntilEvent = getDaysUntilEvent(newType);

            EventManager.scheduleEvent(() -> {
                Construct.finalizeConstruction(character, newHouse, location, oldHouse, oldUtilitySlot);
            }, daysUntilEvent, gameEvent);
        } else {
            character.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough resources for construction"));
        }
    }

    private static int getDaysUntilEvent(Properties newType) {
        int baseConstructionTime = 30;
        double constructionTimeMultiplier = Math.pow(1.6, newType.ordinal());
        return (int) (baseConstructionTime * constructionTimeMultiplier);
    }

    private static void finalizeConstruction(Character character, Property newHouse, Quarter location, Property oldHouse, UtilitySlot oldUtilitySlot) {
        switchPropertyAttributes(character, newHouse, location, oldHouse, oldUtilitySlot);
        character.getEventTracker().addEvent(EventTracker.Message("Major", "New property constructed"));
    }

    private static void switchPropertyAttributes(Character character, Property newHouse, Quarter location, Property oldHouse, UtilitySlot oldUtilitySlot) {
        newHouse.setLocation(location);
        newHouse.setVault(oldHouse.getVault());
        newHouse.getUtilitySlot().setOwnedUtilityBuildings(oldUtilitySlot.getOwnedUtilityBuildings());
        newHouse.setOwner(character);
        newHouse.setDefense(oldHouse.getDefense());
        PropertyManager.unsubscribe(oldHouse);
        character.setProperty(newHouse);
    }

    public static Properties getNextProperty(Character character) {
        Property oldHouse = character.getProperty();
        Properties currentType = Properties.valueOf(oldHouse.getClass().getSimpleName());
        Properties[] allProperties = Properties.values();
        if (currentType.ordinal() >= allProperties.length - 1){
            return null;
        }else {
            return allProperties[currentType.ordinal() + 1];
        }
    }

    private static Property initiateNewProperty(Properties type, String oldName) {
        return switch (type) {
            case Shack -> new Shack(oldName);
            case Cottage -> new Cottage(oldName);
            case Villa -> new Villa(oldName);
            case Mansion -> new Mansion(oldName);
            case Manor -> new Manor(oldName);
            case Castle -> new Castle(oldName);
            case Citadel -> new Citadel(oldName);
            case Fortress -> new Fortress(oldName);
        };
    }

    public static TransferPackage getCost(Character character) {

        Properties currentType = Properties.valueOf(character.getProperty().getClass().getSimpleName());
        Properties[] allProperties = Properties.values();

        if (currentType.ordinal() == allProperties.length - 1) {
            return null;
        } else {
            Properties newType = getNextProperty(character);
            int baseFood = Settings.get("constructBaseFood");
            int baseAlloy = Settings.get("constructBaseAlloy");
            int baseGold = Settings.get("constructBaseGold");

            assert newType != null;
            int multiplier = (int) Math.pow(2, newType.ordinal()); // 2 to the power of the ordinal value

            int foodCost = baseFood * multiplier;
            int alloyCost = baseAlloy * multiplier;
            int goldCost = baseGold * multiplier;

            return new TransferPackage(foodCost, alloyCost, goldCost);
        }
    }

}
