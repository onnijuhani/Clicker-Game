package model.time;

import model.stateSystem.GameEvent;

public record ScheduledEvent(Runnable action, int executionDay, int executionMonth, int executionYear, GameEvent gameEvent) {

    public boolean isDue(int currentDay, int currentMonth, int currentYear) {
        if (currentYear > executionYear) return true;
        if (currentYear == executionYear && currentMonth > executionMonth) return true;
        return currentYear == executionYear && currentMonth == executionMonth && currentDay >= executionDay;
    }

}

