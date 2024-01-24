package model.characters.player.clicker;

public class ClickerTools{
    protected int resourceAmount = 1;
    public ClickerTools(){
    }
    public double getResourceAmount(){
        return resourceAmount;
    }
    public void increaseAmount(){
        resourceAmount++;
    }
}
