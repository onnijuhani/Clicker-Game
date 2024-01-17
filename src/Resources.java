public class Resources {
    private double amount = 0;

    public double getAmount() {
        return amount;
    }
    public void add(double amount) {
        this.amount += amount;
    }
    public void subtract(double amount) {
        this.amount -= amount;
    }
    @Override
    public String toString() {
        return "" + amount;
    }
}

class Wallet {
    private Food food;
    private Alloy alloy;
    private Gold gold;

    public Wallet() {
        this.food = new Food();
        this.alloy = new Alloy();
        this.gold = new Gold();
    }
    public boolean hasEnoughResource(Resource type, double amount) {
        switch (type) {
            case Food:
                return food.getAmount() >= amount;
            case Alloy:
                return alloy.getAmount() >= amount;
            case Gold:
                return gold.getAmount() >= amount;
            default:
                throw new IllegalArgumentException("Unsupported resource type: " + type);
        }
    }
    public double[] getWalletValues() {
        return new double[]{food.getAmount(), alloy.getAmount(), gold.getAmount()};
    }
    public void addResources(TransferPackage transfer) {
        double[] amounts = transfer.getAll();
        this.food.add(amounts[0]);
        this.alloy.add(amounts[1]);
        this.gold.add(amounts[2]);
    }
    public void subtractResources(TransferPackage transfer) {
        double[] amounts = transfer.getAll();
        this.food.subtract(amounts[0]);
        this.alloy.subtract(amounts[1]);
        this.gold.subtract(amounts[2]);
    }

    public void deposit(Wallet depositFromWallet, TransferPackage transfer){
        this.addResources(transfer);
        depositFromWallet.subtractResources(transfer);
    }

    public void withdrawal(Wallet withdrawalToWallet, TransferPackage transfer){
        this.subtractResources(transfer);
        withdrawalToWallet.addResources(transfer);
    }

    @Override
    public String toString() {
        return "Food: "+food.getAmount() + " Alloys: "+alloy.getAmount() + " Gold: "+gold.getAmount();
    }


    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }


    public Alloy getAlloy() {
        return alloy;
    }

    public void setAlloy(Alloy alloy) {
        this.alloy = alloy;
    }


    public Gold getGold() {
        return gold;
    }

    public void setGold(Gold gold) {
        this.gold = gold;
    }

}

class Vault extends Wallet {

    private int protection = 100;
    public Vault() {
    }
}

class WorkWallet extends Wallet {
    private boolean taxedOrNot;
    public WorkWallet() {
        this.taxedOrNot = false;
    }
    public void isTaxed(){
        taxedOrNot = true;
    }
    public void notTaxed(){
        taxedOrNot = false;
    }
}

class Food extends Resources {
    private static double totalFoodCount = 0;
    public Food() {
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
    public static double getTotalFoodCount() {
        return totalFoodCount;
    }
}

class Alloy extends Resources {
    private static double totalAlloyCount = 0;
    public Alloy() {
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
    public Gold() {
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

record TransferPackage(double food, double alloy, double gold) {
    public double[] getAll() {
        return new double[]{food, alloy, gold};
    }
    public static TransferPackage fromArray(double[] values) {
        if (values.length != 3) {
            throw new IllegalArgumentException("Array must have exactly 3 elements.");
        }
        return new TransferPackage(values[0], values[1], values[2]);
    }
    public static TransferPackage fromAnotherPackage(TransferPackage other) {
        return new TransferPackage(other.food, other.alloy, other.gold);
    }
    public static TransferPackage fromEnum(Resource type, double amount) {
        switch (type) {
            case Food:
                return new TransferPackage(amount, 0, 0);
            case Alloy:
                return new TransferPackage(0, amount, 0);
            case Gold:
                return new TransferPackage(0, 0, amount);
            default:
                throw new IllegalArgumentException("Unsupported resource type: " + type);
        }
    }

}


class Salary{

    private double food;
    private double alloy;
    private double gold;

    public Salary(double food, double alloy, double gold){
        this.food = food;
        this.alloy = alloy;
        this.gold = gold;
    }
    public double[] getAll(){
        return new double[]{food, alloy, gold};
    }

    public void changePay(double foodRate, double alloyRate, double goldRate){
        this.food = this.food * (1 + foodRate);
        this.alloy = this.alloy * (1 + alloyRate);
        this.gold = this.gold * (1 + goldRate);
    }
    public double getFood() {
        return food;
    }
    public void setFood(double food) {
        this.food = food;
    }
    public double getAlloy() {
        return alloy;
    }
    public void setAlloy(double alloy) {
        this.alloy = alloy;
    }
    public double getGold() {
        return gold;
    }
    public void setGold(double gold) {
        this.gold = gold;
    }
}

enum Resource {
    Food,
    Alloy,
    Gold
}
