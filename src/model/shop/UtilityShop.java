package model.shop;

import model.Settings;
import model.buildings.Property;
import model.buildings.utilityBuilding.*;
import model.characters.Character;
import model.characters.Person;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.EventTracker;

public class UtilityShop extends ShopComponents {
    public UtilityShop(Wallet wallet) {
        super(wallet);
    }

    public boolean upgradeBuilding(UtilityBuildings type, Person person) {
        Property property = person.getProperty();
        UtilitySlot slot = property.getUtilitySlot();
        UtilityBuilding building = slot.getUtilityBuilding(type);

        if (building != null) {
            int upgradePrice = building.getUpgradePrice();
            if (person.getWallet().hasEnoughResource(Resource.Gold, upgradePrice)) {
                person.getWallet().subtractGold(upgradePrice);
                building.increaseLevel();
                if(person.isPlayer()) {
                    person.getEventTracker().addEvent(EventTracker.Message("Shop",
                            "Successfully upgraded " + type + " to level " +
                                    building.getUpgradeLevel() + "!"));
                }
                property.getUtilitySlot().increaseTotalLevels();
                return true; // Upgrade was successful
            } else {
                if(person.isPlayer()) {
                    person.getEventTracker().addEvent(EventTracker.Message("Error", "Insufficient gold to upgrade " + type));
                }
                return false; // Upgrade failed

            }
        } else {
            person.getEventTracker().addEvent(EventTracker.Message("Error", type + " not owned."));
            return false; // Building not owned
        }
    }

    public boolean buyBuilding(UtilityBuildings type, Person person) {

        // Prevent purchasing both SlaveFacility and WorkerCenter
        UtilitySlot slot = person.getProperty().getUtilitySlot();
        if ((type == UtilityBuildings.SlaveFacility && slot.isUtilityBuildingOwned(UtilityBuildings.WorkerCenter)) ||
                (type == UtilityBuildings.WorkerCenter && slot.isUtilityBuildingOwned(UtilityBuildings.SlaveFacility))) {
            if(person.isPlayer()) {
                person.getEventTracker().addEvent(EventTracker.Message("Error", "Cannot own both " + UtilityBuildings.SlaveFacility + " and " + UtilityBuildings.WorkerCenter));
            }
            return false;
        }


        if(person.getProperty().getUtilitySlot().getSlotAmount() == person.getProperty().getUtilitySlot().usedSlotAmount()){
            if(person.isPlayer()) {
                person.getEventTracker().addEvent(EventTracker.Message("Error", "No more available utility slots"));
            }
            return false;
        }
        int price = getBuildingPrice(type);
        if (!person.getWallet().hasEnoughResource(Resource.Gold, price)) {
            if(person.isPlayer()) {
                person.getEventTracker().addEvent(EventTracker.Message("Error", "Insufficient gold to buy " + type));
            }
            return false;
        }

        UtilityBuilding newBuilding = createBuilding(type, price, person.getCharacter());
        if (newBuilding == null) {
            if(person.isPlayer()) {
                person.getEventTracker().addEvent(EventTracker.Message("Error", "Unknown building type: " + type));
            }
            return false;
        }

        Property property = person.getProperty();
        property.getUtilitySlot().addUtilityBuilding(type, newBuilding);
        person.getWallet().subtractGold(price);
        if(person.isPlayer()) {
            person.getEventTracker().addEvent(EventTracker.Message("Major", "Successfully purchased " + type + "!")); //This goes to major instead to see wtf npc is doing
        }
        property.getUtilitySlot().increaseTotalLevels();
        return true;
    }

    private int getBuildingPrice(UtilityBuildings type) {
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

    private UtilityBuilding createBuilding(UtilityBuildings type, int price, Character character) {
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
}
