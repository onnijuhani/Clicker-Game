package model.resourceManagement.resources;

public class BasicResources {
    protected int amount = 0;

    public int getAmount() {
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




