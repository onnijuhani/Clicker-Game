package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Person;

public class Mansion extends Property {
    public Mansion(String name, Person owner) {
        super(PropertyConfig.MANSION, name, owner);
        this.propertyEnum = Properties.Mansion;
        this.utilitySlot = new UtilitySlot(4);
    }
}
