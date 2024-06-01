package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.payments.Payment;
import model.characters.payments.PaymentCalendar;
import model.characters.Person;
import model.resourceManagement.TransferPackage;
import model.time.Time;

import java.util.Random;

public class MysticMine extends UtilityBuilding {

    private int alloyProduction;
    private int goldProduction;
    private final Random random = new Random();

    public MysticMine(int basePrice, Person owner) {
        super(basePrice, owner);
        this.alloyProduction = Settings.getInt("mineProduction")*2;
        this.goldProduction = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.MysticMine;
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
        return new TransferPackage(0,alloyProduction, goldProduction);
    }

    @Override
    public void updatePaymentCalendar(PaymentCalendar calendar) {
        calendar.addPayment(PaymentCalendar.PaymentType.INCOME, Payment.MYSTIC_MINE_INCOME, getGenerateAmount(), Time.utilitySlots);
    }


}
