package model.time;

import model.stateSystem.GameEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EventManager {
    private static final List<ScheduledEvent> scheduledEvents = new ArrayList<>();

    public static void scheduleEvent(Runnable eventAction, int daysUntilEvent, GameEvent gameEvent) {

        // Calculate the target day for the event
        int targetDay = Time.getDay() + daysUntilEvent;
        int targetMonth = Time.month;
        int targetYear = Time.year;

        while (targetDay > 30) {
            targetDay -= 30;
            targetMonth++;

            if (targetMonth > 12) {
                targetMonth = 1;
                targetYear++;
            }
        } // Now targetDay, targetMonth, and targetYear represent the future date when the event should occur

        gameEvent.setExecutionTime(targetDay, targetMonth, targetYear);
        scheduledEvents.add(new ScheduledEvent(eventAction, targetDay, targetMonth,targetYear, gameEvent));


    }


    public static void processScheduledEvents() {
        Iterator<ScheduledEvent> iterator = scheduledEvents.iterator();
        int currentDay = Time.getDay();
        int currentMonth = Time.month;
        int currentYear = Time.year;

        while (iterator.hasNext()) {
            ScheduledEvent event = iterator.next();
            if (event.isDue(currentDay, currentMonth, currentYear)) {
                event.action().run();
                event.gameEvent().endEvent(); // Ends the event and removes it from Character
                iterator.remove();
            } //event.gameEvent().timeLeftUntilExecution() USED TO TRACK THE TIME DURING EVENT, why is this disabled?
        }
    }
}
