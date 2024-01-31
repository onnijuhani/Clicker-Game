package model.buildings;

public class Castle extends Property {
    public Castle(String name) {
        super(PropertyConfig.CASTLE, name);
        this.propertyEnum = Properties.Castle;
    }
}
