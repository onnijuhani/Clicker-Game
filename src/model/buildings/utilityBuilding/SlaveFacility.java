package model.buildings.utilityBuilding;

import javafx.animation.KeyFrame;
import javafx.application.Platform;
import javafx.util.Duration;
import model.characters.payments.Payment;
import model.characters.payments.PaymentCalendar;
import model.characters.Person;
import model.characters.Trait;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;
import model.time.Time;

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
        production[0] += Math.max(50, production[0] / (increaseDivider * (level - MAX_LEVEL)));
        production[1] += Math.max(50, production[1] / (increaseDivider * (level - MAX_LEVEL)));
        production[2] += Math.max(50, production[2] / (increaseDivider * (level - MAX_LEVEL)));
    }

    protected void payConsequence() {
        synchronized (this) {
            Set<Person> alliesCopy = new HashSet<>(owner.getRelationsManager().getAllies());
            for (Person ally : alliesCopy) {

                if (!ally.getAiEngine().getProfile().containsKey(Trait.Slaver)) { // slavers don't mind
                    owner.getRelationsManager().removeAlly(ally);
                    ally.getRelationsManager().removeAlly(owner);

                    if(ally.getAiEngine().getProfile().containsKey(Trait.Liberal)){ // liberals become enemies
                        owner.getRelationsManager().addEnemy(ally);
                        ally.getRelationsManager().addEnemy(owner);
                    }

                    if(owner.isPlayer()) {
                        owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Relationship with " + ally.getCharacter() + "\n\t\t\t\tcooled due to Slave Facility construction."));
                    }
                }
            }
        }

        Set<Person> enemiesCopy = new HashSet<>(owner.getRelationsManager().getEnemies());
        for (Person enemy : enemiesCopy) {
            if (enemy.getAiEngine().getProfile().containsKey(Trait.Slaver)) { // Enemies that are slavers are no longer enemies
                owner.getRelationsManager().removeEnemy(enemy);
                enemy.getRelationsManager().removeEnemy(owner);
                if(owner.isPlayer()) {
                    owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Common interests in Slave Facilities\n\t\t\t\thave improved your standing with " + enemy.getCharacter() + "."));
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

    private TransferPackage getGenerateAmount() {
        return new TransferPackage(production[0],production[1],production[2]);
    }

    @Override
    public void updatePaymentCalendar(PaymentCalendar calendar) {
        calendar.addPayment(PaymentCalendar.PaymentType.INCOME, Payment.SLAVE_FACILITY_INCOME, getGenerateAmount(), Time.utilitySlots);
    }


}
