package model.war;

import model.characters.Person;
import model.stateSystem.MessageTracker;

public class MilitaryBattleManager {

    public static MilitaryBattle executeMilitaryBattle(Person attacker, Person defender) {

        if(attacker == defender){
            System.out.println("tried to attack self");
            return null;
        }
        if(attacker.getGrandFoundry() == null || defender.getGrandFoundry() == null){
            return null;
        }

        if (attacker.getProperty() instanceof Military military && defender.getProperty() instanceof Military military2) {

            if(military.getArmy().getState() != null){
                attacker.getEventTracker().addEvent(MessageTracker.Message("Error", "Cannot enter military battle, your military is not in Neutral state"));
                return null;
            }
            if(military2.getArmy().getState() != null){
                attacker.getEventTracker().addEvent(MessageTracker.Message("Error", "Cannot enter military battle, opponent's military is not in Neutral state"));
                return null;
            }

            attacker.getEventTracker().addEvent(MessageTracker.Message("Major", String.format("Launched attack against the army of %s", defender)));
            defender.getEventTracker().addEvent(MessageTracker.Message("Major", String.format("Your army is under attack by %s", attacker)));

            return new MilitaryBattle(military, military2);



        } else {
            throw new RuntimeException("Tried to launch attack against non military property");
        }
    }


}
