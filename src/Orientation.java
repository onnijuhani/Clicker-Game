class Style {

    Orientation name;
    public Orientation getName() {
        return name;
    }

    public Style(Orientation name) {
        this.name = name;
    }
}



public enum Orientation {
    Imperial,
    Democratic
}