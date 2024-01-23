package time;

import java.util.ArrayList;
import java.util.List;

public class TimeEventManager {
    private static List<TimeObserver> observers = new ArrayList<>();

    public static List<TimeObserver> getObservers() {
        return observers;
    }

    public static void subscribe(TimeObserver observer) {
        observers.add(observer);
    }

    public static void notifyTimeUpdate(int day, int month, int year) {
        // Notify all observers
        for (TimeObserver observer : observers) {
            observer.timeUpdate(day, month, year);
        }
    }
}