package model.buildings.properties;

import model.buildings.Properties;
import model.buildings.Property;
import model.buildings.PropertyConfig;
import model.characters.Person;
import model.war.Army;
import model.war.Military;

public class MilitaryProperty extends Property implements Military {

    protected Army army;

    public MilitaryProperty(PropertyConfig.PropertyValues propertyValues ,String name, Person owner) {
        super(propertyValues, name, owner);
        this.army = new Army(this, owner);
        this.propertyEnum = Properties.Castle;
    }

    public Army getArmy() {
        return army;
    }

    public void setArmy(Army army) {
        this.army.clearResources(); // destroy the army made by constructor
        this.army = army;
    }



}
