package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;

public class Manor extends Property {
    public Manor(String name) {
        super(PropertyConfig.MANOR, name);
        this.propertyEnum = Properties.Manor;
    }
}
