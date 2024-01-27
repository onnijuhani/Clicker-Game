package model.time;

import java.util.ArrayList;
import java.util.List;

public class UtilityManager {

        private static List<UtilityObserver> observers = new ArrayList<>();

        public static List<UtilityObserver> getObservers() {
            return observers;
        }

        public static void subscribe(UtilityObserver observer) {
            observers.add(observer);
        }

        public static void notifyTimeUpdate() {
            for (UtilityObserver observer : observers) {
                observer.utilityUpdate();
            }
        }

}
