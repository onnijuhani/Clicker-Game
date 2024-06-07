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

    void getNpcLogger();

    /**
     * if the NPC executes literally what the class name says it's then called defaultAction
     */
    void defaultAction();


    /**
     * if the NPC doesn't execute the class name literally, it then executed the defaultSkip
     */
    void defaultSkip();

     void ambitiousAction();
 void unambitiousAction();
 void slaverAction();
 void liberalAction();
 void aggressiveAction();
 void passiveAction();
 void loyalAction();
 void disloyalAction();
 void attackerAction();
 void defenderAction();

    /**
     * Compares this NPC action with another to determine order.
     *
     * @param other the other NPCAction to compare to this one
     * @return a negative integer, zero, or a positive integer as this NPCAction
     * is less than, equal to, or greater than the specified NPCAction.
     */
    int compareTo(NPCAction other);
}
