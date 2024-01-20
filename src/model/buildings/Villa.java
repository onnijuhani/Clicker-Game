package model.buildings;

public class Villa extends Property{
    public Villa(String name) {
        super(PropertyConfig.VILLA, name + " " + "Villa");
        this.propertyEnum = Properties.Villa;
    }
}
