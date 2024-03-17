package model.time;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaxEventManager {
    private static final List<TaxObserver> observers = new ArrayList<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static synchronized List<TaxObserver> getObservers() {
        // Return a defensive copy of the observer list to ensure thread-safe iteration
        return new ArrayList<>(observers);
    }

    public static void subscribe(TaxObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }
    public static void unSubscribe(TaxObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public static void notifyTimeUpdate(int day, int month, int year) {
        // Create a snapshot of the observer list to ensure thread-safe iteration
        List<TaxObserver> snapshot;
        synchronized (observers) {
            snapshot = new ArrayList<>(observers);
        }

        // Notify all observers in parallel
        for (TaxObserver observer : snapshot) {
            executor.submit(() -> observer.taxUpdate(day, month, year));
        }
    }
}
