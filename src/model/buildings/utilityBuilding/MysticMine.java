package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.Character;

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

        // If the result is negative, set it to 0
        if (result < 0) {
            result = 0;
        }

        return (int) Math.round(result);
    }


    public int getAlloyProduction() {
        return alloyProduction;
    }

    public int getGoldProduction() {
        return goldProduction;
    }
}
