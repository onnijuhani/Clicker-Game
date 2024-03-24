package model.characters.ai.actions;

import model.characters.Person;

import java.util.PriorityQueue;

public class CombatActions {

    private Person person;
    public CombatActions(Person person) {
        this.person = person;
    }
    private PriorityQueue<NPCAction> queue;
}

/**
Should select random enemy to duel but only if there can be found enemy what is weaker than what they are.
IF THEY ARE TOO WEAK TO ATTACK ANYONE, CALL INCREASE PERSONAL ATTACK POWER METHOD EVERY TIME!!!
if there are no enemies SKIP unless they are Aggressive, then always attack randomly. If they are also Attackers, attack no matter what.
if they are Liberal they should always select Slaver as their opponent.
if they are Disloyal, attack allies, superiors and characters in their own city. Disloyal should be able to do attack allies!!!
if they are Passive, they should skip this one ALWAYS unless they are also Slaver, Ambitious or Attacker, then have small chance of attack
if they are defender, they should often skip this one unless they are also Slaver, Ambitious or Aggressive, then have higher chance of attack

Skipping default = increase personal defence
 */
class Duel implements NPCAction{

    @Override
    public void execute() {

    }

    @Override
    public void defaultAction() {

    }

    @Override
    public void defaultSkip() {

    }
}


/**
Execute only if attack level is way higher than what the authority has.
 Often should be skipped.

 ALWAYS skip if they are Loyal, instead add authority as ally every time here (in case they are not for some reason)
 If they are disloyal or ambitious, CALL INCREASE ATTACK POWER IF THEY ARE WEAK AND ALSO UPGRADE GOLD MINE.

 if Liberal has Slaver as authority, they should always increase attack power and upgrade gold production.
 Liberal and Slaver if they are Authority themselves, should always upgrade Defences here UNLESS they are also Ambitious and Aggressive,
 then increase attack.

 Unambitious should Vault resources here.

 Skipping here should improve either personal or property defence.
 */
class AuthorityBattle implements NPCAction{


    @Override
    public void execute() {

    }

    @Override
    public void defaultAction() {

    }

    @Override
    public void defaultSkip() {

    }
}


/**
 EVERYONE SHOULD CONSIDER THIS IF THEY ARE LOW ON RESOURCES AND THERE IS GOOD TARGET AVAILABLE
 Aggressive attacks here everytime no matter what.
 Passive only considers this if they are very low on resources.
 Disloyal targets allies (if he has any)

 SKIPPING =  INCREASE PROPERTY DEFENCE
 */
class Robbery implements NPCAction{


    @Override
    public void execute() {

    }

    @Override
    public void defaultAction() {

    }

    @Override
    public void defaultSkip() {

    }
}




