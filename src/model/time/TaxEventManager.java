package model.time;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaxEventManager {
    private static final List<TaxObserver> observers = new ArrayList<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static synchronized List<TaxObserver> getObservers() {
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
    public static boolean isSubscriped(TaxObserver observer) {
        synchronized (observers) {
            return observers.contains(observer);
        }
    }

    public static void notifyTimeUpdate(int day, int month, int year) {
        List<TaxObserver> snapshot;
        synchronized (observers) {
            snapshot = new ArrayList<>(observers);
        }

        for (TaxObserver observer : snapshot) {
            executor.submit(() -> observer.taxUpdate(day, month, year));
        }
    }
}
