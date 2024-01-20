package model.resourceManagement.payments;

public class Salary{

    private double food;
    private double alloy;
    private double gold;

    public Salary(double food, double alloy, double gold){
        this.food = food;
        this.alloy = alloy;
        this.gold = gold;
    }
    public double[] getAll(){
        return new double[]{food, alloy, gold};
    }

    public void changePay(double foodRate, double alloyRate, double goldRate){
        this.food = this.food * (1 + foodRate);
        this.alloy = this.alloy * (1 + alloyRate);
        this.gold = this.gold * (1 + goldRate);
    }
    public double getFood() {
        return food;
    }
    public void setFood(double food) {
        this.food = food;
    }
    public double getAlloy() {
        return alloy;
    }
    public void setAlloy(double alloy) {
        this.alloy = alloy;
    }
    public double getGold() {
        return gold;
    }
    public void setGold(double gold) {
        this.gold = gold;
    }
}
