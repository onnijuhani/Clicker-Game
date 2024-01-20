package model.resourceManagement.resources;

public class Gold extends BasicResources {
    private static double totalGoldCount = 0;
    public Gold() {
    }
    @Override
    public void add(double amount) {
        super.add(amount);
        totalGoldCount += amount;
    }
    @Override
    public void subtract(double amount) {
        super.subtract(amount);
        totalGoldCount -= amount;
    }
    public static double getTotalGoldCount() {
        return totalGoldCount;
    }
}
