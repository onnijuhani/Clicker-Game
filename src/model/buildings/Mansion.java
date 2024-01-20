package model.buildings;

public class Mansion extends Property{
    public Mansion(String name) {
        super(PropertyConfig.MANSION, name + " " + "Mansion");
        this.propertyEnum = Properties.Mansion;
    }
}
