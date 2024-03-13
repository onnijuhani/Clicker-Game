package model.time;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UtilityManager {
    // Thread-safe list that supports concurrent iterations and modifications
    private static List<UtilityObserver> observers = new CopyOnWriteArrayList<>();

    public static void subscribe(UtilityObserver observer) {
        // Prevent duplicate subscriptions
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public static void unsubscribe(UtilityObserver observer) {
        observers.remove(observer);
    }

    public static void notifyTimeUpdate() {
        for (UtilityObserver observer : observers) {
            // Consider offloading heavy work to a separate thread if applicable
            observer.utilityUpdate();
        }
    }
}