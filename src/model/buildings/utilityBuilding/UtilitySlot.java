package model.buildings.utilityBuilding;

import model.Settings;
import model.buildings.Property;
import model.characters.Character;
import model.characters.payments.PaymentManager;
import model.characters.payments.PaymentTracker;
import model.shop.UtilityShop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class UtilitySlot implements PaymentTracker {

    private final int slotAmount;
    private HashMap<UtilityBuildings,UtilityBuilding> ownedUtilityBuildings;

    public void increaseTotalLevels() {
       totalUpgradeLevels++;
    }

    public int getTotalUpgradeLevels() {
        return totalUpgradeLevels;
    }

    private int totalUpgradeLevels = 0;

    public UtilitySlot(int slotAmount) {
        this.slotAmount = slotAmount;
        this.ownedUtilityBuildings = new HashMap<>();
    }

    public void addUtilityBuilding(UtilityBuildings type, UtilityBuilding building){
        ownedUtilityBuildings.put(type, building);
    }


    public int getSlotAmount() {
        return slotAmount;
    }

    public boolean isUtilityBuildingOwned(UtilityBuildings type) {
        return ownedUtilityBuildings.containsKey(type);
    }
    public UtilityBuilding getUtilityBuilding(UtilityBuildings type) {
        return ownedUtilityBuildings.get(type);
    }


    public HashMap<UtilityBuildings, UtilityBuilding> getOwnedUtilityBuildings() {
        return ownedUtilityBuildings;
    }

    public void setOwnedUtilityBuildings(HashMap<UtilityBuildings, UtilityBuilding> ownedUtilityBuildings) {
        this.ownedUtilityBuildings = ownedUtilityBuildings;
    }

    public int usedSlotAmount(){
        return getOwnedUtilityBuildings().size();
    }


    @Override
    public void updatePaymentManager(PaymentManager calendar) {
        ownedUtilityBuildings.forEach((key, building) -> {
            building.updatePaymentManager(calendar);
        });
    }

    public void addRandomUtilityBuilding(Character character) {
        UtilityBuildings randomType = getRandomUtilityBuildingType();
        if (randomType != null) {
            createUtilityBuilding(randomType, character);
        }
    }
    private UtilityBuildings getRandomUtilityBuildingType() {
        List<UtilityBuildings> eligibleTypes = Arrays.stream(UtilityBuildings.values())
                .filter(type -> type != UtilityBuildings.SlaveFacility && type != UtilityBuildings.WorkerCenter)
                .toList();

        if (eligibleTypes.isEmpty()) {
            return null;
        }

        Random random = Settings.getRandom();
        return eligibleTypes.get(random.nextInt(eligibleTypes.size()));
    }
    private void createUtilityBuilding(UtilityBuildings type, Character character) {
        UtilityBuilding newBuilding = UtilityShop.createBuilding(type,UtilityShop.getBuildingPrice(type), character);
        Property property = character.getPerson().getProperty();
        property.getUtilitySlot().addUtilityBuilding(type, newBuilding);
    }

    public int getAnyLevel(UtilityBuildings  type){
        return getUtilityBuilding(type).getUpgradeLevel();
    }
}

