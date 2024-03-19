package model.characters.player.clicker;

import model.shop.UpgradeSystem;

import model.resourceManagement.Resource;

public class ClickerTools extends UpgradeSystem {
    protected int resourceAmount = 1;
    protected final int MAX_LEVEL = 5;

    protected int increaseDivider = 1;

    protected int finalPrice = 0;

    public ClickerTools(int basePrice, Resource type){
        super(basePrice);
    }
    public int getResourceAmount(){
        return resourceAmount;
    }

    public int getUpgradePrice() {
        if(level < MAX_LEVEL){
            return basePrice * (int) Math.pow(2, level -1 );
        }
        else if(level == MAX_LEVEL) {
            setBasePrice(basePrice*2);
            return basePrice * (int) Math.pow(2, level -1 );
        }
        else if(increaseDivider < 32) {
            int priceAfterMaxLevel = basePrice * (int) Math.pow(3, level -1 );
            finalPrice = priceAfterMaxLevel;
            return priceAfterMaxLevel;
        }
        else{
            return finalPrice;
        }
    }

    @Override
    public boolean upgradeLevel() {
        level++;
        if (level <= MAX_LEVEL) {
            resourceAmount *= 2;
        } else {
            int increaseAmount = Math.max(resourceAmount / increaseDivider, 1);
            resourceAmount += increaseAmount;
            increaseDivider *=2;
        }
        return true;
    }

    // haven't tested if this works yet
    public int calculateNextResourceAmount() {
        if (level < MAX_LEVEL) {
            return resourceAmount * 2;
        } else {
            int increaseAmount = Math.max(resourceAmount / increaseDivider, 1);
            return resourceAmount + increaseAmount;
        }
    }

}
