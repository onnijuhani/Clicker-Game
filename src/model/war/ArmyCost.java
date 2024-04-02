package model.war;

import model.Settings;

public class ArmyCost {
    public static final int increaseArmyDefence = Settings.getInt("increaseArmyDefence");
    public static final int increaseArmyAttack = Settings.getInt("increaseArmyAttack");
    public static final int hireSoldierFood = Settings.getInt("hireSoldierFood");
    public static final int hireSoldierAlloy = Settings.getInt("hireSoldierAlloy");
    public static final int hireSoldierGold = Settings.getInt("hireSoldierGold");
    public static final int runningFood = Settings.getInt("runningFood");
    public static final int runningAlloy = Settings.getInt("runningAlloy");
    public static final int runningGold = Settings.getInt("runningGold");
}
