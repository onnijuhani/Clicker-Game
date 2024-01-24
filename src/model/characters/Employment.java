package model.characters;

import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.WorkWallet;

public class Employment {
    private int food;
    private int alloy;
    private int gold;
    private float bonusFoodRate = 1;
    private float bonusAlloyRate = 1;
    private float bonusGoldRate = 1;
    private final WorkWallet workWallet;

    public Employment(int foodBaseRate, int alloyBaseRate, int goldBaseRate, WorkWallet workWallet) {
        this.food = foodBaseRate;
        this.alloy = alloyBaseRate;
        this.gold = goldBaseRate;
        this.workWallet = workWallet;
    }

    public void generatePayment() {
        int bonusFood = (int) (food * bonusFoodRate);
        int bonusAlloy = (int) (alloy * bonusAlloyRate);
        int bonusGold = (int) (gold * bonusGoldRate);

        TransferPackage transfer = new TransferPackage(bonusFood, bonusAlloy, bonusGold);
        workWallet.addResources(transfer);
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
}
