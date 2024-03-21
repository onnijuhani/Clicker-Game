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

    protected boolean isGuildMember;
    protected final int[] production = {60, 30 ,6};

    public SlaveFacility(int basePrice, Person owner) {
        super(basePrice, owner);
        this.value = 1;
        this.name = UtilityBuildings.SlaveFacility;
        delayConsequence();
    }

    public void setAsGuildMember(boolean isGuildMember) {
        this.isGuildMember = isGuildMember;
        if (isGuildMember) {
            addBonus("Guild bonus", 0.2);
        } else {
            removeBonus("Guild bonus");
        }
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

    public int getUpgradePrice() {
        if (level < MAX_LEVEL) {
            return calculateUpgradePrice();
        } else {
            return Math.min(calculateUpgradePrice(), PRICE_CEILING);
        }
    }

    private int calculateUpgradePrice() {
        return Math.min((int) (basePrice * Math.pow(3, level - 1)), PRICE_CEILING);
    }

    public boolean increaseLevel() {
        level++;
        if (level <= MAX_LEVEL) {
            value *= 2;
            increaseProduction();
        } else {
            value += Math.max(1, value / (increaseDivider * (level - MAX_LEVEL)));
            increaseProductionAfterMaxReached();
        }
        return true;
    }
    public void increaseProductionAfterMaxReached() {
        production[0] += Math.max(1, production[0] / (increaseDivider * (level - MAX_LEVEL)));
        production[1] += Math.max(1, production[1] / (increaseDivider * (level - MAX_LEVEL)));
        production[2] += Math.max(1, production[2] / (increaseDivider * (level - MAX_LEVEL)));
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
                if(owner.isPlayer()) {
                    owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Common interests in Slave Facilities have improved your standing with " + enemy + "."));
                }
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
        transfer = new TransferPackage(production[0]*calculateBonus(), production[1]*calculateBonus(), production[2]*calculateBonus());
        owner.getWallet().addResources(transfer);
        if(owner.isPlayer()) {
            owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
        }
    }


}
