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
import model.stateSystem.MessageTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.PopUpMessageTracker;
import model.time.EventManager;
import model.time.PropertyManager;
import model.war.Military;
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

        if(newType == Properties.Castle){
            if(person.getRole().getNation().isAtWar()){
                // cannot upgrade into military property if nation is at war
                return;
            }
        }


        TransferPackage cost = getCost(person);

        assert cost != null;


        if(!wallet.hasEnoughResources(cost)){
            person.getRole().getNation().getShop().getExchange().forceAcquire(cost, person, false);
        }


        if (wallet.hasEnoughResources(cost)) {

            person.removeAspiration(Aspiration.UPGRADE_PROPERTY);
            person.removeAspiration(Aspiration.SAVE_RESOURCES);

            wallet.subtractResources(cost);

            assert newType != null;

            GameEvent gameEvent = new GameEvent(Event.CONSTRUCTION, person);
            person.getEventTracker().addEvent(MessageTracker.Message("Minor", "Construction of "+newType+ " started"));

            int daysUntilEvent = getDaysUntilEvent(newType);

            EventManager.scheduleEvent(() -> {
                Construct.finalizeConstruction(person, newType, location, oldHouse, oldUtilitySlot);
            }, daysUntilEvent, gameEvent);
        } else {
            if (person.isPlayer()){
            person.getEventTracker().addEvent(MessageTracker.Message("Error", "Not enough resources for construction"));
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
        person.getEventTracker().addEvent(MessageTracker.Message("Major", "New property constructed"));

        newHouse.maintenance.updatePaymentManager(person.getPaymentManager());


        if(person.isPlayer()) {
            triggerConstructionPopUp(newType);
        }


    }

    private static void triggerConstructionPopUp(Properties type) {
        String headline = "Construction Ready";
        String mainText = "";
        String imagePath;
        String buttonText = "Great";

        switch (type) {
            case Cottage -> mainText = "Cottage Constructed!\nThis humble home adds a new utility slot, allowing you to harness more resources from the land. It also comes with higher maintenance costs.";
            case Villa -> mainText = "Your new Villa stands proud!\nThis elegant structure adds another utility slot, increasing your ability to gather resources.";
            case Mansion -> mainText = "Behold the Mansion!\nWith its grandeur, it adds another utility slot to your domain, further boosting your resource gathering.";
            case Manor -> mainText = "The newly constructed noble Manor is a testament to your growing influence.\nIt adds the final utility slot, maximizing your resource collection potential.";
            case Castle -> mainText = """
                    After all this time...
                    The Castle marks a brand new era in your reign. As the majestic structure rises from the ground, its formidable walls offer unparalleled protection.
                    Now, you command an army, ready to defend your lands and conquer new territories. Military mechanics are now at your disposal, allowing you to train soldiers,
                    fortify defenses, and expand your influence. Your journey to greatness takes a significant leap forward as you transform from a mere ruler to a strategic commander.""";
            case Citadel -> mainText = "Your Citadel is a symbol of unmatched strength.\nIt's devastating power surpasses your old Castle and takes you to new dimension.";
            case Fortress -> mainText = "The Fortress is the pinnacle of your territorial might. Standing tall and impregnable, it is a testament to your unyielding power and strategic brilliance. "
                    + "The reinforced walls and state-of-the-art defenses make it the most formidable structure in your domain. With the Fortress, your ability to withstand sieges and "
                    + "launch powerful counterattacks is unmatched. Your influence over the land is now solidified, and you are recognized as a dominant force, capable of shaping the fate of kingdoms. "
                    + "This monumental achievement marks the zenith of your architectural and military prowess, securing your legacy as an unassailable ruler.";
        }

        imagePath = switch (type) {
            case Cottage -> "Properties/cottagePop.jpg";
            case Villa -> "Properties/villaPop.jpg";
            case Mansion -> "Properties/mansionPop.jpg";
            case Manor -> "Properties/manorPop.jpg";
            case Castle -> "Properties/castlePop.jpg";
            case Citadel -> "Properties/citadelPop.jpg";
            case Fortress -> "Properties/fortressPop.jpg";
            default -> null;
        };

        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(headline, mainText, imagePath, buttonText);
        PopUpMessageTracker.sendMessage(message);
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

        if(oldHouse instanceof Military military){
            Military newMilitary = (Military) newHouse;
            newMilitary.setArmy(military.getArmy());
        }



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
            case Cottage -> new Castle(oldName, person); //TODO CORRECT THIS!!!!!!!!!!!!!!!!
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
