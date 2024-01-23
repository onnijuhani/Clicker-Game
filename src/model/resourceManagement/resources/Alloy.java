package model.resourceManagement.resources;


import java.math.BigDecimal;
import java.math.RoundingMode;

public class Alloy extends BasicResources {
    private static double totalAlloyCount = 0;

    public Alloy() {
    }

    @Override
    public void add(double amount) {
        amount = round(amount);
        super.add(amount);
        totalAlloyCount = round(totalAlloyCount + amount);
    }

    @Override
    public void subtract(double amount) {
        amount = round(amount);
        super.subtract(amount);
        totalAlloyCount = round(totalAlloyCount - amount);
    }

    public static double getTotalAlloyCount() {
        return totalAlloyCount;
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}

