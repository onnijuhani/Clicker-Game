package time;

import java.util.ArrayList;
import java.util.List;

public class GenerateManager {
    private static List<GenerateObserver> observers = new ArrayList<>();
    public static List<GenerateObserver> getObservers() {
            return observers;
        }
        public static void subscribe(GenerateObserver observer) {
        observers.add(observer);
    }
    public static void notifyTimeUpdate() {
        // Notify all observers
        for (GenerateObserver observer : observers) {
            observer.generateUpdate();
        }
    }

}
