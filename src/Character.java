import java.util.ArrayList;
import java.util.HashMap;

public class Character {
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

    Authority authorityOver;
}

class Support extends Character {


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

    protected Food food;
    protected Alloy alloy;
    protected Gold gold;


    public Peasant() {
    }

    public void walletTransfer(double percent){
        wallet.addFood(food.getAmount()*percent);
        wallet.addAlloy(alloy.getAmount()*percent);
        wallet.addGold(gold.getAmount()*percent);
    }

}

class Farmer extends Peasant {
    public Farmer() {
        super.food = new Food(0);
        super.wallet = new Wallet(0, 0, 0);
    }
    public Food getFood() {
        return food;
    }
    public void generateFood(int amount){
        this.food.add(amount);
    }
    public Food taxFood(double taxPercent) {
        double amount = this.food.getAmount()*taxPercent;
        this.food.subtract(amount);
        Food food = new Food(amount);
        return food;
    }
    @Override
    public void walletTransfer(double percent){
        double amount = food.getAmount()*percent;
        wallet.addFood(amount);
        this.food.subtract(amount);
    }
}

class Miner extends Peasant {

    public Miner() {
        super.alloy = new Alloy(0);
        super.wallet = new Wallet(0, 0, 0);
    }

    public Alloy getalloy() {
        return alloy;
    }
    public void generateAlloy(int amount){
        this.alloy.add(amount);
    }
    public Alloy taxAlloy(double taxPercent) {
        double amount = this.alloy.getAmount()*taxPercent;
        this.alloy.subtract(amount);
        Alloy alloy = new Alloy(amount);
        return alloy;
    }
    @Override
    public void walletTransfer(double percent){
        double amount = alloy.getAmount()*percent;
        wallet.addAlloy(amount);
        this.alloy.subtract(amount);
    }

}

class Merchant extends Peasant {
    public Merchant() {
        super.gold = new Gold(0);
        super.wallet = new Wallet(0, 0, 0);
    }

    public Gold getgold() {
        return gold;
    }
    public void generateGold(int amount){
        this.gold.add(amount);
    }
    public Gold taxGold(double taxPercent) {
        double amount = this.gold.getAmount()*taxPercent;
        this.gold.subtract(amount);
        Gold gold = new Gold(amount);
        return gold;
    }
    @Override
    public void walletTransfer(double percent){
        double amount = gold.getAmount()*percent;
        wallet.addAlloy(amount);
        this.gold.subtract(amount);
    }
}

class Slave extends Peasant {


    public Slave() {
        super.food = new Food(0);
        super.alloy = new Alloy(0);
        super.gold = new Gold(0);
        super.wallet = new Wallet(0, 0, 0);
    }

    public void generate(int food, int alloy, int gold){
        this.food.add(food);
        this.alloy.add(alloy);
        this.gold.add(gold);
    }

    public HashMap<String, Resources> tax(double taxPercent){
        HashMap<String, Resources> taxed = new HashMap<>();

        double amountFood = this.food.getAmount()*taxPercent;
        this.food.subtract(amountFood );
        Food food = new Food(amountFood );
        double amountAlloy = this.alloy.getAmount()*taxPercent;
        this.alloy.subtract(amountAlloy);
        Alloy alloy = new Alloy(amountAlloy);
        double amountGold = this.gold.getAmount()*taxPercent;
        this.gold.subtract(amountGold);
        Gold gold = new Gold(amountGold);

        taxed.put("Food",food);
        taxed.put("Alloys",alloy);
        taxed.put("Gold", gold);

        return taxed;
    }
}

