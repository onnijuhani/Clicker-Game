package model.buildings;

public class Fortress extends Property{

    public Fortress(String name) {
        super(PropertyConfig.FORTRESS, name);
        this.propertyEnum = Properties.Fortress;
    }
}
