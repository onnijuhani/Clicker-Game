package model.buildings.utilityBuilding;

import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.util.Duration;
import model.characters.Character;
import model.characters.Person;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SlaveFacility extends UtilityBuilding {
    private int slaveAmount;
    private int slots;
    private final int[] production = {20, 10 ,2};

    public SlaveFacility(int basePrice, Character owner) {
        super(basePrice, owner);
        this.slots = 1;
        this.slaveAmount = 1;
        this.name = UtilityBuildings.SlaveFacility;
        delayConsequence();
    }

    private void delayConsequence() {
        // this is delayed because of eventTracker
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(new KeyFrame(
                Duration.millis(1000), // Delay before executing the task
                ae -> {
                    try {
                        Platform.runLater(this::payConsequence);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }));
        timeline.setCycleCount(1);
        timeline.play();
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

    private void payConsequence() {
        synchronized (this) { // ensuring Thread safety
            Set<Person> alliesCopy = new HashSet<>(owner.getRelationshipManager().getAllies());
            for (Person ally : alliesCopy) {
                if (!ally.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.SlaveFacility)) {
                    owner.getRelationshipManager().removeAlly(ally);
                    ally.getRelationshipManager().removeAlly(owner.getPerson());
                    owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Relationship with " + ally + " cooled due to Slave Facility construction."));
                }
            }
        }

        Set<Person> enemiesCopy = new HashSet<>(owner.getRelationshipManager().getEnemies());
        for (Person enemy : enemiesCopy) {
            if (enemy.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.SlaveFacility)) {
                owner.getRelationshipManager().removeEnemy(enemy);
                enemy.getRelationshipManager().removeEnemy(owner.getPerson());
                owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Common interests in Slave Facilities have improved your standing with " + enemy + "."));
            }
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
