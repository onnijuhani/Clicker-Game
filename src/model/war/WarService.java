package model.war;

import model.worldCreation.Nation;

public class WarService {


    public static War startWar(Nation attacker, Nation defender) {

        if(attacker == defender){
            throw new RuntimeException(attacker+"Tried to enter war against itself");
        }
        if(attacker.isVassal()){
            if(defender != attacker.getOverlord()) {
                System.out.println(attacker + " is a Vassal. Tried to enter a war against: " + defender + "\nVassals can only attack their overlords");
                return null;
            }
        }
        if(defender.isVassal()){
            System.out.println(defender + " is a Vassal. Attempted attack done by: " + attacker);
            return null;
        }

        String warName;

        if(attacker.isVassal()){
            if(attacker.getOverlord() == defender){
                warName = attacker + "'s War for Independence";
            }else{
                throw new RuntimeException("Vassal attempted to enter a war against someone else than their overlord." +
                        " Overlord: " + attacker.getOverlord() + ". Target: " + defender);
            }
        }else if(attacker.getVassals().size() >= 2){
            warName = attacker + "'s War for Supremacy";
        }else{
            warName = attacker + "'s War for Conquest";
        }





        War war = new War(attacker, defender, warName);

        attacker.startWar(defender, war);
        defender.startWar(attacker, war);

        return war;

    }


}
