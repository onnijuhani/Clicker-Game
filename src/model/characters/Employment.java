package model.characters;

import model.Settings;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.WorkWallet;
import model.stateSystem.MessageTracker;

import java.util.Random;

public class Employment {
    private int food;
    private int alloy;
    private int gold;
    private float bonusFoodRate = 1;
    private float bonusAlloyRate = 1;
    private float bonusGoldRate = 1;

    public void setWorkWallet(WorkWallet workWallet) {
        this.workWallet = workWallet;
    }

    private WorkWallet workWallet;
    private final Random random = Settings.getRandom();

    public Employment(int foodBaseRate, int alloyBaseRate, int goldBaseRate, WorkWallet workWallet) {
        this.food = generateRandomValue(foodBaseRate);
        this.alloy = generateRandomValue(alloyBaseRate);
        this.gold = generateRandomValue(goldBaseRate);
        this.workWallet = workWallet;
    }

    private int generateRandomValue(int baseRate) {
        double factor = 0.25; // 25% range
        int minValue = (int) (baseRate * (1 - factor));
        int maxValue = (int) (baseRate * (1 + factor));
        return minValue + random.nextInt(maxValue - minValue + 1);
    }

    public void generatePayment() {
        int bonusFood = (int) (food * bonusFoodRate);
        int bonusAlloy = (int) (alloy * bonusAlloyRate);
        int bonusGold = (int) (gold * bonusGoldRate);


        TransferPackage transfer = new TransferPackage(bonusFood, bonusAlloy, bonusGold);
        workWallet.addResources(transfer);


        workWallet.getOwner().getEventTracker().addEvent(MessageTracker.Message("Clicker", "Labor generated: " +transfer ));
    }


    public TransferPackage getFullSalary() {
        int bonusFood = (int) (food * bonusFoodRate);
        int bonusAlloy = (int) (alloy * bonusAlloyRate);
        int bonusGold = (int) (gold * bonusGoldRate);

        return new TransferPackage(bonusFood, bonusAlloy, bonusGold);
    }

    public void setBonusFoodRate(float bonusFoodRate) {
        this.bonusFoodRate = bonusFoodRate;
    }

    public void setBonusAlloyRate(float bonusAlloyRate) {
        this.bonusAlloyRate = bonusAlloyRate;
    }

    public void setBonusGoldRate(float bonusGoldRate) {
        this.bonusGoldRate = bonusGoldRate;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public void setAlloy(int alloy) {
        this.alloy = alloy;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public void clearResources() {
        this.workWallet = null;
    }
}
