package model.shop;

public class UpgradeSystem {
    protected int level;


    protected int basePrice;
    protected int MAX_LEVEL = 6;
    protected int increaseDivider = 1;
    protected int finalPrice = 0;
    protected int value;
    protected int upgradePrice;

    public UpgradeSystem(int basePrice) {
        this.level = 1;
        this.basePrice = basePrice;
        this.upgradePrice = basePrice;
    }
    public int getUpgradeLevel() {
        return level;
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


    public int getUpgrade() {
        if(level < MAX_LEVEL){
            upgradePrice = basePrice * (int) Math.pow(4, level );
            roofPrice();
            upgradePrice = roundToSensibleValue(upgradePrice);
            return upgradePrice;
        }
        else if(level == MAX_LEVEL) {
            System.out.println("wtf "+upgradePrice);
            upgradePrice = (int) (upgradePrice * 3);
            roofPrice();
            upgradePrice = roundToSensibleValue(upgradePrice);
            return upgradePrice;
        }
        else if(increaseDivider < 256) {
            upgradePrice = (int) (upgradePrice * 2);
            upgradePrice = roundToSensibleValue(upgradePrice);
            finalPrice = upgradePrice;
            roofPrice();
            return upgradePrice;
        }
        else{
            roofPrice();
            return finalPrice;
        }
    }

    private void roofPrice() {
        if (upgradePrice > 5_000_000){
            upgradePrice = 5_000_000;
        }
        if (finalPrice > 5_000_000){
            finalPrice = 5_000_000;
        }
    }

    public int getUpgradePrice() {
        return upgradePrice;
    }

    public boolean upgradeLevel() {
        level++;
        if (level <= MAX_LEVEL) {
            value *= 2;
        } else {
            int increaseAmount = Math.max(value / increaseDivider, 1);
            value += increaseAmount;
            increaseDivider *=2;
        }
        return true;
    }



    public void decreaseLevel() {
        if (level > 1) {
            level--;
        }
    }

    public void setBasePrice(int basePrice) {
        this.basePrice = basePrice;
    }
}
