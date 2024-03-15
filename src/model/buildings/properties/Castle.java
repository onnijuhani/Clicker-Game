package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.characters.Character;

public class Castle extends Property {
    public Castle(String name, Character owner) {
        super(PropertyConfig.CASTLE, name, owner);
        this.propertyEnum = Properties.Castle;
    }
}
