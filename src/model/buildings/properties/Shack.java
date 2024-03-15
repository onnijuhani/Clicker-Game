package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.buildings.utilityBuilding.UtilitySlot;

public class Shack extends Property {
    public Shack(String name) {
        super(PropertyConfig.SHACK, name);
        this.propertyEnum = Properties.Shack;
        this.utilitySlot = new UtilitySlot(2);
    }
}
