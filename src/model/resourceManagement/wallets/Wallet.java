package model.resourceManagement.wallets;

import model.characters.Person;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.shop.Ownable;
import model.stateSystem.MessageTracker;

import static model.Settings.formatNumber;

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

    public boolean isEmpty(){
        return food == 0 && alloy == 0 && gold == 0;
    }

    public boolean hasEmptySlot(){
        return food <= 0 || alloy <= 0 || gold <= 0;
    }

    public boolean isLowBalance(){
        return food <= 100 && alloy <= 50 && gold <= 10;
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
        if(!transfer.isPositive()) throw new IllegalArgumentException("Negative value in transferPackage");
        if (limitReached()) return;
        this.food += transfer.food();
        this.alloy += transfer.alloy();
        this.gold += transfer.gold();
    }

    private boolean limitReached() {
        return food > 1_000_000_000 || alloy > 1_000_000_000 || gold > 1_000_000_000;
    }

    public boolean subtractResources(TransferPackage transfer) {
        if (transfer == null) {
            throw new IllegalArgumentException("TransferPackage cannot be null.");
        }
        if(!hasEnoughResources(transfer)){
            return false; // quick return if there isn't enough resources
        }
        this.food -= transfer.food();
        this.alloy-= transfer.alloy();
        this.gold -= transfer.gold();

        generateRescuePackage();
        return true;
    }

    public boolean deposit(Wallet depositFromWallet, TransferPackage transfer){
        if(!transfer.isPositive()) return false;
        if (limitReached()) return false;
        if(!depositFromWallet.hasEnoughResources(transfer)){
            return false; // quick return if there isn't enough resources, should never happen tho.
        }
        this.addResources(transfer);
        depositFromWallet.subtractResources(transfer);
        depositFromWallet.generateRescuePackage();
        return true;
    }
    public boolean depositAll(Wallet depositFromWallet){
        if (limitReached()) return false;



        int[] all = depositFromWallet.getWalletValues();
        TransferPackage transfer = TransferPackage.fromArray(all);
        this.addResources(transfer);
        depositFromWallet.subtractResources(transfer);
        depositFromWallet.generateRescuePackage();
        return true;
    }


    /**
     * Withdrawal is a faster way to send resources from this wallet to another wallet using transferPackage
     * @param withdrawalToWallet wallet that will receive the resources
     * @param transfer transferPackage that contains the amounts
     * @return returns true if the transaction happens and false if not. There must be enough resources in the wallet.
     */
    public boolean withdrawal(Wallet withdrawalToWallet, TransferPackage transfer){
        if(!hasEnoughResources(transfer)){
            return false; // quick return if there isn't enough resources
        }
        this.subtractResources(transfer);
        withdrawalToWallet.addResources(transfer);
        generateRescuePackage();
        return true;
    }
    @Override
    public String toString() {
        return "Food: "+formatNumber(food) + " Alloys: "+formatNumber(alloy) + " Gold: "+formatNumber(gold);
    }

    public String toShortString() {
        return "F:"+formatNumber(food) + " A:"+formatNumber(alloy) + " G:"+formatNumber(gold);
    }

    public String toStringValuesRows() {
        return "Food: " + formatNumber(food) +
                "\nAlloys: " + formatNumber(alloy) +
                "\nGold: " + formatNumber(gold);
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
        return new double[]{(food / total), (alloy / total), (gold / total)};
    }

    public int getCombinedAmount() {
        return food + alloy + gold;
    }

    public String getRatioString() {
        double[] ratios = getBalanceRatio();
        return String.format("Food: %.0f%% Alloys: %.0f%% Gold: %.0f%%", ratios[0]*100, ratios[1]*100, ratios[2]*100);
    }

    public TransferPackage getBalance(){
        return new TransferPackage(food, alloy, gold);
    }




    public void subtractFood(int amount) {
        food = Math.max(0, food - amount);
    }

    public void subtractAlloy(int amount) {
        alloy = Math.max(0, alloy - amount);
    }

    public void subtractGold(int amount) {
        gold = Math.max(0, gold - amount);
    }
    public int getFood() {
        return food;
    }
    public void setFood(int food) {
        this.food = Math.max(food, 0);
    }
    public int getAlloy() {
        return alloy;
    }
    public void setAlloy(int alloy) {
        this.alloy = Math.max(alloy, 0);
    }
    public int getGold() {
        return gold;
    }
    public void setGold(int gold) {
        this.gold = Math.max(gold, 0);
    }


    /**
     * In case wallet is completely emptied, this method attempts to make a withdrawal from vault to prevent loss of strikes.
     */
    private void generateRescuePackage(){
        if(!this.isEmpty()){
            return;
        }
        if(getOwner() instanceof Person person){
            TransferPackage expenses = person.getPaymentManager().getFullExpense();
            Vault vault = person.getProperty().getVault();

            if(vault.isEmpty()){
                return;
            }

            if(this.deposit(vault, expenses)){
                person.getEventTracker().addEvent(MessageTracker.Message("Minor", "Deposited needed resources from Vault"));
            }else{
                this.depositAll(vault);
            }
        }
    }

}






