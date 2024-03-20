package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Person;

public class Meadowlands extends UtilityBuilding {


    public Meadowlands(int basePrice, Person owner) {
        super(basePrice, owner);
        this.production = Settings.getInt("meadowLandsProduction");
        this.name = UtilityBuildings.MeadowLands;
    }
    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                production + " Food"
                );
    }

    public void upgradeProduction() {
        this.production = production * 2;
    }

}
