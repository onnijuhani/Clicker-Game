package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Person;
import model.characters.Status;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;

public class Meadowlands extends UtilityBuilding {

    public Meadowlands(int basePrice, Person owner) {
        super(basePrice, owner);
        this.value = Settings.getInt("meadowLandsProduction");
        this.name = UtilityBuildings.MeadowLands;

        if (owner.getPerson().getRole().getStatus() == Status.Farmer) {
            addBonus("Farmer bonus", 0.50);
        }
    }
    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                        value + " Food"
                );
    }
    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(value,0,0);
        owner.getWallet().addResources(transfer);
        if (owner.isPlayer()) {
            owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated" + transfer));
        }
    }

}
