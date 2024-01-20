package model.buildings;

public class Cottage extends Property{
    public Cottage(String name) {
        super(PropertyConfig.COTTAGE, name + " " + "Cottage");
        this.propertyEnum = Properties.Cottage;
    }
}
