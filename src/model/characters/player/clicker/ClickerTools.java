package model.characters.player.clicker;

import model.shop.UpgradeSystem;

import model.resourceManagement.Resource;

public class ClickerTools extends UpgradeSystem {
    protected int resourceAmount = 1;
    private final Resource type;

    public ClickerTools(int basePrice, Resource type){
        super(basePrice);
        this.type = type;
    }
    public int getResourceAmount(){
        return resourceAmount;
    }

    public void upgradeResourceAmount(){
        resourceAmount *= 2;
    }
    @Override
    public void upgradeLevel() {
        level++;
        upgradeResourceAmount();
    }

}
