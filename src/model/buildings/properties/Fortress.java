package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.characters.Character;

public class Fortress extends Property {

    public Fortress(String name, Character owner) {
        super(PropertyConfig.FORTRESS, name, owner);
        this.propertyEnum = Properties.Fortress;
    }
}
