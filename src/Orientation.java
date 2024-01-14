class OrientationStyle {

    Orientation name;
    public Orientation getName() {
        return name;
    }

    public OrientationStyle(Orientation name) {
        this.name = name;
    }
}



public enum Orientation {
    Imperial,
    Democratic
}