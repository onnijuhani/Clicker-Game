package model.resourceManagement;

public record TransferPackage(int food, int alloy, int gold) {
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
        return switch (type) {
            case Food -> new TransferPackage(amount, 0, 0);
            case Alloy -> new TransferPackage(0, amount, 0);
            case Gold -> new TransferPackage(0, 0, amount);
            default -> throw new IllegalArgumentException("Unsupported resource type: " + type);
        };
    }
    @Override
    public String toString() {
        return "Food: "+food+" Alloys: "+alloy+" Gold: "+gold;
    }
    public String toShortString() {
        return "F:"+food+" A:"+alloy+" G:"+gold;
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

    public TransferPackage add(TransferPackage other) {
        return new TransferPackage(this.food + other.food, this.alloy + other.alloy, this.gold + other.gold);
    }

    public TransferPackage multiply(double factor) {
        return new TransferPackage((int) (food * factor), (int) (alloy * factor), (int) (gold * factor));
    }

    public TransferPackage subtract(TransferPackage other) {
        return new TransferPackage(this.food - other.food, this.alloy - other.alloy, this.gold - other.gold);
    }

    public TransferPackage divide(double divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Divisor cannot be zero.");
        }
        return new TransferPackage((int) (food / divisor), (int) (alloy / divisor), (int) (gold / divisor));
    }


}
