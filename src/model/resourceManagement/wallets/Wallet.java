package model.resourceManagement.wallets;

import model.resourceManagement.TransferPackage;
import model.resourceManagement.Resource;

public class Wallet {
    private int food;
    private int alloy;
    private int gold;



    public Wallet() {
        this.food = 0;
        this.alloy = 0;
        this.gold = 0;
    }
    public boolean hasEnoughResource(Resource type, int amount) {
        switch (type) {
            case Food:
                return food >= amount;
            case Alloy:
                return alloy >= amount;
            case Gold:
                return gold >= amount;
            default:
                throw new IllegalArgumentException("Unsupported resource type: " + type);
        }
    }
    public int[] getWalletValues() {
        return new int[]{food, alloy, gold};
    }
    public void addResources(TransferPackage transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("TransferPackage cannot be null.");
        }
        this.food += transfer.food();
        this.alloy += transfer.alloy();
        this.gold += transfer.gold();
    }

    public void subtractResources(TransferPackage transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("TransferPackage cannot be null.");
        }
        this.food -= transfer.food();
        this.alloy-= transfer.alloy();
        this.gold -= transfer.gold();
    }

    public void deposit(Wallet depositFromWallet, TransferPackage transfer){
        this.addResources(transfer);
        depositFromWallet.subtractResources(transfer);
    }
    public void depositAll(Wallet depositFromWallet){
        int[] all = depositFromWallet.getWalletValues();
        TransferPackage transfer = TransferPackage.fromArray(all);
        this.addResources(transfer);
        depositFromWallet.subtractResources(transfer);
    }

    public void withdrawal(Wallet withdrawalToWallet, TransferPackage transfer){
        this.subtractResources(transfer);
        withdrawalToWallet.addResources(transfer);
    }
    @Override
    public String toString() {
        return "Food: "+food + " Alloys: "+alloy + " Gold: "+gold;
    }

    public String toStringValuesRows() {
        return "Food: " + food +
                "\nAlloys: " + alloy +
                "\nGold: " + gold;
    }




    public void subtractFood(int amount) {
        food =- amount;
    }
    public void subtractAlloy(int amount) {
        alloy -= amount;
    }
    public void subtractGold(int amount) {
        gold -= amount;
    }

    public int getFood() {
        return food;
    }
    public void setFood(int food) {
        this.food = food;
    }
    public int getAlloy() {
        return alloy;
    }
    public void setAlloy(int alloy) {
        this.alloy = alloy;
    }
    public int getGold() {
        return gold;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }

}






