import java.util.ArrayList;
import java.util.HashMap;

public class Character implements TimeObserver {

    public static int getTotalAmount() {
        return totalAmount;
    }

    protected static int totalAmount;
    ArrayList<Slave> slaves;

    Nation nation;
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

    public String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    protected Wallet wallet;

    protected Property property;

    public Character() {
        this.wallet = new Wallet(0,0,0);
        this.slaves = new ArrayList<>();
        this.name = NameCreation.generateCharacterName();
        subscribeToTimeEvents();
    }

    public void setProperty(Property property){
        this.property = property;
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

    public void collectResources(Character character, double percent){

        Wallet wallet = character.getWallet();

        double food =  wallet.getFood();
        double alloy =  wallet.getAlloy();
        double gold =  wallet.getGold();

        double foodAmount = food * percent;
        double alloyAmount = alloy * percent;
        double goldAmount = gold * percent;

        wallet.subtractFood(foodAmount);
        wallet.subtractAlloy(alloyAmount);
        wallet.subtractGold(goldAmount);

        this.wallet.addResources(foodAmount, alloyAmount, goldAmount);
    }

}

class AuthorityCharacter extends Character {


}

class Support extends Character {

    double salary = 400;

    Authority authority;

    public static int totalAmount;

    public Support(Authority authority) {
        this.authority = authority;
        this.totalAmount += 1;
    }
}



class King extends AuthorityCharacter {

    public static int totalAmount;
    public King() {
        super(); // Call the superclass constructor
        this.totalAmount += 1;
    }
}


class Noble extends Support {

    public static int totalAmount;
    public Noble(Authority authority) {
        super(authority);
        this.totalAmount += 1;
    }
}

class Vanguard extends Support {
    // The salary field in Vanguard hides the one in Support
    public static int totalAmount;
    public Vanguard(Authority authority) {
        super(authority);
        // Updating the salary field in the Support class
        super.salary = 1000;
        this.totalAmount += 1;
    }
}


class Governor extends AuthorityCharacter {

    public static int totalAmount;
    public Governor() {
        this.totalAmount += 1;
    }
}

class Mercenary extends Support {
    public static int totalAmount;
    public Mercenary(Authority authority) {
        super(authority);
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

class Peasant extends Character implements TimeObserver {

    @Override
    public void timeUpdate(int currentDay, int currentWeek, int currentMonth, int currentYear) {
        if (currentDay == 1 && currentWeek == 1){
            generate(100, 100, 50);
        }

    }

    protected Food food;
    protected Alloy alloy;
    protected Gold gold;
    public Authority quarterAuthority;


    public Peasant(Authority quarterAuthority) {
        this.food = new Food(0);
        this.alloy = new Alloy(0);
        this.gold = new Gold(0);
        this.wallet = new Wallet(0, 0, 0);
        this.quarterAuthority = quarterAuthority;
        super.property = new Shack("default");
        property.setOwner(this);
    }

    public void walletTransfer(double percent){
        wallet.addFood(food.getAmount()*percent);
        wallet.addAlloy(alloy.getAmount()*percent);
        wallet.addGold(gold.getAmount()*percent);
    }

    public void generate(int food, int alloy, int gold){
        this.food.add(food);
        this.alloy.add(alloy);
        this.gold.add(gold);
    }

    public HashMap<Resource, Double> getResources(){
        HashMap<Resource, Double> resources = new HashMap<>();
        resources.put(Resource.Food,food.getAmount());
        resources.put(Resource.Alloy,alloy.getAmount());
        resources.put(Resource.Gold,gold.getAmount());
        return resources;
    }


    public HashMap<Resource, Resources> releaseTax(HashMap<Resource, Double> taxRates) {

        double amountFood = this.food.getAmount() * taxRates.get(Resource.Food);
        double amountAlloy = this.alloy.getAmount() * taxRates.get(Resource.Alloy);
        double amountGold = this.gold.getAmount() * taxRates.get(Resource.Gold);

        this.walletTransfer(1);

        this.food.subtract(amountFood);
        this.alloy.subtract(amountAlloy);
        this.gold.subtract(amountGold);

        Food food = new Food(amountFood);
        Alloy alloy = new Alloy(amountAlloy);
        Gold gold = new Gold(amountGold);

        HashMap<Resource, Resources> collected = new HashMap<>();
        collected.put(Resource.Food,food);
        collected.put(Resource.Alloy,alloy);
        collected.put(Resource.Gold,gold);

        return collected;
    }


    public Food getFood() {
        return food;
    }

    public Alloy getAlloy() {
        return alloy;
    }

    public Gold getGold() {
        return gold;
    }

}

class Farmer extends Peasant {
    public static int totalAmount;
    public Farmer(Authority quarterAuthority) {
        super(quarterAuthority);
        this.totalAmount += 1;
    }

    @Override
    public void generate(int food, int alloy, int gold){
        this.food.add(food * 1);
        this.alloy.add(alloy * 0);
        this.gold.add(gold * 0);

    }
}

class Miner extends Peasant {
    public static int totalAmount;
    public Miner(Authority quarterAuthority) {
        super(quarterAuthority);
        this.totalAmount += 1;
    }
    @Override
    public void generate(int food, int alloy, int gold){
        this.food.add(food * 0);
        this.alloy.add(alloy * 1);
        this.gold.add(gold * 0);
    }

}

class Merchant extends Peasant {
    public static int totalAmount;
    public Merchant(Authority quarterAuthority) {
        super(quarterAuthority);
        this.totalAmount += 1;
    }
    @Override
    public void generate(int food, int alloy, int gold){
        this.food.add(food * 0);
        this.alloy.add(alloy * 0);
        this.gold.add(gold * 1);

    }
}

class Slave extends Character {
    public static int totalAmount;
    Character owner;
    protected Food food;
    protected Alloy alloy;
    protected Gold gold;


    public Slave(Character owner) {
        this.food = new Food(0);
        this.alloy = new Alloy(0);
        this.gold = new Gold(0);
        this.wallet = new Wallet(0, 0, 0);
        this.owner = owner;
        this.totalAmount += 1;
    }

    public void generate(int food, int alloy, int gold){
        this.food.add(food);
        this.alloy.add(alloy);
        this.gold.add(gold);
        wallet.addResources(this.food.getAmount(), this.alloy.getAmount(), this.gold.getAmount());
        this.food.subtract(this.food.getAmount());
        this.alloy.subtract(this.alloy.getAmount());
        this.gold.subtract(this.gold.getAmount());
    }

    public HashMap<Resource, Resources> releaseTax(HashMap<Resource, Double> taxRates) {

        double amountFood = this.food.getAmount() * 1;
        double amountAlloy = this.alloy.getAmount() * 1;
        double amountGold = this.gold.getAmount() * 1;

        this.food.subtract(amountFood);
        this.alloy.subtract(amountAlloy);
        this.gold.subtract(amountGold);

        Food food = new Food(amountFood);
        Alloy alloy = new Alloy(amountAlloy);
        Gold gold = new Gold(amountGold);

        HashMap<Resource, Resources> collected = new HashMap<>();
        collected.put(Resource.Food,food);
        collected.put(Resource.Alloy,alloy);
        collected.put(Resource.Gold,gold);

        return collected;
    }

}

