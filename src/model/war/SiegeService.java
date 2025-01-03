package model.war;

import model.characters.Person;
import model.stateSystem.MessageTracker;

public class SiegeService {

    public static MilitaryBattle executeMilitaryBattle(Person attacker, Person defender) {

        if(attacker == defender){

            throw new RuntimeException("Tried to attack self. Attacker and nation: " + attacker +" "+ attacker.getRole().getNation() +
                    " Defender and nation nation: " + defender +" "+ defender.getRole().getNation());
        }
        if(attacker.getGrandFoundry() == null || defender.getGrandFoundry() == null){
            return null;
        }

        if (checkIfArmyIsNull(attacker, defender)) return null;

        if (attacker.getProperty() instanceof Military military && defender.getProperty() instanceof Military military2) {

            if(!military.getArmy().isAvailable()){
                attacker.getMessageTracker().addMessage(MessageTracker.Message("Error", "Cannot enter military battle, your military is not in Neutral state"));
                return null;
            }
            if(!military2.getArmy().isAvailable()){
                attacker.getMessageTracker().addMessage(MessageTracker.Message("Error", "Cannot enter military battle, opponent's military is not in Neutral state"));
                return null;
            }

            attacker.getMessageTracker().addMessage(MessageTracker.Message("Major", String.format("Launched attack against the army of %s", defender)));
            defender.getMessageTracker().addMessage(MessageTracker.Message("Major", String.format("Your army is under attack by %s", attacker)));

            return new MilitaryBattle(military, military2);



        } else {
            throw new RuntimeException("Tried to launch attack against non military property");
        }
    }


    private static boolean checkIfArmyIsNull(Person attacker, Person defender) {
        if(attacker.getProperty() instanceof Military military){
            if(military.getArmy() == null){
                return true;
            }
        }

        if(defender.getProperty() instanceof Military military){
            return military.getArmy() == null;
        }
        return false;
    }


}
