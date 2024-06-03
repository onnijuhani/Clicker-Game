package model.characters.player.clicker;

import model.shop.UpgradeSystem;

import model.resourceManagement.Resource;

public class ClickerTools extends UpgradeSystem {


    public ClickerTools(int basePrice, Resource type){
        super(basePrice);
        super.MAX_LEVEL = 10;
        super.priceIncreaseAmount = 2;
    }


}
