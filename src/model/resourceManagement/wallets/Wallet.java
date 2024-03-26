package model.resourceManagement.wallets;

import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.shop.Ownable;

public class Wallet {
    private int food;
    private int alloy;
    private int gold;

    private Ownable owner;

    public Wallet(final Ownable owner) {
        this.owner = owner;
        this.food = 0;
        this.alloy = 0;
        this.gold = 0;
    }
    public boolean hasEnoughResource(Resource type, int amount) {
        return switch (type) {
            case Food -> food >= amount;
            case Alloy -> alloy >= amount;
            case Gold -> gold >= amount;
        };
    }

    public int getResource(Resource type) {
        return switch (type) {
            case Food -> food;
            case Alloy -> alloy;
            case Gold -> gold;
            default -> throw new IllegalArgumentException("Unsupported resource type: " + type);
        };
    }
    public Ownable getOwner() {
        return owner;
    }

    public boolean hasResources(int food, int alloy, int gold){
        TransferPackage test = new TransferPackage(food, alloy, gold);
        return this.hasEnoughResources(test);
    }

    public boolean hasEnoughResources(TransferPackage transfer) {
        int[] amounts = transfer.getAll();
        return food >= amounts[0] && alloy >= amounts[1] && gold >= amounts[2];
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

    public String toShortString() {
        return "F:"+food + " A:"+alloy + " G:"+gold;
    }

    public String toStringValuesRows() {
        return "Food: " + food +
                "\nAlloys: " + alloy +
                "\nGold: " + gold;
    }

    public void setOwner(Ownable owner) {
        this.owner = owner;
    }


    /**
     * restricts the balance to grow high in certain situations where only the percentage matters
     */
    public void cutBalanceInHalf(){
        if(food > 10) {
            food = food / 2;
        }
        if(alloy > 10) {
            alloy = alloy / 2;
        }
        if (gold > 10) {
            gold = gold / 2;
        }
    }

    public double[] getBalanceRatio() {
        double total = food + alloy + gold;
        if (total == 0) return new double[]{0, 0, 0};
        return new double[]{(food / total) * 100, (alloy / total) * 100, (gold / total) * 100};
    }

    public String getRatioString() {
        double[] ratios = getBalanceRatio();
        return String.format("Food: %.0f%% Alloys: %.0f%% Gold: %.0f%%", ratios[0], ratios[1], ratios[2]);
    }




    public void subtractFood(int amount) {
        food -= amount;
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






