package model.buildings.utilityBuilding;

import java.util.HashMap;

public class UtilitySlot {

    private final int slotAmount;


    private HashMap<UtilityBuildings,UtilityBuilding> ownedUtilityBuildings;

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


}

