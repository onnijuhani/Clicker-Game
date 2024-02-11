package model.time;

import java.util.ArrayList;
import java.util.List;

public class NpcManager {
    private static List<NpcObserver> observers = new ArrayList<>();
    public static List<NpcObserver> getObservers() {
        return observers;
    }
    public static void subscribe(NpcObserver observer) {
        observers.add(observer);
    }
    public static void notifyTimeUpdate(int day, int month, int year) {
        for (NpcObserver observer : observers) {
            observer.npcUpdate(day, month, year);
        }
    }
}
