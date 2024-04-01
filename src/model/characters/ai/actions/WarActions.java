package model.characters.ai.actions;

import model.buildings.properties.MilitaryProperty;
import model.characters.Person;
import model.characters.Trait;
import model.characters.ai.actionCircle.WeightedObject;
import model.stateSystem.EventTracker;
import model.war.Army;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WarActions {
    private Person person;
    private final Map<Trait, Integer> profile;

    private final List<WeightedObject> allActions = new LinkedList<>();




    public WarActions(Person person, Map<Trait, Integer> profile) {
        this.person = person;
        this.profile = profile;
        createAllActions();
    }

    private void createAllActions() {
        HireSoldiers hireSoldiers = new HireSoldiers(5,profile);

        allActions.add(hireSoldiers);

    }

    class HireSoldiers extends WeightedObject{

        public HireSoldiers(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }


        @Override
        public void defaultAction(){

            if (notMilitaryProperty()) return;

            MilitaryProperty property = (MilitaryProperty) person.getProperty();
            Army army = property.getArmy();

            if(army.hireSoldiers(1)){
                person.getEventTracker().addEvent(EventTracker.Message("Major", "Recruited new Soldier"));
            }else{
                person.getEventTracker().addEvent(EventTracker.Message("Major", "Recruiting new Soldiers went wrong"));
            }

        }

    }

    private boolean notMilitaryProperty() {
        if(!(person.getProperty() instanceof MilitaryProperty)){
            return true;
        }
        return false;
    }

    public List<WeightedObject> getAllActions() {
        return allActions;
    }


}
