package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Person;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;

public class Meadowlands extends UtilityBuilding {
    private int production;

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
    @Override
    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(production,0,0);
        owner.getPerson().getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
    }




    public int getProduction() {
        return production;
    }

    public void upgradeProduction() {
        this.production = production * 2;
    }

    @Override
    public boolean upgradeLevel() {
        if (level < MAX_LEVEL) {
            level++;
            upgradeProduction();
            return true;
        } else {
            owner.getEventTracker().addEvent(EventTracker.Message("Error", "Max level reached"));
            return false;
        }
    }

}
