package model.buildings;

public class Manor extends Property{
    public Manor(String name) {
        super(PropertyConfig.MANOR, name);
        this.propertyEnum = Properties.Manor;
    }
}
