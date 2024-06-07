package model.resourceManagement.payments;

import model.resourceManagement.TransferPackage;

public class Salary{

    private int food;
    private int alloy;
    private int gold;

    public Salary(int food, int alloy, int gold){
        this.food = food;
        this.alloy = alloy;
        this.gold = gold;
    }

    public void updateSalary(TransferPackage transferPackage){
        food = transferPackage.food();
        alloy = transferPackage.alloy();
        gold = transferPackage.gold();
    }


    public int[] getAll(){
        return new int[]{food, alloy, gold};
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
