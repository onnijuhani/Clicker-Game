package model.resourceManagement.resources;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Food extends BasicResources {
    private static double totalFoodCount = 0;

    public Food() {
    }

    @Override
    public void add(double amount) {
        amount = round(amount);
        super.add(amount);
        totalFoodCount = round(totalFoodCount + amount);
    }

    @Override
    public void subtract(double amount) {
        amount = round(amount);
        super.subtract(amount);
        totalFoodCount = round(totalFoodCount - amount);
    }

    public static double getTotalFoodCount() {
        return totalFoodCount;
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}

