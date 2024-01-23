package time;

import model.Settings;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Time {
    public static int year = 1;
    public static int month = 1;
    public static int day = 1;
    private ScheduledExecutorService executorService;
    private boolean isSimulationRunning = false;
    private boolean isFirstDay = true;
    private boolean isFifthDay = true;
    private final int foodConsumption = Settings.get("foodConsumption");
    private final int generate = Settings.get("generate");
    private final int maintenance = Settings.get("maintenance");
    public static final int quarterTax = Settings.get("quarterTax");
    public static final int cityTax = Settings.get("cityTax");
    public static final int provinceTax = Settings.get("provinceTax");
    public static final int nationTax = Settings.get("nationTax");



    public Time() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }
    public void incrementDay() {
        day++;
        if (day > 30) {
            day = 1;
            month++;
        }
        if (month > 12) {
            month = 1;
            year++;
        }
        if (day == quarterTax ||
                day == cityTax ||
                day == provinceTax ||
                day == nationTax ) {
            TimeEventManager.notifyTimeUpdate(day, month, year);
        }
        if (day == foodConsumption && !isFirstDay){
            FoodManager.notifyTimeUpdate();
        }
        if (day == maintenance){
            PropertyManager.notifyTimeUpdate();
        }
        if (day == generate){
            GenerateManager.notifyTimeUpdate();
        }
        isFirstDay = false; // After the first increment, it's no longer the first day
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }


    public int getDay() {
        return day;
    }

    public String getClock() {
        return String.format("year %04d,  month %01d,  day %01d",
                year, month, day);
    }

    public void startSimulation() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        isSimulationRunning = true;
        executorService.scheduleAtFixedRate(this::incrementDay, 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stopSimulation() {
        isSimulationRunning = false;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            executorService = null;
        }
    }
    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }

}
