package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Character;
import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;

import java.util.Random;

public class MysticMine extends UtilityBuilding {

    private int alloyProduction;
    private int goldProduction;
    private Random random = new Random();

    public MysticMine(int basePrice, Character owner) {
        super(basePrice, owner);
        this.alloyProduction = Settings.get("mineProduction")*2;
        this.goldProduction = Settings.get("mineProduction");
        this.name = UtilityBuildings.MysticMine;
    }

    public void upgradeProduction() {
        this.alloyProduction = alloyProduction * 2;
        this.goldProduction = goldProduction * 2;
    }

    @Override
    public void upgrade() {
        level++;
        upgradeProduction();
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
