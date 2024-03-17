package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Person;

public class Shack extends Property {
    public Shack(String name, Person owner) {
        super(PropertyConfig.SHACK, name, owner);
        this.propertyEnum = Properties.Shack;
        this.utilitySlot = new UtilitySlot(2);
    }
}
