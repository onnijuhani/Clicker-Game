package model.time;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GenerateManager {
    private static final List<GenerateObserver> observers = new ArrayList<>();
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static synchronized List<GenerateObserver> getObservers() {
        // Create a defensive copy of observers to ensure thread-safe iteration outside of this method
        return new ArrayList<>(observers);
    }

    public static void subscribe(GenerateObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public static void unSubscribe(GenerateObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public static void notifyTimeUpdate() {
        // Create a snapshot of observers list to avoid ConcurrentModificationException
        // and ensure thread-safe iteration
        List<GenerateObserver> snapshot;
        synchronized (observers) {
            snapshot = new ArrayList<>(observers);
        }
        // Notify all observers in parallel using executor service
        for (GenerateObserver observer : snapshot) {
            executor.submit(observer::generateUpdate);
        }
    }
}
