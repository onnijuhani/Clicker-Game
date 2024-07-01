package model.war;

import model.characters.Person;

/**
 * Military is part of the property, Army is then part of the military.
 */
public interface Military {

    Army getArmy();

    void setArmy(Army army);

    Person getOwner();

    default int getMilitaryStrength(){
        return getArmy().getTotalStrength();
    }
    default Army.ArmyState getState(){
        return getArmy().getState();
    }
}
