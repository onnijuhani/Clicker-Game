package model.characters.ai.cirle;

public abstract class WeightedObject {
    private final int weight;
    public WeightedObject(int weight) {
        this.weight = weight;
    }
    public abstract  void execute();
    public abstract  int getWeight();
    public abstract  void setWeight(int weight);
    public abstract  void incrementWeight(int increment);

}
