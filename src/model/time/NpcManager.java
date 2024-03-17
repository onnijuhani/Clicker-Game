package model.time;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NpcManager {
    private static final List<NpcObserver> observers = new ArrayList<>();

    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    public static List<NpcObserver> getObservers() {
        return observers;
    }

    public static void subscribe(NpcObserver observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }
    public static void unSubscribe(NpcObserver observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public static void notifyTimeUpdate(int day, int month, int year) {
        // Create a copy of the observer list to avoid ConcurrentModificationException
        // if subscribe/unsubscribe operations happen during notification.
        List<NpcObserver> snapshot;
        synchronized (observers) {
            snapshot = new ArrayList<>(observers);
        }

        // Notify all observers in parallel
        for (NpcObserver observer : snapshot) {
            executor.submit(() -> observer.npcUpdate(day, month, year));
        }
    }
}

