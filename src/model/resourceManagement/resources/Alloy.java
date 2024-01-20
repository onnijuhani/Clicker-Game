package model.resourceManagement.resources;

public class Alloy extends BasicResources {
    private static double totalAlloyCount = 0;

    public Alloy() {
    }

    @Override
    public void add(double amount) {
        super.add(amount);
        totalAlloyCount += amount;
    }

    @Override
    public void subtract(double amount) {
        super.subtract(amount);
        totalAlloyCount -= amount;
    }

    public static double getTotalAlloyCount() {
        return totalAlloyCount;
    }
}

