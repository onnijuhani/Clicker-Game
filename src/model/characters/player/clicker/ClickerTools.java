package model.characters.player.clicker;

import model.shop.UpgradeSystem;

public class ClickerTools extends UpgradeSystem {


    public ClickerTools(int basePrice){
        super(basePrice);
        super.MAX_LEVEL = 10;
        super.priceIncreaseAmount = 2;
    }


}
