package model.resourceManagement.wallets;

import model.resourceManagement.resources.Alloy;
import model.resourceManagement.resources.Food;
import model.resourceManagement.resources.Gold;
import model.resourceManagement.resources.Resource;
import model.resourceManagement.TransferPackage;

public class Wallet {
    private Food food;
    private Alloy alloy;
    private Gold gold;
    public Wallet() {
        this.food = new Food();
        this.alloy = new Alloy();
        this.gold = new Gold();
    }
    public boolean hasEnoughResource(Resource type, int amount) {
        switch (type) {
            case Food:
                return food.getAmount() >= amount;
            case Alloy:
                return alloy.getAmount() >= amount;
            case Gold:
                return gold.getAmount() >= amount;
            default:
                throw new IllegalArgumentException("Unsupported resource type: " + type);
        }
    }
    public int[] getWalletValues() {
        return new int[]{food.getAmount(), alloy.getAmount(), gold.getAmount()};
    }
    public void addResources(TransferPackage transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("TransferPackage cannot be null.");
        }
        this.food.add(transfer.food());
        this.alloy.add(transfer.alloy());
        this.gold.add(transfer.gold());
    }

    public void subtractResources(TransferPackage transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("TransferPackage cannot be null.");
        }
        this.food.subtract(transfer.food());
        this.alloy.subtract(transfer.alloy());
        this.gold.subtract(transfer.gold());
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
        return "Food: "+food.getAmount() + " Alloys: "+alloy.getAmount() + " Gold: "+gold.getAmount();
    }

    public String toStringValuesRows() {
        return "Food: " + Math.round(food.getAmount()) +
                "\nAlloys: " + Math.round(alloy.getAmount()) +
                "\nGold: " + Math.round(gold.getAmount());
    }



    public void addFood(int amount) {
        food.add(amount);
    }
    public void addAlloy(int amount) {
        alloy.add(amount);
    }
    public void addGold(int amount) {
        gold.add(amount);
    }
    public void subtractFood(int amount) {
        food.subtract(amount);
    }
    public void subtractAlloy(int amount) {
        alloy.subtract(amount);
    }
    public void subtractGold(int amount) {
        gold.subtract(amount);
    }

    public Food getFood() {
        return food;
    }
    public void setFood(Food food) {
        this.food = food;
    }
    public Alloy getAlloy() {
        return alloy;
    }
    public void setAlloy(Alloy alloy) {
        this.alloy = alloy;
    }
    public Gold getGold() {
        return gold;
    }
    public void setGold(Gold gold) {
        this.gold = gold;
    }
}






