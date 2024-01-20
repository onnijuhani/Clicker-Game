package model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Time {
    public static int year;
    public static int month;
    public static int week;
    public static int day;
    private ScheduledExecutorService executorService;
    private boolean isSimulationRunning = false;

    public Time() {
        executorService = Executors.newSingleThreadScheduledExecutor();
    }
    public void incrementDay() {
        day++;
        if (day > 7) {
            day = 1;
            week++;
        }
        if (week > 4) {
            week = 1;
            month++;
        }
        if (month > 12) {
            month = 1;
            year++;
        }

        TimeEventManager.notifyTimeUpdate(day, week, month, year);
    }
    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getWeek() {
        return week;
    }

    public int getDay() {
        return day;
    }

    public String getClock() {
        return String.format("%04d years, %02d months, %02d weeks, %02d days",
                year, month, week, day);
    }

    public void startSimulation() {
        if (executorService == null || executorService.isShutdown()) {
            // Recreate the executor service if it's shut down
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        isSimulationRunning = true;
        executorService.scheduleAtFixedRate(this::incrementDay, 0, 1, TimeUnit.SECONDS);
    }

    public void stopSimulation() {
        isSimulationRunning = false;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown(); // Optionally consider using shutdownNow()
            executorService = null; // Set to null so it can be recreated later
        }
    }
    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }

}
