package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Person;

public class AlloyMine extends UtilityBuilding {


    public AlloyMine(int basePrice, Person owner) {
        super(basePrice, owner);
        this.production = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.AlloyMine;
    }

    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                production + " Alloys"
        );
    }


}
