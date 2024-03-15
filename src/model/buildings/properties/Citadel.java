package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;

public class Citadel extends Property {
    public Citadel(String name) {
        super(PropertyConfig.CITADEL, name);
        this.propertyEnum = Properties.Citadel;
    }
}
