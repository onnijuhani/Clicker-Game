package model.time;

import model.GameManager;
import model.Settings;
import model.characters.player.clicker.Clicker;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Time {
    public static int year = 0;
    public static int month = 0;
    public static int day = 0;
    private static ScheduledExecutorService executorService;
    private static boolean isSimulationRunning = false;
    private static boolean isManualSimulation = false;
    private static boolean isFirstDay = true;
    private static final int generate = Settings.getInt("generate");
    private static final int maintenance = Settings.getInt("maintenance");
    public static final int quarterTax = Settings.getInt("quarterTax");
    public static final int cityTax = Settings.getInt("cityTax");
    public static final int provinceTax = Settings.getInt("provinceTax");
    public static final int nationTax = Settings.getInt("nationTax");
    public static final int utilitySlots = Settings.getInt("utilitySlots");
    public static final int armyRunningCost = Settings.getInt("armyRunningCost");
    private static int milliseconds = 1000;
    public static boolean gameOver = false;
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

    public static void incrementDay() {
        day++;
        if (day > 30) {
            day = 1;
            month++;
        }
        if (month > 12) {
            month = 1;
            year++;
            GameManager.updateYearly(year);
        }
        if (day == quarterTax ||
                day == cityTax ||
                day == provinceTax ||
                day == nationTax) {
            TaxEventManager.notifyTimeUpdate(day, month, year);
        }
        if (day == maintenance) {
            PropertyManager.notifyTimeUpdate();
        }
        if (day == generate) {
            GenerateManager.notifyTimeUpdate();
        }
        if (day == utilitySlots) {
            UtilityManager.notifyTimeUpdate();
        }
        if (day == armyRunningCost && !(year == 0 && month == 0)) { // prevent unfair situations by skipping the very first time
            ArmyManager.notifyTimeUpdate(day);
        }

        /**
         * TODO OPTIMIZE THIS
         * // 26.3 only triggers during free days
         */
        if (!isFirstDay && freeDay() ) {
            NpcManager.notifyTimeUpdate(day, month, year);
        }
        isFirstDay = false; // After the first increment, it's no longer the first day

        EventManager.processScheduledEvents(); // All scheduled events require information every day


        if(Clicker.getInstance().isAutoClickerOwned()){
            Clicker.getInstance().generateResources();
        }

    }

    private static boolean freeDay(){
        return day != generate && day != maintenance && day != quarterTax && day != cityTax && day != provinceTax && day != nationTax && day != utilitySlots;
    }

    public static void incrementByClick(){
        if(Time.gameOver){
            return;
        }
        if (!isSimulationRunning) {
            incrementDay();
        }
    }

    public static void setManualSimulation(boolean manualSimulation) {
        isManualSimulation = manualSimulation;
        if (isSimulationRunning) {
            isSimulationRunning = !manualSimulation;
        }
    }

    public static int getYear() {
        return year;
    }

    public static int getMonth() {
        return month;
    }


    public static int getDay() {
        return day;
    }

    public static String getClock() {
        return String.format("Year %01d,  Month %01d,  Day %01d",
                year, month, day);
    }

    private static void speedToMilliseconds(){
        if (speed.equals(Speed.Normal)) {
            milliseconds = 1000;
        }
        if (speed.equals(Speed.Fast)) {
            milliseconds = 50;
        }
        if (speed.equals(Speed.Slow)) {
            milliseconds = 2000;
        }
    }

    public static void startSimulation() {
        if (Time.gameOver){
            System.out.println("Game Over");
            return;
        }
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadScheduledExecutor();
        }
        isSimulationRunning = true;
        executorService.scheduleAtFixedRate(Time::incrementDay, 0, milliseconds, TimeUnit.MILLISECONDS);
    }

    public static void stopSimulation() {
        isSimulationRunning = false;
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    public static void setGameOver(boolean gameOver) {
        stopSimulation();
        Time.gameOver = gameOver;
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
                rescheduleSimulation();
            }

        } else if (command.equals(Speed.Fast) && speed.equals(Speed.Slow)) {
            setSpeed(Speed.Normal);
            speedToMilliseconds();
            if (isSimulationRunning) {
                rescheduleSimulation();
            }

        } else if (command.equals(Speed.Fast) && speed.equals(Speed.Normal)) {
            setSpeed(Speed.Fast);
            speedToMilliseconds();
            if (isSimulationRunning) {
                rescheduleSimulation();
            }

        } else if (command.equals(Speed.Slow) && speed.equals(Speed.Fast)) {
            setSpeed(Speed.Normal);
            speedToMilliseconds();
            if (isSimulationRunning) {
                rescheduleSimulation();
            }

        } else if (command.equals(Speed.Slow) && speed.equals(Speed.Normal)) {
            setSpeed(Speed.Slow);
            speedToMilliseconds();
            if (isSimulationRunning) {
                rescheduleSimulation();
            }
        }
    }

    private void rescheduleSimulation() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(Time::incrementDay, 0, milliseconds, TimeUnit.MILLISECONDS);
    }

    public static boolean isIsSimulationRunning() {
        return isSimulationRunning;
    }
}
