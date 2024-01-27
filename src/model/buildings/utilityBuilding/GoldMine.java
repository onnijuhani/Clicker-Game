package model.buildings.utilityBuilding;

import model.Settings;

public class GoldMine extends UtilityBuilding {

    private int production; //production is always per month

    public GoldMine(int basePrice) {
        super(basePrice);
        this.production = Settings.get("mineProduction");
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