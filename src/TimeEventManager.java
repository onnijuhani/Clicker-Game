import java.util.ArrayList;
import java.util.List;

public class TimeEventManager {
    private static List<TimeObserver> observers = new ArrayList<>();
    public static List<TimeObserver> getObservers() {
        return observers;
    }

    public static void subscribe(TimeObserver observer) {
        observers.add(observer);
    }

    public static void notifyTimeUpdate(int day, int week, int month, int year) {
        for (TimeObserver observer : observers) {
            observer.timeUpdate(day, week, month, year);
        }
    }
}

interface TimeObserver {
    void timeUpdate(int day, int week, int month, int year);

}
