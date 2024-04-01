package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.PropertyConfig;
import model.characters.Person;
import model.war.Military;

public class Citadel extends MilitaryProperty implements Military {

    public Citadel(String name, Person owner) {
        super(PropertyConfig.CITADEL, name, owner);
        this.propertyEnum = Properties.Citadel;
    }
}
