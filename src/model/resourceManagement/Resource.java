package model.resourceManagement;

public enum Resource {
    Food,
    Alloy,
    Gold;

    public String toShortString() {
        return switch (this) {
            case Food -> "F";
            case Alloy -> "A";
            case Gold -> "G";
            default -> throw new IllegalArgumentException("Unknown resource");
        };
    }
}

