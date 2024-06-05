package model.stateSystem;

import model.characters.Character;
import model.characters.Person;
import model.time.Time;

import java.util.Arrays;
import java.util.List;

public class GameEvent {
    private Event event;
    private int executionDay;
    private int executionMonth;
    private int executionYear;


    private final List<Person> participants;

    public GameEvent(Event event, Person... participants) {
        this.event = event;
        this.participants = Arrays.asList(participants);
        for (Person participant : participants) {
            participant.getPerson().addEvent(this);
        }
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
        initiator.getPerson().addEvent(this);
        target.getPerson().addEvent(this);
    }

    public void endEvent() {
        for (Person participant : participants) {
            participant.getPerson().getOngoingEvents().remove(this);
        }
    }


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<Person> getParticipants() {
        return participants;
    }


}
