package model.characters;

import model.stateSystem.Event;
import model.time.Time;

public class GameEvent {

    private Event event;
    private int executionDay;
    private int executionMonth;

    private int executionYear;
    private final Character initiator;
    private final Character target;

    public GameEvent(Event event, Character initiator, Character target) {
        this.event = event;
        this.initiator = initiator;
        this.target = target;
        startEvent(initiator, target);
    }

    public int[] timeLeftUntilExecution() {
        // get current time
        int currentDay = Time.day;
        int currentMonth = Time.month;
        int currentYear = Time.year;

        // Calculate the total days until the execution
        int daysUntilExecution = (executionYear - currentYear) * 360 + (executionMonth - currentMonth) * 30 + (executionDay - currentDay);

        // Calculate years, months, and days left
        int yearsLeft = daysUntilExecution / 360;
        int monthsLeft = (daysUntilExecution % 360) / 30;
        int daysLeft = (daysUntilExecution % 360) % 30;

        return new int[]{yearsLeft, monthsLeft, daysLeft};
    }

    public void setExecutionTime(int executionDay, int executionMonth, int executionYear) {
        this.executionDay = executionDay;
        this.executionMonth = executionMonth;
        this.executionYear = executionYear;
    }


    private void startEvent(Character initiator, Character target) {
        initiator.addEvent(this);
        target.addEvent(this);
    }

    public void endEvent(){
        initiator.getOngoingEvents().remove(this);
        target.getOngoingEvents().remove(this);
    }


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }


}
