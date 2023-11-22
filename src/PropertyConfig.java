public class PropertyConfig {
    public static final PropertyValues FORTRESS = new PropertyValues(40, 30, 20, 100);
    public static final PropertyValues CITADEL = new PropertyValues(30, 30, 10, 90);
    public static final PropertyValues CASTLE = new PropertyValues(20, 25, 5, 70);
    public static final PropertyValues MANOR = new PropertyValues(15, 10, 5, 60);
    public static final PropertyValues MANSION = new PropertyValues(10, 4, 4, 40);
    public static final PropertyValues VILLA = new PropertyValues(5, 0, 3, 30);
    public static final PropertyValues COTTAGE = new PropertyValues(3, 0, 2, 20);
    public static final PropertyValues SHACK = new PropertyValues(2, 0, 1, 0);

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

class Property {

    int food;
    int alloy;
    int gold;
    int strength;
    Vault vault;

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
    }

    public Vault getVault() {
        return vault;
    }

    public void maintenanceCost(){
        vault.subtractFood(food);
        vault.subtractAlloy(alloy);
        vault.subtractGold(gold);
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





