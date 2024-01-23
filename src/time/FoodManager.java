package time;

import java.util.ArrayList;
import java.util.List;

public class FoodManager {
    private static List<FoodObserver> observers = new ArrayList<>();
    public static List<FoodObserver> getObservers() {
        return observers;
    }
    public static void subscribe(FoodObserver observer) {
        observers.add(observer);
    }
    public static void notifyTimeUpdate() {
        for (FoodObserver observer : observers) {
            observer.foodUpdate();
        }
    }
}
