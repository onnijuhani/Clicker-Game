package model.resourceManagement.resources;


public class Alloy extends BasicResources {
    private static int totalAlloyCount = 0;

    public Alloy() {
    }

    @Override
    public void add(double amount) {
        this.amount += amount;
        totalAlloyCount = totalAlloyCount + (int)amount;
    }

    @Override
    public void subtract(double amount) {
        this.amount -= amount;
        totalAlloyCount = totalAlloyCount - (int) amount;
    }

    public static double getTotalAlloyCount() {
        return totalAlloyCount;
    }

}

