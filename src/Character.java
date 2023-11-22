import java.util.ArrayList;
import java.util.HashMap;

public class Character implements TimeObserver {

    ArrayList<Slave> slaves;

    @Override
    public void TimeUpdate(int currentDay, int currentWeek, int currentMonth, int currentYear) {
    }
    String name;

    Wallet wallet;
    public Character() {
        this.wallet = new Wallet(0,0,0);
        this.slaves = new ArrayList<>();
        this.name = NameCreation.generateCharacterName();
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


    public Support(Authority authority) {
        this.authority = authority;
    }
}



class King extends AuthorityCharacter {


    public King() {
        super(); // Call the superclass constructor
    }
}


class Noble extends Support {


    public Noble(Authority authority) {
        super(authority);
    }
}

class Vanguard extends Support {
    // The salary field in Vanguard hides the one in Support

    public Vanguard(Authority authority) {
        super(authority);
        // Updating the salary field in the Support class
        super.salary = 1000;
    }
}


class Governor extends AuthorityCharacter {


    public Governor() {
    }
}

class Mercenary extends Support {

    public Mercenary(Authority authority) {
        super(authority);
    }
}

class Mayor extends AuthorityCharacter {


    public Mayor() {
    }
}

class Captain extends AuthorityCharacter {


    public Captain() {
    }
}

class Peasant extends Character {

    @Override
    public void TimeUpdate(int currentDay, int currentWeek, int currentMonth, int currentYear) {

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

    public HashMap<Resource, Resources> releaseTax(HashMap<Resource, Double> taxRates) {

        double amountFood = this.food.getAmount() * taxRates.get(Resource.Food);
        double amountAlloy = this.alloy.getAmount() * taxRates.get(Resource.Alloy);
        double amountGold = this.gold.getAmount() * taxRates.get(Resource.Gold);

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


    public Farmer(Authority quarterAuthority) {
        super(quarterAuthority);
    }
}

class Miner extends Peasant {


    public Miner(Authority quarterAuthority) {
        super(quarterAuthority);
    }
}

class Merchant extends Peasant {


    public Merchant(Authority quarterAuthority) {
        super(quarterAuthority);
    }
}

class Slave extends Character {

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

