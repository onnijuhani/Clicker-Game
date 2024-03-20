package model.buildings.utilityBuilding;

import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.util.Duration;
import model.characters.Person;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SlaveFacility extends UtilityBuilding {
    private int slaveAmount;
    private final int[] production = {20, 10 ,2};

    private final int[] finalProduction = {};

    public SlaveFacility(int basePrice, Person owner) {
        super(basePrice, owner);
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




    public void increaseProduction() {
        production[0] *= 2;
        production[1] *= 2;
        production[2] *= 2;
    }


    @Override
    public boolean upgradeLevel() {
        level++;
        if (level <= MAX_LEVEL+5) {
            increaseProduction();
        } else {
            increaseProductionAfterMaxReached();
            increaseDivider *=2;
        }
        return true;
    }

    public void increaseProductionAfterMaxReached() {
        production[0] = production[0]+Math.max(production[0]/increaseDivider, 1);
        production[1] = production[1]+Math.max(production[1]/increaseDivider, 1);
        production[2] = production[2]+Math.max(production[2]/increaseDivider, 1);
    }

    private void payConsequence() {
        synchronized (this) {
            Set<Person> alliesCopy = new HashSet<>(owner.getRelationsManager().getAllies());
            for (Person ally : alliesCopy) {
                if (!ally.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.SlaveFacility)) {
                    owner.getRelationsManager().removeAlly(ally);
                    ally.getRelationsManager().removeAlly(owner);
                    if(owner.isPlayer()) {
                        owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Relationship with " + ally + " cooled due to Slave Facility construction."));
                    }
                }
            }
        }

        Set<Person> enemiesCopy = new HashSet<>(owner.getRelationsManager().getEnemies());
        for (Person enemy : enemiesCopy) {
            if (enemy.getProperty().getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.SlaveFacility)) {
                owner.getRelationsManager().removeEnemy(enemy);
                enemy.getRelationsManager().removeEnemy(owner);
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
        TransferPackage transfer;
        if(level < MAX_LEVEL) {
            transfer = new TransferPackage(production[0], production[1], production[2]);
        }else{
            transfer = new TransferPackage(finalProduction[0], finalProduction[1], finalProduction[2]);
        }
        owner.getWallet().addResources(transfer);
        if(owner.isPlayer()) {
            owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
        }
    }

    public void addSlave() {
        slaveAmount *= 2;
    }








}
