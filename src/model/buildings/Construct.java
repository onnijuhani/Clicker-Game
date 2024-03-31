package model.buildings;

import customExceptions.InsufficientResourcesException;
import model.Settings;
import model.buildings.properties.*;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Person;
import model.characters.ai.Aspiration;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.Event;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.time.EventManager;
import model.time.PropertyManager;
import model.worldCreation.Quarter;

public class Construct {

    public static void constructProperty(Person person) throws InsufficientResourcesException {

        Property oldHouse = person.getProperty();
        Quarter location = oldHouse.getLocation();
        UtilitySlot oldUtilitySlot = oldHouse.getUtilitySlot();

        Wallet wallet = person.getWallet();

        Properties currentType = Properties.valueOf(oldHouse.getClass().getSimpleName());

        if (currentType == Properties.Fortress) {
            return;
        }


        Properties newType = getNextProperty(person);
        TransferPackage cost = getCost(person);

        assert cost != null;

        person.getEventTracker().addEvent(EventTracker.Message("Major" , "Tried construction property 1"));

        if(!wallet.hasEnoughResources(cost)){
            person.getRole().getNation().getShop().getExchange().forceAcquire(cost, person, false);
        }
        person.getEventTracker().addEvent(EventTracker.Message("Major" , "Tried construction property 2"));

        if (wallet.hasEnoughResources(cost)) {

            person.removeAspiration(Aspiration.UPGRADE_PROPERTY);
            person.removeAspiration(Aspiration.SAVE_RESOURCES);

            wallet.subtractResources(cost);

            assert newType != null;

            GameEvent gameEvent = new GameEvent(Event.CONSTRUCTION, person);
            person.getEventTracker().addEvent(EventTracker.Message("Major", "Construction of "+newType+ " started"));

            int daysUntilEvent = getDaysUntilEvent(newType);

            EventManager.scheduleEvent(() -> {
                Construct.finalizeConstruction(person, newType, location, oldHouse, oldUtilitySlot);
            }, daysUntilEvent, gameEvent);
        } else {
            if (person.isPlayer()){
            person.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough resources for construction"));
            }
            throw new InsufficientResourcesException("Not enough resources for construction of " + newType, cost);
        }
    }

    private static int getDaysUntilEvent(Properties newType) {
        int baseConstructionTime = Settings.getInt("baseConstructionTime");
        double constructionTimeMultiplier = Math.pow(1.6, newType.ordinal());
        return (int) (baseConstructionTime * constructionTimeMultiplier);
    }

    private static void finalizeConstruction(Person person, Properties newType, Quarter location, Property oldHouse, UtilitySlot oldUtilitySlot) {
        Property newHouse = initiateNewProperty(newType, oldHouse.getName(), person);
        newHouse.setFirstTimeReached(false);
        switchPropertyAttributes(person, newHouse, location, oldHouse, oldUtilitySlot);
        person.getEventTracker().addEvent(EventTracker.Message("Major", "New property constructed"));
    }

    private static void switchPropertyAttributes(Person person, Property newHouse, Quarter location, Property oldHouse, UtilitySlot oldUtilitySlot) {
        newHouse.setLocation(location);
        newHouse.getVault().setOwner(null);
        newHouse.getVault().deleteFromGameManager();

        newHouse.setVault(oldHouse.getVault());
        newHouse.getVault().setOwner(newHouse);
        newHouse.getUtilitySlot().setOwnedUtilityBuildings(oldUtilitySlot.getOwnedUtilityBuildings());
        newHouse.setDefense(oldHouse.getDefenceStats());
        PropertyManager.unsubscribe(oldHouse);
        person.setProperty(newHouse);
    }

    public static Properties getNextProperty(Person person) {
        Property oldHouse = person.getProperty();
        Properties currentType = Properties.valueOf(oldHouse.getClass().getSimpleName());
        Properties[] allProperties = Properties.values();
        if (currentType.ordinal() >= allProperties.length - 1){
            return null;
        }else {
            return allProperties[currentType.ordinal() + 1];
        }
    }

    private static Property initiateNewProperty(Properties type, String oldName, Person person) {
        return switch (type) {
            case Shack -> new Shack(oldName, person);
            case Cottage -> new Cottage(oldName, person);
            case Villa -> new Villa(oldName, person);
            case Mansion -> new Mansion(oldName, person);
            case Manor -> new Manor(oldName, person);
            case Castle -> new Castle(oldName, person);
            case Citadel -> new Citadel(oldName, person);
            case Fortress -> new Fortress(oldName, person);
        };
    }

    public static TransferPackage getCost(Person person) {

        Properties currentType = Properties.valueOf(person.getProperty().getClass().getSimpleName());
        Properties[] allProperties = Properties.values();

        if (currentType.ordinal() == allProperties.length - 1) {
            return null;    // this should never occur and if it does the game probably crashes
        } else {
            Properties newType = getNextProperty(person);
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
