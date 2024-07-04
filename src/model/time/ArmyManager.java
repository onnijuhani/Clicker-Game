package model.time;

import model.buildings.GrandFoundry;
import model.war.Army;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArmyManager {

    private static final List<ArmyObserver> observers = new CopyOnWriteArrayList<>();
    private static boolean needsSorting = false;

    public static void subscribe(ArmyObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            needsSorting = true;
        }
    }

    public static void unsubscribe(ArmyObserver observer) {
        observers.remove(observer);
    }

    public static void notifyTimeUpdate(int day) {
        if (needsSorting) {
            sortObservers();
            needsSorting = false;
        }

        // Notify all observers
        for (ArmyObserver observer : observers) {
            observer.armyUpdate(day);
            System.out.println(observer);
        }
    }

    private static void sortObservers() {
        observers.sort((o1, o2) -> {
            if (o1 instanceof GrandFoundry && o2 instanceof Army) {
                return -1;
            } else if (o1 instanceof Army && o2 instanceof GrandFoundry) {
                return 1;
            }
            return 0;
        });
    }
}
