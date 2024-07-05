package model.war;

import model.worldCreation.Nation;

import static model.war.WarManager.State.PHASE1;

public class WarManager {
    public enum State {
        PHASE1, PHASE2, PHASE3
    }




    private State state;
    private final Nation attacker;

    private final Nation defender;




    public WarManager(Nation attacker, Nation defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.state = PHASE1;
    }









}
