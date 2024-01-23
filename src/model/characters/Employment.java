package model.characters;

import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.WorkWallet;

public class Employment {
    private double food;
    private double alloy;
    private double gold;
    private double bonusFood = 1;
    private double bonusAlloy = 1;
    private double bonusGold = 1;
    private WorkWallet workWallet;

    public Employment(double foodBaseRate, double alloyBaseRate, double goldBaseRate, WorkWallet workWallet) {
        this.food = foodBaseRate;
        this.alloy = alloyBaseRate;
        this.gold = goldBaseRate;
        this.workWallet = workWallet;
    }

    public void generatePayment() {
        TransferPackage transfer = new TransferPackage(food * bonusFood, alloy * bonusAlloy, gold * bonusGold);
        workWallet.addResources(transfer);
    }

    public void setBonusFood(double bonusFood) {
        this.bonusFood = bonusFood;
    }

    public void setBonusAlloy(double bonusAlloy) {
        this.bonusAlloy = bonusAlloy;
    }

    public void setBonusGold(double bonusGold) {
        this.bonusGold = bonusGold;
    }

    public void setFood(double food) {
        this.food = food;
    }

    public void setAlloy(double alloy) {
        this.alloy = alloy;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }
}
