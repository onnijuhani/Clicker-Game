import java.util.ArrayList;
import java.util.HashMap;

public class Character implements TimeObserver {

    @Override
    public void TimeUpdate(int currentDay, int currentWeek, int currentMonth, int currentYear) {
    }

    Wallet wallet;
    public Character() {
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}

class AuthorityCharacter extends Character {


}

class Support extends Character {

    Character supportFor;


}



class King extends AuthorityCharacter {


    public King() {
        super(); // Call the superclass constructor
    }
}


class Noble extends Support {

    public Noble() {
    }
}

class Vanguard extends Support {


    public Vanguard() {
    }
}

class Governor extends AuthorityCharacter {


    public Governor() {
    }
}

class Mercenary extends Support {


    public Mercenary() {
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


    public Peasant() {
        this.food = new Food(0);
        this.alloy = new Alloy(0);
        this.gold = new Gold(0);
        this.wallet = new Wallet(0, 0, 0);
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
    @Override
    public void TimeUpdate(int currentDay, int currentWeek, int currentMonth, int currentYear) {
        if (currentDay == 1) {
            generate(50,0,0);
        }
    }
    public Farmer() {
    }

}

class Miner extends Peasant {

    public Miner() {
    }


}

class Merchant extends Peasant {
    public Merchant() {
    }

}

class Slave extends Peasant {

    public Slave() {
    }
    @Override
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

