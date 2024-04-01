package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.PropertyConfig;
import model.characters.Person;
import model.war.Military;

public class Fortress extends MilitaryProperty implements Military {

    public Fortress(String name, Person owner) {
        super(PropertyConfig.FORTRESS, name, owner);
        this.propertyEnum = Properties.Fortress;
    }
}
