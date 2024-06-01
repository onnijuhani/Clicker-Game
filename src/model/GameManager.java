package model;

import model.resourceManagement.wallets.Vault;

import java.util.LinkedList;

public class GameManager {
    private static final int foodConsumptionDay = Settings.getInt("foodConsumptionDay");

    private static int foodConsumptionRate = Settings.getInt("foodConsumption");


    private static final int foodConsumptionIncreaseInterval = Settings.getInt("foodConsumptionIncreaseInterval");
    private static final int foodConsumptionIncreaseRate = Settings.getInt("foodConsumptionIncreaseRate");
    private static int currentYear = 0;

    private static final int maintenanceDay = Settings.getInt("maintenance");



    private static final LinkedList<Vault> allVaultsInGame = new LinkedList<>();
    private static final double vaultInterestRate = Settings.getDouble("vaultInterestRate");

    public static LinkedList<Vault> getAllVaultsInGame() {
        return allVaultsInGame;
    }
    public static void applyInterestRate(){
        for (Vault vault : allVaultsInGame){
            vault.applyInterest(vaultInterestRate);
        }
    }
    public static void releaseLockedResources(){
        for (Vault vault : allVaultsInGame){
            vault.releaseLockedResources();
        }
    }

    public static int getFoodConsumptionRate() {
        return foodConsumptionRate;
    }
    public static int getMaintenanceDay() {
        return maintenanceDay;
    }
    public static int getFoodConsumptionDay() {
        return foodConsumptionDay;
    }

    public static void updateYearly(int year) {
        if (year > currentYear) {
            currentYear = year;
            if ((year % foodConsumptionIncreaseInterval) == 0) {
                foodConsumptionRate += Settings.getInt("foodConsumption") * foodConsumptionIncreaseRate;
                applyInterestRate();
                releaseLockedResources();
            }
        }
    }
}
