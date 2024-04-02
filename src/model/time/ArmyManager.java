package model.time;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArmyManager {

    private static final List<ArmyObserver> observers = new CopyOnWriteArrayList<>();

    public static void subscribe(ArmyObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public static void unsubscribe(ArmyObserver observer) {
        observers.remove(observer);
    }

    public static void notifyTimeUpdate(int day) {
        for (ArmyObserver observer : observers) {
            observer.armyUpdate(day);
        }
    }
}
