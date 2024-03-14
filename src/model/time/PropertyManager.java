
package model.time;

import java.util.ArrayList;
import java.util.List;

public class PropertyManager {
    private static List<PropertyObserver> observers = new ArrayList<>();
    public static List<PropertyObserver> getObservers() {
        return observers;
    }

    public static void subscribe(PropertyObserver observer) {
        observers.add(observer);
    }
    public static void unsubscribe(PropertyObserver observer) {
        observers.remove(observer);
    }
    public static void notifyTimeUpdate() {
        // Notify all observers
        for (PropertyObserver observer : observers) {
            observer.propertyUpdate();
        }
    }
}
