package model.characters.player.clicker;

import model.shop.UpgradeSystem;

import model.resourceManagement.Resource;

public class ClickerTools extends UpgradeSystem {
    protected int resourceAmount = 1;

    public ClickerTools(int basePrice, Resource type){
        super(basePrice);
    }
    public int getResourceAmount(){
        return resourceAmount;
    }

    public void upgradeResourceAmount(){
        resourceAmount *= 2;
    }
    @Override
    public boolean upgradeLevel() {
        level++;
        upgradeResourceAmount();
        return true;
    }

}
