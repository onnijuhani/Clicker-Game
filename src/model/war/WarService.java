package model.war;

import model.worldCreation.Nation;

public class WarService {


    public static War startWar(Nation attacker, Nation defender) {

        War war = new War(attacker, defender);

        attacker.startWar(defender, war);
        defender.startWar(attacker, war);

        return war;

    }


}
