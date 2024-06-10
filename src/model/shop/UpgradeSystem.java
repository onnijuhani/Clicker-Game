package model.shop;


import java.util.HashMap;
import java.util.Map;

public class UpgradeSystem {
    protected int level;
    protected int basePrice;
    protected int MAX_LEVEL = 8;
    protected int increaseDivider = 3;
    protected int value;
    protected final int PRICE_CEILING;
    protected final Map<String, Double> bonus = new HashMap<>();
    protected double priceIncreaseAmount = 2.5;

    public UpgradeSystem(int basePrice) {
        this.level = 1;
        this.basePrice = basePrice;
        this.value = 1;
        this.PRICE_CEILING = basePrice * 4000;
    }
    public UpgradeSystem(int basePrice, int price_ceiling_coef) {
        this.level = 1;
        this.basePrice = basePrice;
        this.value = 1;
        this.PRICE_CEILING = basePrice * price_ceiling_coef;
    }
    public UpgradeSystem(int basePrice, double priceIncreaseAmount) {
        this.level = 1;
        this.basePrice = basePrice;
        this.value = 1;
        this.PRICE_CEILING = basePrice * 5000;
        this.priceIncreaseAmount = priceIncreaseAmount;
    }
    public UpgradeSystem(int basePrice, int price_ceiling_coef, double priceIncreaseAmount) {
        this.level = 1;
        this.basePrice = basePrice;
        this.value = 1;
        this.PRICE_CEILING = basePrice * price_ceiling_coef;
        this.priceIncreaseAmount = priceIncreaseAmount;
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

    public int getUpgradePrice() {
        if (level < MAX_LEVEL) {
            return calculateUpgradePrice();
        } else {
            return Math.min(calculateUpgradePrice(), PRICE_CEILING);
        }
    }

    protected int calculateUpgradePrice() {
        return Math.min((int) (basePrice * Math.pow(priceIncreaseAmount, level - 1)), PRICE_CEILING);
    }

    public boolean increaseLevel() {
        level++;
        if (level <= MAX_LEVEL) {
            value *= 2;
        } else {
            int minAdjustment = (int) (value * 0.001);
            value += Math.max(minAdjustment, value / (increaseDivider * (level - MAX_LEVEL)));
        }
        return true;
    }

    public int getValue() {
        return value * calculateBonus();
    }

    public void decreaseLevel() {
        if (level > 1) {
            level--;
            value /= 2;
        }
    }



}
