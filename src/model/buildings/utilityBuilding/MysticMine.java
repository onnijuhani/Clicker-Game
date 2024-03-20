package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Person;

import java.util.Random;

public class MysticMine extends UtilityBuilding {

    private int alloyProduction;
    private int goldProduction;
    private final Random random = new Random();

    public MysticMine(int basePrice, Person owner) {
        super(basePrice, owner);
        this.alloyProduction = Settings.getInt("mineProduction")*2;
        this.goldProduction = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.MysticMine;
    }

    @Override
    public boolean upgradeLevel() {
        level++;
        if (level <= MAX_LEVEL) {
            alloyProduction *= 2;
            goldProduction *= 2;
        } else {
            int alloyIncreaseAmount = Math.max(alloyProduction / increaseDivider, 1);
            int goldIncreaseAmount = Math.max(goldProduction / increaseDivider, 1);
            alloyProduction += alloyIncreaseAmount;
            goldProduction += goldIncreaseAmount;
            increaseDivider *=2;
        }
        return true;
    }

    public void upgradeProduction() {
        this.alloyProduction = alloyProduction * 2;
        this.goldProduction = goldProduction * 2;
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


}
