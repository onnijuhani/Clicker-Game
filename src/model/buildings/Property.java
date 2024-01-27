package model.buildings;


import model.buildings.utilityBuilding.UtilitySlot;
import model.time.PropertyManager;
import javafx.scene.image.Image;
import model.Images;
import model.time.PropertyObserver;
import model.characters.Character;
import model.resourceManagement.wallets.Vault;
import model.worldCreation.Details;
import model.worldCreation.Quarter;
public class Property implements PropertyObserver, Details {

    @Override
    public void propertyUpdate() {
        if (firstTimeReached) {
                // Skip the first model.time this condition is met
                firstTimeReached = false;
        } else {
                maintenance.payMaintenance(this);
        }
    }
    @Override
    public String getDetails(){
        return (this.getClass().getSimpleName() + " " + getName());
    }
    @Override
    public String toString() {
        return name;
    }

    private boolean firstTimeReached = true;
    protected double strength;
    protected Vault vault;
    protected Quarter location;
    protected Character owner;
    protected Maintenance maintenance;
    protected String name;
    protected Buildings buildings;
    protected Properties propertyEnum;



    protected UtilitySlot utilitySlot;


    public Property(PropertyConfig.PropertyValues propertyValues, String name) {
        this.strength = propertyValues.power;
        this.vault = new Vault();
        this.name = name;
        this.maintenance = new Maintenance(propertyValues);
        PropertyManager.subscribe(this);
        this.utilitySlot = new UtilitySlot(5);
    }

    public Image getImage() {
        return Images.PropertyImg.getImage(propertyEnum);
    }



    public Character getOwner() {
        return owner;
    }

    public void setOwner(Character owner) {
        this.owner = owner;
        owner.setProperty(this);
    }

    public Vault getVault() {
        return vault;
    }

    public Quarter getLocation() {
        return location;
    }

    public void setLocation(Quarter location) {
        this.location = location;
        location.addProperty(this);
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
    public UtilitySlot getUtilitySlot() {
        return utilitySlot;
    }

    public void setUtilitySlot(UtilitySlot utilitySlot) {
        this.utilitySlot = utilitySlot;
    }
}
