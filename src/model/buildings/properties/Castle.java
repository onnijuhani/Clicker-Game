package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;

public class Castle extends Property {
    public Castle(String name) {
        super(PropertyConfig.CASTLE, name);
        this.propertyEnum = Properties.Castle;
    }
}
