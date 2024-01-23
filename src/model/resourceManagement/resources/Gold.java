package model.resourceManagement.resources;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Gold extends BasicResources {
    private static double totalGoldCount = 0;

    public Gold() {
    }

    @Override
    public void add(double amount) {
        amount = round(amount);
        super.add(amount);
        totalGoldCount = round(totalGoldCount + amount);
    }

    @Override
    public void subtract(double amount) {
        amount = round(amount);
        super.subtract(amount);
        totalGoldCount = round(totalGoldCount - amount);
    }

    public static double getTotalGoldCount() {
        return totalGoldCount;
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}

