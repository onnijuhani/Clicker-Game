package model.buildings.properties;

import model.buildings.MilitaryBuilding;
import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.characters.Person;

public class Castle extends Property implements MilitaryBuilding {
    public Castle(String name, Person owner) {
        super(PropertyConfig.CASTLE, name, owner);
        this.propertyEnum = Properties.Castle;
    }
}
