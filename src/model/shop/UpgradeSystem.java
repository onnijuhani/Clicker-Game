package model.shop;


import java.util.HashMap;
import java.util.Map;

public class UpgradeSystem {
    protected int level;
    protected int basePrice;
    protected int MAX_LEVEL = 6;
    protected int increaseDivider = 2;
    protected int value;
    protected final int PRICE_CEILING;
    protected final Map<String, Double> bonus = new HashMap<>();

    public UpgradeSystem(int basePrice) {
        this.level = 1;
        this.basePrice = basePrice;
        this.value = 1;
        this.PRICE_CEILING = basePrice * 200_000;
    }

    public void addBonus(String bonusName, double percentage){
        this.bonus.put(bonusName, percentage);
    }

    public void removeBonus(String bonusName){
        this.bonus.remove(bonusName);
    }

    public int getUpgradeLevel() {
        return level;
    }

    protected int calculateBonus(){
        double total = 1;
        for (Double bonus : bonus.values()){
            total += bonus;
        }
        return (int) total;
    }

    public static int roundToSensibleValue(int number) {
        int length = (int) (Math.log10(number) + 1);
        int divisor;

        if (length <= 3) {
            divisor = 10;
        } else {
            divisor = (int) Math.pow(10, length - 2);
        }
        return Math.round(number / (float)divisor) * divisor;
    }


    public int getUpgradePrice() {
        if (level < MAX_LEVEL) {
            return calculateUpgradePrice();
        } else {
            return Math.min(calculateUpgradePrice(), PRICE_CEILING);
        }
    }

    private int calculateUpgradePrice() {
        return Math.min((int) (basePrice * Math.pow(3, level - 1)), PRICE_CEILING);
    }

    public boolean increaseLevel() {
        level++;
        if (level <= MAX_LEVEL) {
            value *= 2;
        } else {
            value += Math.max(1, value / (increaseDivider * (level - MAX_LEVEL)));
        }
        return true;
    }

    public int getValue() {
        return value * calculateBonus();
    }

    public void decreaseLevel() {
        if (level > 1) {
            level--;
        }
    }

}
