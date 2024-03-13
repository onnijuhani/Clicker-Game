package model.buildings;

import model.Settings;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Character;
import model.stateSystem.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Vault;
import model.resourceManagement.wallets.Wallet;
import model.worldCreation.Quarter;

public class Construct {

    public static void constructProperty(Properties type, Character character){

        Property oldHouse = character.getProperty();
        Quarter location = oldHouse.getLocation();
        UtilitySlot oldUtilitySlot = oldHouse.getUtilitySlot();
        Vault oldVault = oldHouse.getVault();
        String oldName = oldHouse.getName();

        Wallet wallet = character.getWallet();

        TransferPackage cost = getCost(type);

        if (wallet.hasEnoughResources(cost)) {

            wallet.subtractResources(cost);
            character.getEventTracker().addEvent(EventTracker.Message("Major", "New property constructed"));

            Property newHouse = initiateNewProperty(type, oldName);
            assert newHouse != null;
            newHouse.setLocation(location);
            newHouse.setVault(oldVault);
            newHouse.getUtilitySlot().setOwnedUtilityBuildings(oldUtilitySlot.getOwnedUtilityBuildings());
            newHouse.setOwner(character);

            character.setProperty(newHouse);

        }
        character.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough resources for construction"));


    }

    private static Property initiateNewProperty(Properties type, String oldName) {
        switch (type){
            case Shack:
                return new Shack(oldName);
            case Cottage:
                return new Cottage(oldName);
            case Villa:
                return new Villa(oldName);
            case Mansion:
                return new Mansion(oldName);
            case Manor:
                return new Manor(oldName);
            case Castle:
                return new Castle(oldName);
            case Citadel:
                return new Citadel(oldName);
            case Fortress:
                return new Fortress(oldName);
            default:
                return null;
        }
    }

    public static TransferPackage getCost(Properties type) {
        int baseFood = Settings.get("constructBaseFood");
        int baseAlloy = Settings.get("constructBaseAlloy");
        int baseGold = Settings.get("constructBaseGold");

        int multiplier = (int) Math.pow(2, type.ordinal()); // 2 to the power of the ordinal value

        int foodCost = baseFood * multiplier;
        int alloyCost = baseAlloy * multiplier;
        int goldCost = baseGold * multiplier;

        return new TransferPackage(foodCost, alloyCost, goldCost);
    }

}
