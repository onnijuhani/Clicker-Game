package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Person;
import model.characters.Status;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;

public class AlloyMine extends UtilityBuilding {

    public AlloyMine(int basePrice, Person owner) {
        super(basePrice, owner);
        this.value = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.AlloyMine;

        if (owner.getPerson().getRole().getStatus() == Status.Miner) {
            addBonus("Miner bonus", 0.25);
        }
    }

    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                        value + " Alloys"
        );
    }

    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(0,value,0);
        owner.getWallet().addResources(transfer);
        if (owner.isPlayer()) {
            owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated" + transfer));
        }
    }

}
