import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class PropertyConfig {
    public static final PropertyValues FORTRESS = new PropertyValues(400, 300, 200, 100);
    public static final PropertyValues CITADEL = new PropertyValues(300, 300, 100, 90);
    public static final PropertyValues CASTLE = new PropertyValues(200, 250, 50, 70);
    public static final PropertyValues MANOR = new PropertyValues(150, 100, 50, 60);
    public static final PropertyValues MANSION = new PropertyValues(100, 40, 40, 40);
    public static final PropertyValues VILLA = new PropertyValues(50, 0, 30, 30);
    public static final PropertyValues COTTAGE = new PropertyValues(30, 0, 20, 20);
    public static final PropertyValues SHACK = new PropertyValues(20, 0, 10, 0);
    public static class PropertyValues {
        double food;
        double alloy;
        double gold;
        double strength;

        public PropertyValues(double food, double alloy, double gold, double strength) {
            this.food = food;
            this.alloy = alloy;
            this.gold = gold;
            this.strength = strength;
        }
    }
}

class PropertyTracker {
    private List<Property> properties;
    public PropertyTracker() {
        this.properties = new ArrayList<>();
    }
    public void addProperty(Property property) {
        properties.add(property);
    }
    public List<Property> getProperties() {
        return properties;
    }
}


class Property implements TimeObserver {

    @Override
    public void timeUpdate(int day, int week, int month, int year) {
        if (week == 4 && month >= 2){
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
    public Image getImage(){
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

class Fortress extends Property {

    public Fortress(String name) {
        super(PropertyConfig.FORTRESS, name + " " + "Fortress");
        this.propertyEnum = Properties.Fortress;
    }
}

class Citadel extends Property {
    public Citadel(String name) {
        super(PropertyConfig.CITADEL, name + " " + "Citadel");
        this.propertyEnum = Properties.Citadel;
    }
}

class Castle extends Property {
    public Castle(String name) {
        super(PropertyConfig.CASTLE, name + " " + "Castle");
        this.propertyEnum = Properties.Castle;
    }
}

class Manor extends Property {
    public Manor(String name) {
        super(PropertyConfig.MANOR, name + " " + "Manor");
        this.propertyEnum = Properties.Manor;
    }
}

class Mansion extends Property {
    public Mansion(String name) {
        super(PropertyConfig.MANSION, name + " " + "Mansion");
        this.propertyEnum = Properties.Mansion;
    }
}

class Villa extends Property {
    public Villa(String name) {
        super(PropertyConfig.VILLA, name + " " + "Villa");
        this.propertyEnum = Properties.Villa;
    }
}

class Cottage extends Property {
    public Cottage(String name) {
        super(PropertyConfig.COTTAGE, name + " " + "Cottage");
        this.propertyEnum = Properties.Cottage;
    }
}

class Shack extends Property {
    public Shack(String name) {
        super(PropertyConfig.SHACK, name + " " + "Shack");
        this.propertyEnum = Properties.Shack;
    }
}

class Maintenance {
    private double food;
    private double alloy;
    private double gold;

    public Maintenance(PropertyConfig.PropertyValues propertyValues){
        this.food = propertyValues.food;
        this.alloy = propertyValues.alloy;
        this.gold = propertyValues.gold;
    }

    public void payMaintenance(Property property) {
        TransferPackage maintenanceCost = maintenanceCost();
        Vault propertyVault = property.getVault();
        Wallet ownerWallet = property.getOwner().getWallet();

        if (canPay(maintenanceCost, propertyVault)) {
            propertyVault.subtractResources(maintenanceCost);
        } else if (canPay(maintenanceCost, ownerWallet)) {
            ownerWallet.subtractResources(maintenanceCost);
        } else {
            System.out.println("Insufficient resources for maintenance. " + ownerWallet + " "+ property.getOwner());
        }
    }

    private boolean canPay(TransferPackage cost, Wallet wallet) {
        double[] walletResources = wallet.getWalletValues();
        double[] costResources = cost.getAll();
        return walletResources[0] >= costResources[0] &&
                walletResources[1] >= costResources[1] &&
                walletResources[2] >= costResources[2];
    }
    public TransferPackage maintenanceCost(){
        return new TransferPackage(food, alloy, gold);
    }
    @Override
    public String toString(){
        return "Maintenance cost: " + food+" - "+alloy+" - "+gold;
    }
}


class Buildings {


    public Buildings() {
    }

    class FarmField{
        public FarmField(){
        }
    }
    class AlloyMine{
        public AlloyMine(){
        }
    }
    class GoldMine {
        public GoldMine() {
     }
    }
    class SlaveFacility{
        public SlaveFacility() {
        }
    }
}
enum Properties{
    Fortress,
    Citadel,
    Castle,
    Manor,
    Mansion,
    Villa,
    Cottage,
    Shack,
}



