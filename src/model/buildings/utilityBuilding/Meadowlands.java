package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Character;
import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;

public class Meadowlands extends UtilityBuilding {

    public Meadowlands(int basePrice, Character owner) {
        super(basePrice, owner);
        this.production = Settings.get("meadowLandsProduction");
        this.name = UtilityBuildings.MeadowLands;
    }
    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                "Produces " + production + " Food"
                );
    }
    @Override
    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(production,0,0);
        owner.getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
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
