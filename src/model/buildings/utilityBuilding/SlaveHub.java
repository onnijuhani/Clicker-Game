package model.buildings.utilityBuilding;

import model.Settings;

public class SlaveHub extends UtilityBuilding {
    private int slaveAmount;
    private final int[] production = {10, 5 ,1};

    public SlaveHub(int basePrice) {
        super(basePrice);
        this.slaveAmount = Settings.get("mineProduction");
    }

    public int slaveAmount() {
        return slaveAmount;
    }

    public void increaseSlaveAmount() {
        this.slaveAmount++;
        increaseProduction();
    }

    public void increaseProduction(){
        production[0] = production[0] * slaveAmount;
        production[1] = production[1] * slaveAmount;
        production[2] = production[2] * slaveAmount;
    }

    @Override
    public void upgrade() {
        level++;
        increaseSlaveAmount();
    }
    public int getSlaveAmount() {
        return slaveAmount;
    }

    public void setSlaveAmount(int slaveAmount) {
        this.slaveAmount = slaveAmount;
    }

    public int[] getProduction() {
        return production;
    }


}
