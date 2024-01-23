package model.buildings;

import model.Settings;

public class PropertyConfig {
    public static final PropertyValues FORTRESS = new PropertyValues(
            Settings.get("fortressFood"),
            Settings.get("fortressAlloy"),
            Settings.get("fortressGold"),
            Settings.get("fortressPower"));;
    public static final PropertyValues CITADEL = new PropertyValues(
            Settings.get("citadelFood"),
            Settings.get("citadelAlloy"),
            Settings.get("citadelGold"),
            Settings.get("citadelPower"));
    public static final PropertyValues CASTLE = new PropertyValues(
            Settings.get("castleFood"),
            Settings.get("castleAlloy"),
            Settings.get("castleGold"),
            Settings.get("castlePower"));

    public static final PropertyValues MANOR = new PropertyValues(
            Settings.get("manorFood"),
            Settings.get("manorAlloy"),
            Settings.get("manorGold"),
            Settings.get("manorPower"));

    public static final PropertyValues MANSION = new PropertyValues(
            Settings.get("mansionFood"),
            Settings.get("mansionAlloy"),
            Settings.get("mansionGold"),
            Settings.get("mansionPower"));

    public static final PropertyValues VILLA = new PropertyValues(
            Settings.get("villaFood"),
            Settings.get("villaAlloy"),
            Settings.get("villaGold"),
            Settings.get("villaPower"));

    public static final PropertyValues COTTAGE = new PropertyValues(
            Settings.get("cottageFood"),
            Settings.get("cottageAlloy"),
            Settings.get("cottageGold"),
            Settings.get("cottagePower"));

    public static final PropertyValues SHACK = new PropertyValues(
            Settings.get("shackFood"),
            Settings.get("shackAlloy"),
            Settings.get("shackGold"),
            Settings.get("shackPower"));
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
    }
}



