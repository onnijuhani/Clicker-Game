package model.time;

import model.stateSystem.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventManager {
    private static final List<ScheduledEvent> scheduledEvents = new CopyOnWriteArrayList<>();

    private static List<ScheduledEvent> getScheduledEvents(){
        return scheduledEvents;
    }

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
        }

        gameEvent.setExecutionTime(targetDay, targetMonth, targetYear);
        scheduledEvents.add(new ScheduledEvent(eventAction, targetDay, targetMonth, targetYear, gameEvent));
    }

    public static void processScheduledEvents() {
        List<ScheduledEvent> eventsToExecute = new ArrayList<>();
        int currentDay = Time.getDay();
        int currentMonth = Time.month;
        int currentYear = Time.year;

        for (ScheduledEvent event : scheduledEvents) {
            if (event.isDue(currentDay, currentMonth, currentYear)) {
                eventsToExecute.add(event);
                scheduledEvents.remove(event);
            }
        }

        for (ScheduledEvent event : eventsToExecute) {
            if (!event.gameEvent().isAborted()) {
                event.action().run();
                event.gameEvent().endEvent();
            } else {
                event.gameEvent().resetBattleStatesAuthorityBattle();
            }
        }
    }
}
