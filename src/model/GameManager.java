package model;

public class GameManager {



    private static final int foodConsumptionDay = Settings.get("foodConsumptionDay");
    private static int foodConsumptionRate = Settings.get("foodConsumption");
    private static final int foodConsumptionIncreaseInterval = Settings.get("foodConsumptionIncreaseInterval");
    private static final int foodConsumptionIncreaseRate = Settings.get("foodConsumptionIncreaseRate");
    private static int currentYear = 0;

    public static int getFoodConsumptionRate() {
        return foodConsumptionRate;
    }
    public static int getFoodConsumptionDay() {
        return foodConsumptionDay;
    }


    public static void updateYearly(int year) {
        if (year > currentYear) {
            currentYear = year;
            if ((year % foodConsumptionIncreaseInterval) == 0) {
                foodConsumptionRate += Settings.get("foodConsumption") * foodConsumptionIncreaseRate;
            }
        }
    }
}
