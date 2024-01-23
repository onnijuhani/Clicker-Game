package model.resourceManagement;

import model.resourceManagement.resources.Resource;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransferPackage(double food, double alloy, double gold) {

    public TransferPackage(double food, double alloy, double gold) {
        this.food = round(food);
        this.alloy = round(alloy);
        this.gold = round(gold);
    }

    private static double round(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
    public double[] getAll() {
        return new double[]{food, alloy, gold};
    }
    public static TransferPackage fromArray(double[] values) {
        if (values.length != 3) {
            throw new IllegalArgumentException("Array must have exactly 3 elements.");
        }
        return new TransferPackage(values[0], values[1], values[2]);
    }
    public static TransferPackage fromAnotherPackage(TransferPackage other) {
        return new TransferPackage(other.food, other.alloy, other.gold);
    }
    public static TransferPackage fromEnum(Resource type, double amount) {
        switch (type) {
            case Food:
                return new TransferPackage(amount, 0, 0);
            case Alloy:
                return new TransferPackage(0, amount, 0);
            case Gold:
                return new TransferPackage(0, 0, amount);
            default:
                throw new IllegalArgumentException("Unsupported resource type: " + type);
        }
    }
    @Override
    public String toString() {
        return String.format("[food=%.2f, alloy=%.2f, gold=%.2f]", food, alloy, gold);
    }

    public double food() {
        return food;
    }
    public double alloy() {
        return alloy;
    }
    public double gold() {
        return gold;
    }
}
