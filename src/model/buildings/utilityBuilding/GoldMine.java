package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Person;

public class GoldMine extends UtilityBuilding {



    public GoldMine(int basePrice, Person owner) {
        super(basePrice, owner);
        this.production = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.GoldMine;
    }




    public void upgradeProduction() {
        this.production = production * 2;
    }



    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                        production + " Gold"
        );
    }


}