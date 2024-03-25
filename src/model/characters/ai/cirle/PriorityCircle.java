package model.characters.ai.cirle;

import java.util.LinkedList;

public class PriorityCircle implements Circle<WeightedObject> {
    private final LinkedList<WeightedObject> circle = new LinkedList<>();
    private final int weightThreshold;
    private final int weightIncrement;

    /**
     * Constructor for creating a PriorityCircle with specified weight threshold and weight increment.
     * @param weightThreshold The minimum weight an object must have to be selected.
     * @param weightIncrement The amount by which each object's weight is increased after selection.
     */
    public PriorityCircle(int weightThreshold, int weightIncrement) {
        this.weightThreshold = weightThreshold;
        this.weightIncrement = weightIncrement;
    }

    /**
     * Overloaded constructor for creating a PriorityCircle with specified weight threshold and default weight increment of 1.
     * @param weightThreshold The minimum weight an object must have to be selected.
     */
    public PriorityCircle(int weightThreshold) {
        this(weightThreshold, 1);
    }

    @Override
    public void add(WeightedObject element) {
        circle.add(element);
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
        for (WeightedObject obj : circle) {
            obj.incrementWeight(weightIncrement);
            if (maxWeightObject == null || obj.getWeight() > maxWeightObject.getWeight()) {
                maxWeightObject = obj;
            }
        }

        if (maxWeightObject != null && maxWeightObject.getWeight() >= weightThreshold) {
            circle.remove(maxWeightObject);
            maxWeightObject.setWeight(0);
            circle.addLast(maxWeightObject);
            return maxWeightObject;
        }

        return null;
    }

    @Override
    public WeightedObject peek() {
        return circle.peek();
    }

    @Override
    public void rotate() {
        if (!circle.isEmpty()) {
            WeightedObject firstElement = circle.poll(); // Remove the first element
            if (firstElement != null) {
                circle.addLast(firstElement); // Add it back to the end
            }
        }
    }
}

