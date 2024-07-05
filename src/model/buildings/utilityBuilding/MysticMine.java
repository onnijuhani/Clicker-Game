package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.Person;
import model.resourceManagement.TransferPackage;
import model.stateSystem.MessageTracker;
import model.time.Time;

import java.util.Random;

public class MysticMine extends UtilityBuilding {

    private int alloyProduction;
    private int goldProduction;
    private final Random random = Settings.getRandom();

    public MysticMine(int basePrice, Person owner) {
        super(basePrice, owner);
        this.alloyProduction = Settings.getInt("mineProduction")*2;
        this.goldProduction = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.MysticMine;
    }

    @Override
    protected void generateAction() {
        TransferPackage transfer = getGenerateAmount();
        owner.getWallet().addResources(transfer);
        if (owner.isPlayer()) {
            owner.getEventTracker().addEvent(MessageTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
        }
    }


    public boolean increaseLevel() {
        level++;
        if (level <= MAX_LEVEL) {
            alloyProduction *= 2;
            goldProduction *= 2;
        } else {
            alloyProduction += Math.max(50, alloyProduction / (increaseDivider * (level - MAX_LEVEL)));
            goldProduction += Math.max(50, goldProduction / (increaseDivider * (level - MAX_LEVEL)));
        }
        return true;
    }

    public void upgradeProduction() {
        this.alloyProduction = alloyProduction * 2;
        this.goldProduction = goldProduction * 2;
    }


    private int calculateNormalDistValue(int mean) {
        double result = random.nextGaussian() * ( (double) mean ) + (double) mean / 2;

        if (result < 0) {
            result = 0;
        }

        return (int) Math.round(result);
    }

    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() +"\n"+
                        "Alloys and Gold"
        );
    }

    private TransferPackage getGenerateAmount() {
        return new TransferPackage(0,calculateNormalDistValue(alloyProduction), calculateNormalDistValue(goldProduction));
    }

    private TransferPackage getExpectedAmount() {
        return new TransferPackage(0,alloyProduction, goldProduction);
    }



    @Override
    public void updatePaymentManager(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.INCOME, Payment.MYSTIC_MINE_INCOME, getExpectedAmount(), Time.utilitySlots);
    }


}
