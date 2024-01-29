package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Character;
import model.characters.player.EventTracker;
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
    public void upgrade() {
        level++;
        upgradeProduction();
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
