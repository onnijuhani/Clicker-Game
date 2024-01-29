package model.buildings.utilityBuilding;

import model.characters.Character;
import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;

import java.util.Arrays;

public class SlaveFacility extends UtilityBuilding {
    private int slaveAmount;
    private int slots;
    private final int[] production = {10, 5 ,1};

    public SlaveFacility(int basePrice, Character owner) {
        super(basePrice, owner);
        this.slots = 1;
        this.slaveAmount = 1;
        this.name = UtilityBuildings.SlaveFacility;
    }


    public void increaseSlotAmount() {
        this.slots *= 2;
        addSlave();
        increaseProduction();
    }


    public void increaseProduction(){
        production[0] = production[0] * slaveAmount;
        production[1] = production[1] * slaveAmount;
        production[2] = production[2] * slaveAmount;
    }

    @Override
    public void upgrade() {
        level++;
        increaseSlotAmount();
    }

    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                "Produces " + Arrays.toString(production)
        );
    }
    @Override
    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(production[0],production[1], production[2]);
        owner.getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
    }

    public void addSlave(){
        if (slots == slaveAmount){
            return;
        }
        slaveAmount *= 2;
    }

    public void removeSlave(){
        if (slaveAmount > 0) {
            slaveAmount--;
        }
    }

    public int getSlaveAmount() {
        return slaveAmount;
    }

    public void setSlaveAmount(int slaveAmount) {
        this.slaveAmount = slaveAmount;
    }

    public int[] getProduction() {
        return production;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }


}
