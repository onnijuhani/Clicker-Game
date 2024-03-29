package model.buildings.properties;

import model.buildings.MilitaryBuilding;
import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.characters.Person;

public class Fortress extends Property  implements MilitaryBuilding {

    public Fortress(String name, Person owner) {
        super(PropertyConfig.FORTRESS, name, owner);
        this.propertyEnum = Properties.Fortress;
    }
}
