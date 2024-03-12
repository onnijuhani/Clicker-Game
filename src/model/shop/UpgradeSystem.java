package model.shop;

public class UpgradeSystem {
    protected int level;
    protected int basePrice;

    public UpgradeSystem(int basePrice) {
        this.level = 1;
        this.basePrice = basePrice;
    }

    public int getUpgradeLevel() {
        return level;
    }

    public int getUpgradePrice() {
        return basePrice * (int) Math.pow(4, level -1 );
    }

    public void upgradeLevel() {
        level++;
    }
    public void decreaseLevel() {
        if (level > 1) {
            level--;
        }
    }

}
