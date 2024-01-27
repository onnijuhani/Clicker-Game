package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Character;

public class AlloyMine extends UtilityBuilding {

    private int production; //production is always per month

    public AlloyMine(int basePrice, Character owner) {
        super(basePrice, owner);
        this.production = Settings.get("mineProduction");
        this.name = UtilityBuildings.AlloyMine;
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
