package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Character;

public class Villa extends Property {
    public Villa(String name, Character owner) {
        super(PropertyConfig.VILLA, name, owner);
        this.propertyEnum = Properties.Villa;
        this.utilitySlot = new UtilitySlot(3);
    }
}
