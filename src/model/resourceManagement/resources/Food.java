package model.resourceManagement.resources;

public class Food extends BasicResources {
    private static double totalFoodCount = 0;
    public Food() {
    }
    @Override
    public void add(double amount) {
        super.add(amount);
        totalFoodCount += amount;
    }
    @Override
    public void subtract(double amount) {
        super.subtract(amount);
        totalFoodCount -= amount;
    }
    public static double getTotalFoodCount() {
        return totalFoodCount;
    }
}
