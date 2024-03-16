package model.buildings;

import model.Settings;

public class PropertyConfig {
    public static final PropertyValues FORTRESS = new PropertyValues(
            Settings.getInt("fortressFood"),
            Settings.getInt("fortressAlloy"),
            Settings.getInt("fortressGold"),
            Settings.getInt("fortressPower"));;
    public static final PropertyValues CITADEL = new PropertyValues(
            Settings.getInt("citadelFood"),
            Settings.getInt("citadelAlloy"),
            Settings.getInt("citadelGold"),
            Settings.getInt("citadelPower"));
    public static final PropertyValues CASTLE = new PropertyValues(
            Settings.getInt("castleFood"),
            Settings.getInt("castleAlloy"),
            Settings.getInt("castleGold"),
            Settings.getInt("castlePower"));

    public static final PropertyValues MANOR = new PropertyValues(
            Settings.getInt("manorFood"),
            Settings.getInt("manorAlloy"),
            Settings.getInt("manorGold"),
            Settings.getInt("manorPower"));

    public static final PropertyValues MANSION = new PropertyValues(
            Settings.getInt("mansionFood"),
            Settings.getInt("mansionAlloy"),
            Settings.getInt("mansionGold"),
            Settings.getInt("mansionPower"));

    public static final PropertyValues VILLA = new PropertyValues(
            Settings.getInt("villaFood"),
            Settings.getInt("villaAlloy"),
            Settings.getInt("villaGold"),
            Settings.getInt("villaPower"));

    public static final PropertyValues COTTAGE = new PropertyValues(
            Settings.getInt("cottageFood"),
            Settings.getInt("cottageAlloy"),
            Settings.getInt("cottageGold"),
            Settings.getInt("cottagePower"));

    public static final PropertyValues SHACK = new PropertyValues(
            Settings.getInt("shackFood"),
            Settings.getInt("shackAlloy"),
            Settings.getInt("shackGold"),
            Settings.getInt("shackPower"));
    public static class PropertyValues {
        int food;
        int alloy;
        int gold;



        int power;


        public PropertyValues(int food, int alloy, int gold, int power) {
            this.food = food;
            this.alloy = alloy;
            this.gold = gold;
            this.power = power;
        }
        public int getPower() {
            return power;
        }

    }
}



