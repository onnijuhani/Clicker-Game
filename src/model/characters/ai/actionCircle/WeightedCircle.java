package model.characters.ai.actionCircle;

import java.util.LinkedList;
import java.util.List;

public class WeightedCircle implements Circle<WeightedObject> {

    private final LinkedList<WeightedObject> circle = new LinkedList<>();
    private final int weightThreshold;
    private final int weightIncrement;

    /**
     * Constructor for creating a WeightedCircle with specified weight threshold and weight increment.
     * @param weightThreshold The minimum weight an object must have to be selected.
     * @param weightIncrement The amount by which each object's weight is increased after selection.
     */
    public WeightedCircle(int weightThreshold, int weightIncrement) {
        this.weightThreshold = weightThreshold;
        this.weightIncrement = weightIncrement;
    }

    /**
     * Overloaded constructor for creating a WeightedCircle with specified weight threshold and default weight increment of 1.
     * @param weightThreshold The minimum weight an object must have to be selected.
     */
    public WeightedCircle(int weightThreshold) {
        this(weightThreshold, 1);
    }

    @Override
    public String toString(){
        return circle.toString();
    }
    @Override
    public void add(WeightedObject element) {
        circle.add(element);
    }


    public void addAll(List list) {
        circle.addAll(list);
    }

    @Override
    public void remove(WeightedObject element) {
        circle.remove(element);
    }

    @Override
    public WeightedObject selectAndRotate() {
        if (circle.isEmpty()) {
            return null;
        }
        WeightedObject maxWeightObject = null;
        // Etsi ensin suurimman painon objekti
        for (WeightedObject obj : circle) {
            if (maxWeightObject == null || obj.getWeight() > maxWeightObject.getWeight()) {
                maxWeightObject = obj;
            }
        }

        // Kasvata sitten jokaisen objektin painoa
        for (WeightedObject obj : circle) {
            obj.incrementWeight(weightIncrement);
        }

        // Tarkista, täyttääkö löydetty objekti kriteerit valinnan jälkeen
        if (maxWeightObject.getWeight() >= weightThreshold) {
            circle.remove(maxWeightObject);
            maxWeightObject.resetWeight(); // Aseta valitun objektin paino nollaksi
            circle.addLast(maxWeightObject); // Lisää objekti takaisin listan loppuun
            return maxWeightObject; // Palauta valittu objekti
        }
        return null; // Palauta null, jos yksikään objekti ei täytä kriteerejä
    }


    public void executeLoop() {
        if (circle.isEmpty()) {
            return;
        }
        WeightedObject maxWeightObject = null;

        for (WeightedObject obj : circle) {
            if (maxWeightObject == null || obj.getWeight() > maxWeightObject.getWeight()) {
                maxWeightObject = obj;
            }
        }


        for (WeightedObject obj : circle) {
            obj.incrementWeight(weightIncrement);
        }


        if (maxWeightObject.getWeight() >= weightThreshold) {
            circle.remove(maxWeightObject);
            maxWeightObject.resetWeight();
            circle.addLast(maxWeightObject);
            maxWeightObject.execute();
        }

    }

    @Override
    public WeightedObject peek() {
        return circle.peek();
    }



}




