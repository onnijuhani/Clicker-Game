package model.time;

import java.util.ArrayList;
import java.util.List;

public class TaxEventManager {
    private static List<TaxObserver> observers = new ArrayList<>();

    public static List<TaxObserver> getObservers() {
        return observers;
    }

    public static void subscribe(TaxObserver observer) {
        observers.add(observer);
    }

    public static void notifyTimeUpdate(int day, int month, int year) {
        // Notify all observers
        for (TaxObserver observer : observers) {
            observer.taxUpdate(day, month, year);
        }
    }
}