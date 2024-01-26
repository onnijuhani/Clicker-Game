package model.time;

import model.Settings;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Time {
    public static int year = 0;
    public static int month = 0;
    public static int day = 0;
    private ScheduledExecutorService executorService;
    private boolean isSimulationRunning = false;

    private boolean isManualSimulation = false;
    private boolean isFirstDay = true;
    private boolean isFifthDay = true;
    private final int foodConsumption = Settings.get("foodConsumption");
    private final int generate = Settings.get("generate");
    private final int maintenance = Settings.get("maintenance");
    public static final int quarterTax = Settings.get("quarterTax");
    public static final int cityTax = Settings.get("cityTax");
    public static final int provinceTax = Settings.get("provinceTax");
    public static final int nationTax = Settings.get("nationTax");

    private int milliseconds = 1000;


    public static Speed getSpeed() {
        return speed;
    }

    public static void setSpeed(Speed speed) {
        Time.speed = speed;
    }

    public static Speed speed = Speed.Normal;


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
                day == nationTax) {
            TimeEventManager.notifyTimeUpdate(day, month, year);
        }
        if (day == foodConsumption && !isFirstDay) {
            FoodManager.notifyTimeUpdate();
        }
        if (day == maintenance) {
            PropertyManager.notifyTimeUpdate();
        }
        if (day == generate) {
            GenerateManager.notifyTimeUpdate();
        }
        isFirstDay = false; // After the first increment, it's no longer the first day
    }

    public void incrementByClick(){
        if (!isSimulationRunning) {
            incrementDay();
        }
    }

    public void setManualSimulation(boolean manualSimulation) {
        isManualSimulation = manualSimulation;
        if (isSimulationRunning) {
            isSimulationRunning = !manualSimulation;
        }
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }


    public static int getDay() {
        return day;
    }

    public static String getClock() {
        return String.format("Year %01d,  Month %01d,  Day %01d",
                year, month, day);
    }

    private void speedToMilliseconds(){
        if (speed.equals(Speed.Normal)) {
            milliseconds = 1000;
        }
        if (speed.equals(Speed.Fast)) {
            milliseconds = 25;
        }
        if (speed.equals(Speed.Slow)) {
            milliseconds = 2000;
        }
    }

    public void startSimulation() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        isSimulationRunning = true;
        executorService.scheduleAtFixedRate(this::incrementDay, 0, milliseconds, TimeUnit.MILLISECONDS);

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

    public boolean isManualSimulation() {
        return isManualSimulation;
    }

    public void updateSpeed(Speed command) {
        if (command.equals(speed)) {
            speedToMilliseconds();
            if (isSimulationRunning) {
                rescheduleSimulation(); // Reschedule the task with the new speed
            }

        } else if (command.equals(Speed.Fast) && speed.equals(Speed.Slow)) {
            setSpeed(Speed.Normal);
            speedToMilliseconds();
            if (isSimulationRunning) {
                rescheduleSimulation(); // Reschedule the task with the new speed
            }

        } else if (command.equals(Speed.Fast) && speed.equals(Speed.Normal)) {
            setSpeed(Speed.Fast);
            speedToMilliseconds();
            if (isSimulationRunning) {
                rescheduleSimulation(); // Reschedule the task with the new speed
            }

        } else if (command.equals(Speed.Slow) && speed.equals(Speed.Fast)) {
            setSpeed(Speed.Normal);
            speedToMilliseconds();
            if (isSimulationRunning) {
                rescheduleSimulation(); // Reschedule the task with the new speed
            }

        } else if (command.equals(Speed.Slow) && speed.equals(Speed.Normal)) {
            setSpeed(Speed.Slow);
            speedToMilliseconds();
            if (isSimulationRunning) {
                rescheduleSimulation(); // Reschedule the task with the new speed
            }
        }
    }

    private void rescheduleSimulation() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow(); // Stop the current task
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::incrementDay, 0, milliseconds, TimeUnit.MILLISECONDS);
    }
}
