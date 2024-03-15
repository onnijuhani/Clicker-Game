package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Character;
import model.stateSystem.EventTracker;
import model.resourceManagement.TransferPackage;

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

    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                production + " Alloys"
        );
    }
    @Override
    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(0,production,0);
        owner.getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
    }

}
