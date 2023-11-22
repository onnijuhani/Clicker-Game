public class PropertyConfig {
    public static final PropertyValues FORTRESS = new PropertyValues(40, 30, 20, 100);
    public static final PropertyValues CITADEL = new PropertyValues(30, 30, 10, 90);
    public static final PropertyValues CASTLE = new PropertyValues(20, 25, 5, 70);
    public static final PropertyValues MANOR = new PropertyValues(15, 10, 5, 60);
    public static final PropertyValues MANSION = new PropertyValues(10, 4, 4, 40);
    public static final PropertyValues VILLA = new PropertyValues(5, 0, 3, 30);
    public static final PropertyValues COTTAGE = new PropertyValues(3, 0, 2, 20);
    public static final PropertyValues SHACK = new PropertyValues(2, 0, 1, 0);
    public static final Vault FORTRESS_Vault = new Vault(40, 30, 20);
    public static final Vault CITADEL_Vault = new Vault(30, 30, 10);
    public static final Vault CASTLE_Vault = new Vault(20, 25, 5);
    public static final Vault MANOR_Vault = new Vault(15, 10, 5);
    public static final Vault MANSION_Vault = new Vault(10, 4, 5);
    public static final Vault VILLA_Vault = new Vault(5, 0, 3);
    public static final Vault COTTAGE_Vault = new Vault(3, 0, 2);
    public static final Vault SHACK_Vault = new Vault(2, 0, 1);



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
        super(PropertyConfig.FORTRESS, PropertyConfig.FORTRESS_Vault, name + " " + "Fortress");
    }
}

class Citadel extends Property {
    String propertyName = "Citadel";
    public Citadel(String name) {
        super(PropertyConfig.CITADEL, PropertyConfig.CITADEL_Vault, name + " " + "Citadel");
    }
}

class Castle extends Property {
    String propertyName = "Castle";
    public Castle(String name) {
        super(PropertyConfig.CASTLE, PropertyConfig.CASTLE_Vault, name + " " + "Castle");
    }
}

class Manor extends Property {
    String propertyName = "Manor";
    public Manor(String name) {
        super(PropertyConfig.MANOR, PropertyConfig.MANOR_Vault, name + " " + "Manor");
    }
}

class Mansion extends Property {
    String propertyName = "Mansion";
    public Mansion(String name) {
        super(PropertyConfig.MANSION, PropertyConfig.MANSION_Vault, name + " " + "Mansion");
    }
}

class Villa extends Property {
    String propertyName = "Ville";
    public Villa(String name) {
        super(PropertyConfig.VILLA, PropertyConfig.VILLA_Vault, name + " " + "Villa");
    }
}

class Cottage extends Property {
    String propertyName = "Cottage";
    public Cottage(String name) {
        super(PropertyConfig.COTTAGE, PropertyConfig.COTTAGE_Vault, name + " " + "Cottage");
    }
}

class Shack extends Property {
    String propertyName = "Shack";
    public Shack(String name) {
        super(PropertyConfig.SHACK, PropertyConfig.SHACK_Vault, name + " " + "Shack");
    }
}





