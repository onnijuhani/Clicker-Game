package model.buildings;

import javafx.scene.image.Image;
import model.Images;
import model.worldCreation.Quarter;
import model.TimeEventManager;
import model.TimeObserver;
import model.characters.Character;
import model.resourceManagement.wallets.Vault;

public class Property implements TimeObserver {

    @Override
    public void timeUpdate(int day, int week, int month, int year) {
        if (week == 4 && month >= 2) {
            maintenance.payMaintenance(this);
        }
    }

    protected double strength;
    protected Vault vault;
    protected Quarter location;
    protected Character owner;
    protected Maintenance maintenance;
    protected String name;
    protected Buildings buildings;
    protected Properties propertyEnum;

    public Property(PropertyConfig.PropertyValues propertyValues, String name) {
        this.strength = propertyValues.strength;
        this.vault = new Vault();
        this.name = name;
        this.maintenance = new Maintenance(propertyValues);
        TimeEventManager.subscribe(this);
    }

    public Image getImage() {
        return Images.PropertyImg.getImage(propertyEnum);
    }

    @Override
    public String toString() {
        return name;
    }

    public Character getOwner() {
        return owner;
    }

    public void setOwner(Character owner) {
        this.owner = owner;
    }

    public Vault getVault() {
        return vault;
    }

    public Quarter getLocation() {
        return location;
    }

    public void setLocation(Quarter location) {
        this.location = location;
    }

    public boolean hasLocation() {
        return location != null;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public String getName() {
        return name;
    }

    public Buildings getBuildings() {
        return buildings;
    }

    public void setBuildings(Buildings buildings) {
        this.buildings = buildings;
    }

    public Maintenance getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
    }
}
