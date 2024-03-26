package model.characters.ai.actionCircle;

/**
 * Circle is a custom data structure I designed as a possible way to run the NPC AI system in the game.
 * It is a data structure where objects inside form a circular structure and are executed one by one.
 */

public interface Circle<T> {

    /**
     * Adds an element to the circle.
     * @param element The element to add.
     */
    void add(T element);

    /**
     * Removes an element from the circle.
     * @param element The element to remove.
     */
    void remove(T element);

    /**
     * Selects the highest priority element based on certain criteria and performs rotation.
     */
    T selectAndRotate();

    /**
     * Returns the next element to be selected without removing it.
     * @return The next element.
     */
    T peek();


}