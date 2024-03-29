package model.buildings.properties;

import model.buildings.MilitaryBuilding;
import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.characters.Person;

public class Citadel extends Property implements MilitaryBuilding {
    public Citadel(String name, Person owner) {
        super(PropertyConfig.CITADEL, name, owner);
        this.propertyEnum = Properties.Citadel;
    }
}
