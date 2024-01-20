package model.buildings;

public class Shack extends Property{
    public Shack(String name) {
        super(PropertyConfig.SHACK, name + " " + "Shack");
        this.propertyEnum = Properties.Shack;
    }
}
