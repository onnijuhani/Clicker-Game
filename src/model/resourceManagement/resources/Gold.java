package model.resourceManagement.resources;

public class Gold extends BasicResources {
    private static int totalGoldCount = 0;

    public Gold() {
    }

    @Override
    public void add(double amount) {
        this.amount += amount;
        totalGoldCount = totalGoldCount + (int) amount;
    }

    @Override
    public void subtract(double amount) {
        this.amount -= amount;
        totalGoldCount = totalGoldCount - (int) amount;
    }

    public static double getTotalGoldCount() {
        return totalGoldCount;
    }


}

