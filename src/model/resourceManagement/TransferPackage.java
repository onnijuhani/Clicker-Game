package model.resourceManagement;

import model.resourceManagement.resources.Resource;

public record TransferPackage(double food, double alloy, double gold) {

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
