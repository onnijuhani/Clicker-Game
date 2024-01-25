package model.resourceManagement;

public record TransferPackage(int food, int alloy, int gold) {

    public TransferPackage(int food, int alloy, int gold) {
        this.food = food;
        this.alloy = alloy;
        this.gold =  gold;
    }

    public int[] getAll() {
        return new int[]{food, alloy, gold};
    }
    public static TransferPackage fromArray(int[] values) {
        if (values.length != 3) {
            throw new IllegalArgumentException("Array must have exactly 3 elements.");
        }
        return new TransferPackage(values[0], values[1], values[2]);
    }
    public static TransferPackage fromAnotherPackage(TransferPackage other) {
        return new TransferPackage(other.food, other.alloy, other.gold);
    }



    public static TransferPackage fromEnum(Resource type, int amount) {
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
        return " (food: "+food+" alloy: "+alloy+" gold: "+gold+")";
    }

    public int food() {
        return food;
    }
    public int alloy() {
        return alloy;
    }
    public int gold() {
        return gold;
    }
}
