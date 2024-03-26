package model.characters.ai.actionCircle;

import model.characters.ai.actions.NPCAction;

public class WeightedObject implements NPCAction {
    protected int weight;
    protected int importance;

    public WeightedObject(int weight) {
        this.weight = weight;
        this.importance = weight;
    }
    public WeightedObject() {
        this.weight = 1;
        this.importance = 1;
    }
    public WeightedObject(int weight, int importance) {
        this.weight = weight;
        this.importance = importance;
    }

    public int getWeight(){
        return weight;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }
    public void resetWeight(){
        this.weight = 1;
    }

    public void execute(){
    }
    @Override
    public void defaultAction() {
    }
    @Override
    public void defaultSkip() {
    }
    public void incrementWeight(int increment){
        this.weight += increment*importance;
    }
    public void setImportance(int importance) {
        this.importance = importance;
    }

    @Override
    public int compareTo(NPCAction o) {
        return 0;
    }
}
