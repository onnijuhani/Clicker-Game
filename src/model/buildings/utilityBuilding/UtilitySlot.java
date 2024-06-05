package model.buildings.utilityBuilding;

import model.characters.payments.PaymentManager;
import model.characters.payments.PaymentTracker;

import java.util.HashMap;

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
    public void updatePaymentCalendar(PaymentManager calendar) {
        ownedUtilityBuildings.forEach((key, building) -> {
            building.updatePaymentCalendar(calendar);
        });
    }
}

