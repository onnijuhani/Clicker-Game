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
        return basePrice * (int) Math.pow(3, level -1 );
    }

    public void upgrade() {
        level++;
    }

}
