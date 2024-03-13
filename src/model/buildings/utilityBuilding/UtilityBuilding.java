package model.buildings.utilityBuilding;

import model.characters.Character;
import model.stateSystem.EventTracker;
import model.resourceManagement.TransferPackage;
import model.shop.UpgradeSystem;
import model.time.UtilityManager;
import model.time.UtilityObserver;

public class UtilityBuilding extends UpgradeSystem implements UtilityObserver {

    protected UtilityBuildings name;

    protected Character owner;

    @Override
    public void utilityUpdate() {
        generateAction();
    }
    @Override
    public int getUpgradePrice() {
        return basePrice * (int) Math.pow(2, level );
    }

    public String getInfo(){
        return "empty";
    }

    public UtilityBuilding(int basePrice, Character owner) {
        super(basePrice);
        this.owner = owner;
        UtilityManager.subscribe(this);
    }

    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(0,0,0);
        owner.getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Generate", this.getClass().getSimpleName() + "generated" + transfer));
    }

}
