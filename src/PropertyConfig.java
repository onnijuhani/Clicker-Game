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

    public static Vault createFortressVault() {
        return new Vault(FORTRESS.food, FORTRESS.alloy, FORTRESS.gold);
    }

    public static Vault createCitadelVault() {
        return new Vault(CITADEL.food, CITADEL.alloy, CITADEL.gold);
    }

    public static Vault createCastleVault() {
        return new Vault(CASTLE.food, CASTLE.alloy, CASTLE.gold);
    }

    public static Vault createManorVault() {
        return new Vault(MANOR.food, MANOR.alloy, MANOR.gold);
    }

    public static Vault createMansionVault() {
        return new Vault(MANSION.food, MANSION.alloy, MANSION.gold);
    }

    public static Vault createVillaVault() {
        return new Vault(VILLA.food, VILLA.alloy, VILLA.gold);
    }

    public static Vault createCottageVault() {
        return new Vault(COTTAGE.food, COTTAGE.alloy, COTTAGE.gold);
    }

    public static Vault createShackVault() {
        return new Vault(SHACK.food, SHACK.alloy, SHACK.gold);
    }

    public static class PropertyValues {
        int food;
        int alloy;
        int gold;
        int strength;

        public PropertyValues(int food, int alloy, int gold, int strength) {
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
        if (week == 4){
            maintenanceCost();
        }
    }
    @Override
    public String toString() {
        return name;
    }

    public void subscribeToTimeEvents() {
        TimeEventManager.subscribe(this);
    }

    protected int food;
    protected int alloy;
    protected int gold;
    protected int strength;
    protected Vault vault;
    protected Quarter location;
    public Character owner;


    String name;
    public String getName() {
        return name;
    }

    public Property(PropertyConfig.PropertyValues propertyValues, Vault vault, String name) {
        this.food = propertyValues.food;
        this.alloy = propertyValues.alloy;
        this.gold = propertyValues.gold;
        this.strength = propertyValues.strength;
        this.vault = vault;
        this.name = name;
        subscribeToTimeEvents();
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

    public void maintenanceCost(){
        if (vault.getFood() < food) {
            owner.getWallet().subtractFood(food);
        } else {
            vault.subtractFood(food);
        }

        if (vault.getAlloy() < alloy) {
            owner.getWallet().subtractAlloy(alloy);
        } else {
            vault.subtractAlloy(alloy);
        }

        if (vault.getGold() < gold) {
            owner.getWallet().subtractGold(gold);
        } else {
            vault.subtractGold(gold);
        }

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

    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getAlloy() {
        return alloy;
    }

    public void setAlloy(int alloy) {
        this.alloy = alloy;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
}

class Fortress extends Property {
    String propertyName = "Fortress";
    public Fortress(String name) {
        super(PropertyConfig.FORTRESS, PropertyConfig.createFortressVault(), name + " " + "Fortress");
    }
}

class Citadel extends Property {
    String propertyName = "Citadel";
    public Citadel(String name) {
        super(PropertyConfig.CITADEL, PropertyConfig.createCitadelVault(), name + " " + "Citadel");
    }
}

class Castle extends Property {
    String propertyName = "Castle";
    public Castle(String name) {
        super(PropertyConfig.CASTLE, PropertyConfig.createCastleVault(), name + " " + "Castle");
    }
}

class Manor extends Property {
    String propertyName = "Manor";
    public Manor(String name) {
        super(PropertyConfig.MANOR, PropertyConfig.createManorVault(), name + " " + "Manor");
    }
}

class Mansion extends Property {
    String propertyName = "Mansion";
    public Mansion(String name) {
        super(PropertyConfig.MANSION, PropertyConfig.createMansionVault(), name + " " + "Mansion");
    }
}

class Villa extends Property {
    String propertyName = "Ville";
    public Villa(String name) {
        super(PropertyConfig.VILLA, PropertyConfig.createVillaVault(), name + " " + "Villa");
    }
}

class Cottage extends Property {
    String propertyName = "Cottage";
    public Cottage(String name) {
        super(PropertyConfig.COTTAGE, PropertyConfig.createCottageVault(), name + " " + "Cottage");
    }
}

class Shack extends Property {
    String propertyName = "Shack";
    public Shack(String name) {
        super(PropertyConfig.SHACK, PropertyConfig.createShackVault(), name + " " + "Shack");
    }
}





