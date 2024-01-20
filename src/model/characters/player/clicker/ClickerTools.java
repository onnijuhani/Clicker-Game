package model.characters.player.clicker;

public class ClickerTools{
    protected double resourceAmount = 1;
    public ClickerTools(){
    }
    public double getResourceAmount(){
        return resourceAmount;
    }
    public void increaseAmount(){
        resourceAmount++;
    }
}
