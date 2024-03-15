package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Character;

public class Cottage extends Property {
    public Cottage(String name, Character owner) {
        super(PropertyConfig.COTTAGE, name, owner);
        this.propertyEnum = Properties.Cottage;
        this.utilitySlot = new UtilitySlot(3);
    }
}
