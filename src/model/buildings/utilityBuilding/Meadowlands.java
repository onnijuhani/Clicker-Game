package model.buildings.utilityBuilding;

import model.Settings;

public class Meadowlands extends UtilityBuilding {

    private int production;

    public Meadowlands(int basePrice) {
        super(basePrice);
        this.production = Settings.get("meadowlandsProduction");
    }


    public int getProduction() {
        return production;
    }

    public void upgradeProduction() {
        this.production = production * 2;
    }

    @Override
    public void upgrade() {
        level++;
        upgradeProduction();
    }

}
