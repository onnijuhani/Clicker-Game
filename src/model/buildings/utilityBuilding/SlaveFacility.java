package model.buildings.utilityBuilding;

import model.characters.Character;
import model.stateSystem.EventTracker;
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


    public void increaseProduction() {
        production[0] *= 2;
        production[1] *= 2;
        production[2] *= 2;
    }

    @Override
    public boolean upgradeLevel() {
        if (level < MAX_LEVEL) {
            level++;
            increaseSlotAmount();
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
                        "Food, Alloys and Gold" + "\n" + Arrays.toString(production)
        );
    }
    @Override
    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(production[0],production[1], production[2]);
        owner.getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
    }

    public void addSlave() {
        if (slots == slaveAmount) {
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
