package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;

public class Mansion extends Property {
    public Mansion(String name) {
        super(PropertyConfig.MANSION, name);
        this.propertyEnum = Properties.Mansion;
    }
}
