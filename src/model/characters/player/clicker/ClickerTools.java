package model.characters.player.clicker;

public class ClickerTools{
    protected int resourceAmount = 1;
    public ClickerTools(){
    }
    public int getResourceAmount(){
        return resourceAmount;
    }
    public void increaseAmount(){
        resourceAmount++;
    }
    public void upgradeAmount(){
        resourceAmount *= 2;
    }
}
