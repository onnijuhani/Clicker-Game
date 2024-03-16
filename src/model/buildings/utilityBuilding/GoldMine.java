package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Character;
import model.stateSystem.EventTracker;
import model.resourceManagement.TransferPackage;

public class GoldMine extends UtilityBuilding {

    private int production; //production is always per month

    public GoldMine(int basePrice, Character owner) {
        super(basePrice, owner);
        this.production = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.GoldMine;
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
            return false;
        } else {
            owner.getEventTracker().addEvent(EventTracker.Message("Error", "Max level reached"));
            return true;
        }
    }

    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                        production + " Gold"
        );
    }
    @Override
    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(0,0, production);
        owner.getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
    }

}