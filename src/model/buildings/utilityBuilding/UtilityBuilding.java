package model.buildings.utilityBuilding;

import model.shop.UpgradeSystem;
import model.time.UtilityObserver;

public class UtilityBuilding extends UpgradeSystem implements UtilityObserver {

    @Override
    public void utilityUpdate() {
        //add methods eventually
    }

    public UtilityBuilding(int basePrice) {
        super(basePrice);
    }

}
