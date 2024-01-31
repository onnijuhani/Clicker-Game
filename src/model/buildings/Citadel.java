package model.buildings;

public class Citadel extends Property {
    public Citadel(String name) {
        super(PropertyConfig.CITADEL, name);
        this.propertyEnum = Properties.Citadel;
    }
}
