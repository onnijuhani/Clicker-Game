package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.PropertyConfig;
import model.characters.Person;
import model.war.Military;

public class Castle extends MilitaryProperty implements Military {
    public Castle(String name, Person owner) {
        super(PropertyConfig.CASTLE, name, owner);
        this.propertyEnum = Properties.Castle;
    }
}
