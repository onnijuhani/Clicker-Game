package model.buildings;

public class Citadel extends Property {
    public Citadel(String name) {
        super(PropertyConfig.CITADEL, name + " " + "Citadel");
        this.propertyEnum = Properties.Citadel;
    }
}
