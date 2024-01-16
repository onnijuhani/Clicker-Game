import java.util.LinkedList;

public class Character implements TimeObserver {
    protected static int totalAmount;
    LinkedList<Slave> slaves;
    Nation nation;
    public String name;
    protected Wallet wallet;
    protected Property property;
    protected LinkedList<Character> allies;
    protected LinkedList<Character> enemies;
    public Character() {
        this.wallet = new Wallet();
        this.slaves = new LinkedList<>();
        this.allies = new LinkedList<>();
        this.enemies = new LinkedList<>();
        this.name = NameCreation.generateCharacterName();
        subscribeToTimeEvents();
    }
    public String getName() {
        return name;
    }
    public Nation getNation() {
        return nation;
    }
    public void setNation(Nation nation) {
        this.nation = nation;
    }
    @Override
    public void timeUpdate(int day, int week, int month, int year) {
    }
    public void subscribeToTimeEvents() {
        TimeEventManager.subscribe(this);
    }
    @Override
    public String toString() {
        return name + "  Main House: " + property;
    }
    public void setProperty(Property property){
        this.property = property;
    }
    public Property getProperty(){
        return property;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void addSlave(Slave slave){
        slaves.add(slave);
    }
    public void deleteSlave(Slave slave){
        slaves.remove(slave);
    }
    public void addAlly(Character ally){
        allies.add(ally);
    }
    public void deleteAlly(Character ally){
        allies.remove(ally);
    }
    public void addEnemy(Character enemy){
        enemies.add(enemy);
    }
    public void deleteEnemy(Character enemy){
        enemies.remove(enemy);
    }
}

class AuthorityCharacter extends Character {
    private Authority taxLiabilityTo;
    private Tax taxForm;
    public AuthorityCharacter() {
        this.taxForm = new Tax();
    }
    public Authority getTaxLiabilityTo() {
        return taxLiabilityTo;
    }
    public void setTaxLiabilityTo(Authority taxLiabilityTo) {
        this.taxLiabilityTo = taxLiabilityTo;
    }
    public Tax getTaxForm() {
        return taxForm;
    }
    public void setTaxForm(Tax taxForm) {
        this.taxForm = taxForm;
    }
}
class King extends AuthorityCharacter {
    public static int totalAmount;
    public King() {
        this.totalAmount += 1;
    }
}

class Governor extends AuthorityCharacter {
    public static int totalAmount;
    public Governor() {
        this.totalAmount += 1;
    }
}


class Mayor extends AuthorityCharacter {
    public static int totalAmount;
    public Mayor() {
        this.totalAmount += 1;
    }
}

class Captain extends AuthorityCharacter {
    public static int totalAmount;
    public Captain() {
        this.totalAmount += 1;
    }
}


class Support extends Character {
    protected Salary salary;
    protected Authority authorityTo;
    public static int totalAmount;
    public Support(Authority authorityTo) {
        this.authorityTo = authorityTo;
        this.totalAmount += 1;
        createProperty();
    }
    public void createProperty(){
        this.property = PropertyCreation.createSupportProperty(this);
        this.property.setOwner(this);
    }
    public Salary getSalary() {
        return salary;
    }
    public void setSalary(Salary salary) {
        this.salary = salary;
    }
}
class Noble extends Support {
    public static int totalAmount;
    public Noble(Authority authority) {
        super(authority);
        this.salary = new Salary(15,10,5);
        this.totalAmount += 1;
    }
}
class Vanguard extends Support {
    public static int totalAmount;
    public Vanguard(Authority authority) {
        super(authority);
        this.salary = new Salary(40,30,20);
        this.totalAmount += 1;
    }
}
class Mercenary extends Support {
    public static int totalAmount;
    public Mercenary(Authority authority) {
        super(authority);
        this.salary = new Salary(30,30,10);
        this.totalAmount += 1;
    }
}


class Peasant extends Character implements TimeObserver {
    class Employment {
        private double food;
        private double alloy;
        private double gold;
        private double bonusFood = 1;
        private double bonusAlloy = 1;
        private double bonusGold = 1;

        public Employment(double foodBaseRate, double alloyBaseRate, double goldBaseRate){
            this.food = foodBaseRate;
            this.alloy = alloyBaseRate;
            this.gold = goldBaseRate;
        }
        public TransferPackage payment(){
            TransferPackage transfer = new TransferPackage(food*bonusFood,alloy*bonusAlloy,gold*bonusGold);
            return transfer;
        }
        public void setBonusFood(double bonusFood) {
            this.bonusFood = bonusFood;
        }
        public void setBonusAlloy(double bonusAlloy) {
            this.bonusAlloy = bonusAlloy;
        }
        public void setBonusGold(double bonusGold) {
            this.bonusGold = bonusGold;
        }
        public void setFood(double food) {
            this.food = food;
        }
        public void setAlloy(double alloy) {
            this.alloy = alloy;
        }
        public void setGold(double gold) {
            this.gold = gold;
        }
    }

    @Override
    public void timeUpdate(int currentDay, int currentWeek, int currentMonth, int currentYear) {
        if (currentDay == 1 && currentWeek == 1){
            generate();
        }
    }
    protected WorkWallet workWallet;
    protected Authority quarterAuthority;
    protected double generateRate = 0;
    protected  Employment employment;

    public Peasant() {
        this.workWallet = new WorkWallet();
        super.property = PropertyCreation.createPeasantProperty(this);
    }
    public void generate(){
        this.workWallet.addResources(getEmployment().payment());
    }
    public Employment getEmployment() {
        return employment;
    }

    public Wallet getWorkWallet() {
        return workWallet;
    }
    public void setWorkWallet(WorkWallet workWallet) {
        this.workWallet = workWallet;
    }
    public Authority getQuarterAuthority() {
        return quarterAuthority;
    }
    public void setQuarterAuthority(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
    }
    public double getGenerateRate() {
        return generateRate;
    }
    public void setGenerateRate(double generateRate) {
        this.generateRate = generateRate;
    }
}

class Farmer extends Peasant {
    public static int totalAmount;
    public Farmer(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        super.employment = new Employment(100,0,0);
        this.totalAmount += 1;
    }
}

class Miner extends Peasant {
    public static int totalAmount;
    public Miner(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        super.employment = new Employment(0,100,0);
        this.totalAmount += 1;
    }
}

class Merchant extends Peasant {
    public static int totalAmount;
    public Merchant(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        super.employment = new Employment(0,0,100);
        this.totalAmount += 1;
    }
}

class Slave extends Peasant {
    public static int totalAmount;
    Character owner;
    public Slave(Character owner) {
        this.owner = owner;
        super.employment = new Employment(10,10,1);
        this.totalAmount += 1;
    }
}

enum Peasants {
    Slave,
    Farmer,
    Miner,
    Merchant,
}

enum Authorities {
    King,
    Governor,
    Mayor,
    Captain,
}

enum Supports {
    Noble,
    Vanguard,
    Mercenary,
}

