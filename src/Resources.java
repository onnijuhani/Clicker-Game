import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Resources {

    private double amount;

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void add(double amount) {
        this.amount += amount;
    }

    public void subtract(double amount) {
        this.amount -= amount;
    }

}

class Wallet {
    private Food food;
    private Alloy alloy;
    private Gold gold;

    public Wallet(double food, double alloy, double gold) {
        this.food = new Food(food);
        this.alloy = new Alloy(alloy);
        this.gold = new Gold(gold);
    }
    public HashMap<String, Double> getWalletValues() {
        HashMap<String, Double> wallet = new HashMap<>();
        wallet.put("Food", food.getAmount());
        wallet.put("Alloys", alloy.getAmount());
        wallet.put("Gold", gold.getAmount());
        return wallet;
    }
    public void addResources(double food, double alloys, double gold) {
        this.food.add(food);
        this.alloy.add(alloys);
        this.gold.add(gold);
    }

    public void subtractResources(double food, double alloys, double gold) {
        this.food.subtract(food);
        this.alloy.subtract(alloys);
        this.gold.subtract(gold);
    }

    public double getFood() {
        return food.getAmount();
    }

    public void setFood(double food) {
        this.food.setAmount(food);
    }

    public void addFood(double food) {
        this.food.add(food);
    }

    public void subtractFood(double food) {
        this.food.subtract(food);
    }

    public double getAlloy() {
        return alloy.getAmount();
    }

    public void setAlloy(double alloy) {
        this.alloy.setAmount(alloy);
    }

    public void addAlloy(double alloy) {
        this.alloy.add(alloy);
    }

    public void subtractAlloy(double alloy) {
        this.alloy.subtract(alloy);
    }

    public double getGold() {
        return gold.getAmount();
    }

    public void setGold(double gold) {
        this.gold.setAmount(gold);
    }

    public void addGold(double gold) {
        this.gold.add(gold);
    }

    public void subtractGold(double gold) {
        this.gold.subtract(gold);
    }
}

class Vault extends Wallet {

    public Vault(double food, double alloy, double gold) {
        super(food, alloy, gold);
    }
}

class Food extends Resources {

    private static double totalFoodCount = 0;

    public Food(double amount) {
        super.add(amount);
        totalFoodCount += amount;
    }

    @Override
    public void add(double amount) {
        super.add(amount);
        totalFoodCount += amount;
    }
    @Override
    public void subtract(double amount) {
        super.subtract(amount);
        totalFoodCount -= amount;
    }

    public static double getTotalFoodCount(){
        return totalFoodCount;
    }



}

class Alloy extends Resources {
    private static double totalAlloyCount = 0;

    public Alloy(double amount) {
        super.add(amount);
        totalAlloyCount += amount;
    }

    @Override
    public void add(double amount) {
        super.add(amount);
        totalAlloyCount += amount;
    }

    @Override
    public void subtract(double amount) {
        super.subtract(amount);
        totalAlloyCount -= amount;
    }

    public static double getTotalAlloyCount() {
        return totalAlloyCount;
    }
}

class Gold extends Resources {
    private static double totalGoldCount = 0;

    public Gold(double amount) {
        super.add(amount);
        totalGoldCount += amount;
    }

    @Override
    public void add(double amount) {
        super.add(amount);
        totalGoldCount += amount;
    }

    @Override
    public void subtract(double amount) {
        super.subtract(amount);
        totalGoldCount -= amount;
    }

    public static double getTotalGoldCount() {
        return totalGoldCount;
    }
}


enum Resource {
    Food,
    Alloy,
    Gold
}