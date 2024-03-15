package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.characters.Character;

public class Manor extends Property {
    public Manor(String name, Character owner) {
        super(PropertyConfig.MANOR, name, owner);
        this.propertyEnum = Properties.Manor;
    }
}
