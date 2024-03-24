package model.characters.ai.actions;


/**
 * this interface forces the classes to only have the default and the skip. Add special actions if needed:
 * void ambitiousAction();
 * void unambitiousAction();
 * void slaverAction();
 * void liberalAction();
 * void aggressiveAction();
 * void passiveAction();
 * void loyalAction();
 * void disloyalAction();
 * void attackerAction();
 * void defenderAction();
 */

public interface NPCAction extends Comparable<NPCAction> {


    void execute();

    /**
     * if the NPC executes literally what the class name says it's then called defaultAction
     */
    void defaultAction();


    /**
     * if the NPC doesn't execute the class name literally, it then executed the defaultSkip
     */
    void defaultSkip();



}
