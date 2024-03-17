package model.buildings.utilityBuilding;

import model.characters.Character;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;

import java.util.Arrays;

public class WorkerCenter extends UtilityBuilding {
    private int workerAmount;
    private int slots;
    private final int[] production = {10, 5 ,1};

    public WorkerCenter(int basePrice, Character owner) {
        super(basePrice, owner);
        this.slots = 1;
        this.workerAmount = 1;
        this.name = UtilityBuildings.WorkerCenter;
    }




    public void increaseSlotAmount() {
        this.slots *= 2;
        addWorker();
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
        owner.getPerson().getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
    }

    public void addWorker() {
        if (slots == workerAmount) {
            return;
        }
        workerAmount *= 2;
    }

    public void removeWorker(){
        if (workerAmount > 0) {
            workerAmount--;
        }
    }

    public int getWorkerAmount() {
        return workerAmount;
    }

    public void setWorkerAmount(int workerAmount) {
        this.workerAmount =workerAmount;
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
