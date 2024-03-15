package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.buildings.utilityBuilding.UtilitySlot;

public class Cottage extends Property {
    public Cottage(String name) {
        super(PropertyConfig.COTTAGE, name);
        this.propertyEnum = Properties.Cottage;
        this.utilitySlot = new UtilitySlot(3);
    }
}
