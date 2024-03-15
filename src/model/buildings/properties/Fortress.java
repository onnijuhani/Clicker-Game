package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;

public class Fortress extends Property {

    public Fortress(String name) {
        super(PropertyConfig.FORTRESS, name);
        this.propertyEnum = Properties.Fortress;
    }
}
