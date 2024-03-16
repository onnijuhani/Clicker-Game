package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Character;
import model.stateSystem.EventTracker;
import model.resourceManagement.TransferPackage;

import java.util.Random;

public class MysticMine extends UtilityBuilding {

    private int alloyProduction;
    private int goldProduction;
    private Random random = new Random();

    public MysticMine(int basePrice, Character owner) {
        super(basePrice, owner);
        this.alloyProduction = Settings.getInt("mineProduction")*2;
        this.goldProduction = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.MysticMine;
    }

    public void upgradeProduction() {
        this.alloyProduction = alloyProduction * 2;
        this.goldProduction = goldProduction * 2;
    }

    @Override
    public boolean upgradeLevel() {
        if (level < MAX_LEVEL) {
            level++;
            upgradeProduction();
            return true;
        } else {
            owner.getEventTracker().addEvent(EventTracker.Message("Error", "Max level reached"));
            return false;
        }
    }

    public int calculateRandomAlloyProduction() {
        return calculateNormalDistValue(alloyProduction);
    }

    public int calculateRandomGoldProduction() {
        return calculateNormalDistValue(goldProduction);
    }

    private int calculateNormalDistValue(int mean) {
        double result = random.nextGaussian() * ( (double) mean ) + (double) mean / 2;

        if (result < 0) {
            result = 0;
        }

        return (int) Math.round(result);
    }
    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() +"\n"+
                        "Alloys and Gold"
        );
    }
    @Override
    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(0, calculateRandomAlloyProduction(),calculateRandomGoldProduction());
        owner.getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
    }



}
