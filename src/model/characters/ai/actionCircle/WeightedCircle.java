package model.characters.ai.actionCircle;

import model.Settings;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class WeightedCircle implements Circle<WeightedObject> {

    private final Queue<WeightedObject> circle = new PriorityQueue<>();
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
            circle.add(maxWeightObject);
            return maxWeightObject;
        }
        return null;
    }


    public void executeLoop() {

        if(Settings.DB){System.out.println("loop 1");}

        if (circle.isEmpty()) {
            return;
        }
        WeightedObject maxWeightObject = null;

        if(Settings.DB){System.out.println("loop 2");}
        for (WeightedObject obj : circle) {
            if (maxWeightObject == null || obj.getWeight() > maxWeightObject.getWeight()) {
                maxWeightObject = obj;
            }
        }

        if(Settings.DB){System.out.println("loop 3");}
        for (WeightedObject obj : circle) {
            obj.incrementWeight(weightIncrement);
        }


        if(Settings.DB){System.out.println("loop 4"+maxWeightObject);}
        if (maxWeightObject.getWeight() >= weightThreshold) {
            circle.remove(maxWeightObject);
            maxWeightObject.resetWeight();
            circle.add(maxWeightObject);
            maxWeightObject.execute();
        }
        if(Settings.DB){System.out.println("loop 5");}

    }

    @Override
    public WeightedObject peek() {
        return circle.peek();
    }



}




