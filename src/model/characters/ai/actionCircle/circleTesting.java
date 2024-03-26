package model.characters.ai.actionCircle;

import java.util.ArrayList;
import java.util.List;

public class circleTesting {

    static List<String> results1 = new ArrayList<>();
    static List<String> results2 = new ArrayList<>();
    public static void main(String[] args) {
        int amount = 4100;
        int weightThreshold = 45;


        WeightedCircle weightedCircle2 = new WeightedCircle(weightThreshold);
        // Adding weighted objects to the circle again for the second test
        weightedCircle2.add(new WeightedTestObject(10,"testi1"));
        weightedCircle2.add(new WeightedTestObject(7,3,"testi2"));
        weightedCircle2.add(new WeightedTestObject(3,"testi3"));
        weightedCircle2.add(new WeightedTestObject(6, 15,"testi4"));
        weightedCircle2.add(new WeightedTestObject("testi5"));

        for(int i = 0; i < amount; i++) {
            WeightedObject selectedObject = weightedCircle2.selectAndRotate();
            if (selectedObject != null) {
                selectedObject.execute();
                results2.add(selectedObject.toString());
            }
        }

        // Vertaa tuloksia
        if (results1.equals(results2)) {
            System.out.println("Tulokset ovat identtiset.");
        } else {
            System.out.println("Tulokset eivät ole identtiset.");
        }
    }
}



class WeightedTestObject extends WeightedObject {

    public WeightedTestObject(int weight, String name) {
        super(weight);
        this.name = name;
    }
    public WeightedTestObject(String name) {
        super();
        this.name = name;
    }
    public WeightedTestObject(int weight,int importance,String name) {
        super(weight, importance);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private final String name;


    @Override
    public String toString(){
        return name +" "+ weight;
    }
    @Override
    public void execute() {
        System.out.println(toString());
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void incrementWeight(int increment) {
        this.weight += increment*importance;
    }
    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }


}
