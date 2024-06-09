package model.war;

import model.characters.Person;
import model.stateSystem.Event;
import model.stateSystem.GameEvent;
import model.stateSystem.State;
import model.worldCreation.Nation;

public class AreaStateManager {
    private Nation claimingNation;

    private Nation enemyNation;

    private boolean isUnderAttack;
    private double occupationProgress; // 0.0 means no occupation, 1.0 means fully occupied




    private Person attackingCommander; // the person leading the war effort in the area.
    private Person defendingCommander; // the person leading the defence in the area.

    public AreaStateManager(Nation nation) {
        this.claimingNation = nation;
        this.enemyNation = null;
        this.isUnderAttack = false;
        this.occupationProgress = 0.0;
    }


    public Nation getClaimingNation() {
        return claimingNation;
    }

    public void setClaimingNation(Nation claimingNation) {
        if(claimingNation.isAtWar()){
            String e = "Cannot change claims during war. Use the triggerClaimChange() method instead";
            throw new RuntimeException(e);
        }
        this.claimingNation = claimingNation;
    }

    public boolean isUnderAttack() {
        return isUnderAttack;
    }

    public void setUnderAttack(boolean underAttack) {
        if(!claimingNation.isAtWar()){
            String e = "Cannot be under attack if nation is not at war.";
            throw new RuntimeException(e);
        }
        isUnderAttack = underAttack;
    }

    public double getOccupationProgress() {
        return occupationProgress;
    }

    public void setOccupationProgress(double occupationProgress) {
        if(!claimingNation.isAtWar()){
            String e = "Cannot be under attack if nation is not at war.";
            throw new RuntimeException(e);
        }
        this.occupationProgress = occupationProgress;
    }

    public void triggerClaimChange() {
        this.claimingNation = enemyNation;
        this.enemyNation = null;
        this.attackingCommander = null;
        this.defendingCommander = null;
        this.isUnderAttack = false;
        this.occupationProgress = 0.0;
    }

    public boolean isUnderOccupation() {
        return occupationProgress > 0.0;
    }

    public Person getAttackingCommander() {
        return attackingCommander;
    }
    public Person getDefendingCommander() {
        return defendingCommander;
    }


    public void setAttackingCommander(Person attackingCommander) {
        if(claimingNation.isAtWar()){
            this.attackingCommander = attackingCommander;
            return;
        }else{
            if(!claimingNation.isAtWar()){
                String e = "Cannot set attacking commander if nation is not at war.";
                throw new RuntimeException(e);
            }
        }
        this.attackingCommander = null;
    }


    public void setDefendingCommander(Person defendingCommander) {
        if(claimingNation.isAtWar()){
            this.defendingCommander = defendingCommander;
            return;
        }else{
            if(!claimingNation.isAtWar()){
                String e = "Cannot set defending commander if nation is not at war.";
                throw new RuntimeException(e);
            }
        }
        this.defendingCommander = null;
    }


    public void startWar(Nation enemy, Person defendingCommander) {
        this.enemyNation = enemy;
        this.defendingCommander = defendingCommander;
        defendingCommander.addState(State.TRUCE); // truce is set to make sure no one wins authority battle against them.
        GameEvent authorityBattle = defendingCommander.getAnyOnGoingEvent(Event.AuthorityBattle);
        if(!(authorityBattle == null)){
            authorityBattle.abort(Event.AuthorityBattle);
        }

    }
}