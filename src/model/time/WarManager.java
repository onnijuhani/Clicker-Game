package model.time;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WarManager {
    private static final List<WarObserver> observers = new CopyOnWriteArrayList<>();

    public static void subscribe(WarObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public static void unsubscribe(WarObserver observer) {
        observers.remove(observer);
    }

    public static void notifyTimeUpdate(int day) {
        for (WarObserver observer : observers) {
            observer.warUpdate(day);
        }
    }
}
