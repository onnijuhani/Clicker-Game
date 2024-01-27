package model.buildings;

import model.buildings.utilityBuilding.UtilitySlot;

public class Shack extends Property{
    public Shack(String name) {
        super(PropertyConfig.SHACK, name + " " + "Shack");
        this.propertyEnum = Properties.Shack;
        this.utilitySlot = new UtilitySlot(2);
    }
}
