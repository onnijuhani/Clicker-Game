package model.time;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UtilityManager {
    private static final List<UtilityObserver> observers = new CopyOnWriteArrayList<>();

    public static void subscribe(UtilityObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public static void unsubscribe(UtilityObserver observer) {
        observers.remove(observer);
    }

    public static void notifyTimeUpdate() {
        for (UtilityObserver observer : observers) {
            observer.utilityUpdate();
        }
    }
}