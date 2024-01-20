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
    public boolean hasEnoughResource(Resource type, double amount) {
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
    public double[] getWalletValues() {
        return new double[]{food.getAmount(), alloy.getAmount(), gold.getAmount()};
    }
    public void addResources(TransferPackage transfer) {
        double[] amounts = transfer.getAll();
        this.food.add(amounts[0]);
        this.alloy.add(amounts[1]);
        this.gold.add(amounts[2]);
    }
    public void subtractResources(TransferPackage transfer) {
        double[] amounts = transfer.getAll();
        this.food.subtract(amounts[0]);
        this.alloy.subtract(amounts[1]);
        this.gold.subtract(amounts[2]);
    }

    public void deposit(Wallet depositFromWallet, TransferPackage transfer){
        this.addResources(transfer);
        depositFromWallet.subtractResources(transfer);
    }
    public void depositAll(Wallet depositFromWallet){
        double[] all = depositFromWallet.getWalletValues();
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
    public void addFood(double amount) {
        food.add(amount);
    }
    public void addAlloy(double amount) {
        alloy.add(amount);
    }
    public void addGold(double amount) {
        gold.add(amount);
    }
    public void subtractFood(double amount) {
        food.subtract(amount);
    }
    public void subtractAlloy(double amount) {
        alloy.subtract(amount);
    }
    public void subtractGold(double amount) {
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






