package model.resourceManagement.resources;

public class BasicResources {
    private double amount = 0;

    public double getAmount() {
        return amount;
    }
    public void add(double amount) {
        this.amount += amount;
    }
    public void subtract(double amount) {
        this.amount -= amount;
    }
    @Override
    public String toString() {
        return "" + amount;
    }

}




