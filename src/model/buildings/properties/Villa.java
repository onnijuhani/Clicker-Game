package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.buildings.utilityBuilding.UtilitySlot;

public class Villa extends Property {
    public Villa(String name) {
        super(PropertyConfig.VILLA, name);
        this.propertyEnum = Properties.Villa;
        this.utilitySlot = new UtilitySlot(3);
    }
}
