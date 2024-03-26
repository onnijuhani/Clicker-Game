package model.time;

import java.util.ArrayList;
import java.util.List;
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

    public static boolean isSubscriped(NpcObserver observer) {
        synchronized (observers) {
            return observers.contains(observer);
        }
    }

    public static void notifyTimeUpdate(int day, int month, int year) {
        // Notify all observers
        for (NpcObserver observer : observers) {
            observer.npcUpdate(day, month, year);
        }
    }

//    public static void notifyTimeUpdate(int day, int month, int year) {
//        List<NpcObserver> snapshot;
//        synchronized (observers) {
//            snapshot = new ArrayList<>(observers);
//        }
//
//        // CountDownLatch to wait for all tasks to complete
//        CountDownLatch latch = new CountDownLatch(snapshot.size());
//
//        for (NpcObserver observer : snapshot) {
//            executor.submit(() -> {
//                try {
//                    observer.npcUpdate(day, month, year);
//                } catch (Exception e) {
//                    // Log or handle the exception as appropriate
//                    e.printStackTrace();
//                } finally {
//                    latch.countDown(); // Ensure latch is decremented even if an exception occurs
//                }
//            });
//        }
//
//        try {
//            latch.await(); // Wait for all observers to be notified
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt(); // Restore the interrupted status
//
//        }
//    }
}

