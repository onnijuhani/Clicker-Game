package model.resourceManagement.resources;

public class Food extends BasicResources {
    private static int totalFoodCount = 0;

    public Food() {
    }

    @Override
    public void add(double amount) {
        this.amount += amount;
        totalFoodCount = totalFoodCount + (int) amount;
    }

    @Override
    public void subtract(double amount) {
        this.amount -= amount;
        totalFoodCount = totalFoodCount - (int) amount;
    }

    public static double getTotalFoodCount() {
        return totalFoodCount;
    }


}

