package model.characters.player.clicker;

import model.shop.UpgradeSystem;

import model.resourceManagement.Resource;

public class ClickerTools extends UpgradeSystem {
    protected final int MAX_LEVEL = 5;

    public ClickerTools(int basePrice, Resource type){
        super(basePrice);
    }


}
