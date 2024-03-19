package model.shop;

public class UpgradeSystem {
    protected int level;


    protected int basePrice;
    protected final int MAX_LEVEL = 10;

    public UpgradeSystem(int basePrice) {
        this.level = 1;
        this.basePrice = basePrice;
    }
    public int getUpgradeLevel() {
        return level;
    }
    public int getUpgradePrice() {
        return basePrice * (int) Math.pow(3, level -1 );
    }
    public boolean upgradeLevel() {
        if (level < MAX_LEVEL) {
            level++;
            return true;
        } else {
            return false;
        }
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
