package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Character;

public class GoldMine extends UtilityBuilding {

    private int production; //production is always per month

    public GoldMine(int basePrice, Character owner) {
        super(basePrice, owner);
        this.production = Settings.get("mineProduction");
        this.name = UtilityBuildings.GoldMine;
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