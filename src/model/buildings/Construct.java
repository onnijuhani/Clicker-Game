package model.buildings;

import model.Settings;
import model.buildings.properties.*;
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

        Property oldHouse = character.getPerson().getProperty();
        Quarter location = oldHouse.getLocation();
        UtilitySlot oldUtilitySlot = oldHouse.getUtilitySlot();

        Wallet wallet = character.getPerson().getWallet();

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

            GameEvent gameEvent = new GameEvent(Event.CONSTRUCTION, character);
            character.getEventTracker().addEvent(EventTracker.Message("Major", "Construction Started"));

            int daysUntilEvent = getDaysUntilEvent(newType);

            EventManager.scheduleEvent(() -> {
                Construct.finalizeConstruction(character, newType, location, oldHouse, oldUtilitySlot);
            }, daysUntilEvent, gameEvent);
        } else {
            character.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough resources for construction"));
        }
    }

    private static int getDaysUntilEvent(Properties newType) {
        int baseConstructionTime = Settings.getInt("baseConstructionTime");
        double constructionTimeMultiplier = Math.pow(1.6, newType.ordinal());
        return (int) (baseConstructionTime * constructionTimeMultiplier);
    }

    private static void finalizeConstruction(Character character, Properties newType, Quarter location, Property oldHouse, UtilitySlot oldUtilitySlot) {
        Property newHouse = initiateNewProperty(newType, oldHouse.getName(), character);
        newHouse.setFirstTimeReached(false);
        switchPropertyAttributes(character, newHouse, location, oldHouse, oldUtilitySlot);
        character.getEventTracker().addEvent(EventTracker.Message("Major", "New property constructed"));
    }

    private static void switchPropertyAttributes(Character character, Property newHouse, Quarter location, Property oldHouse, UtilitySlot oldUtilitySlot) {
        newHouse.setLocation(location);
        newHouse.getVault().setOwner(null);
        newHouse.getVault().deleteFromGameManager();

        newHouse.setVault(oldHouse.getVault());
        newHouse.getVault().setOwner(newHouse);
        newHouse.getUtilitySlot().setOwnedUtilityBuildings(oldUtilitySlot.getOwnedUtilityBuildings());
        newHouse.setDefense(oldHouse.getDefense());
        PropertyManager.unsubscribe(oldHouse);
        character.getPerson().setProperty(newHouse);
    }

    public static Properties getNextProperty(Character character) {
        Property oldHouse = character.getPerson().getProperty();
        Properties currentType = Properties.valueOf(oldHouse.getClass().getSimpleName());
        Properties[] allProperties = Properties.values();
        if (currentType.ordinal() >= allProperties.length - 1){
            return null;
        }else {
            return allProperties[currentType.ordinal() + 1];
        }
    }

    private static Property initiateNewProperty(Properties type, String oldName, Character character) {
        return switch (type) {
            case Shack -> new Shack(oldName, character.getPerson());
            case Cottage -> new Cottage(oldName, character.getPerson());
            case Villa -> new Villa(oldName, character.getPerson());
            case Mansion -> new Mansion(oldName, character.getPerson());
            case Manor -> new Manor(oldName, character.getPerson());
            case Castle -> new Castle(oldName, character.getPerson());
            case Citadel -> new Citadel(oldName, character.getPerson());
            case Fortress -> new Fortress(oldName, character.getPerson());
        };
    }

    public static TransferPackage getCost(Character character) {

        Properties currentType = Properties.valueOf(character.getPerson().getProperty().getClass().getSimpleName());
        Properties[] allProperties = Properties.values();

        if (currentType.ordinal() == allProperties.length - 1) {
            return null;
        } else {
            Properties newType = getNextProperty(character);
            int baseFood = Settings.getInt("constructBaseFood");
            int baseAlloy = Settings.getInt("constructBaseAlloy");
            int baseGold = Settings.getInt("constructBaseGold");

            assert newType != null;
            int multiplier = (int) Math.pow(3, newType.ordinal()); // 2 to the power of the ordinal value

            int foodCost = baseFood * multiplier;
            int alloyCost = baseAlloy * multiplier;
            int goldCost = baseGold * multiplier;

            return new TransferPackage(foodCost, alloyCost, goldCost);
        }
    }

}
