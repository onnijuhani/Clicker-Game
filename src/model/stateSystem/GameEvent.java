package model.stateSystem;

import model.buildings.Property;
import model.characters.Character;
import model.characters.Person;
import model.characters.ai.Aspiration;
import model.time.Time;

import java.util.Arrays;
import java.util.List;
@SuppressWarnings("CallToPrintStackTrace")
public class GameEvent {
    private Event event;
    private int executionDay;
    private int executionMonth;
    private int executionYear;


    private final List<Person> participants;

    private boolean isAborted;

    public GameEvent(Event event, Person... participants) {
        this.event = event;
        this.participants = Arrays.asList(participants);
        for (Person participant : participants) {
            participant.getPerson().addEvent(this);
        }
        this.isAborted = false; // Initialize as not aborted
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

    public String getTimeLeftString() {
        int[] timeLeft = timeLeftUntilExecution();
        return (String.format("%d days, %d months, %d years left", timeLeft[2], timeLeft[1], timeLeft[0]));
    }
    public String getTimeLeftShortString() {
        int[] timeLeft = timeLeftUntilExecution();
        return (String.format("%d d, %d m, %d y left", timeLeft[2], timeLeft[1], timeLeft[0]));
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


    @Override
    public String toString() {
        return "GameEvent{" +
                "event=" + event +
                ", executionDay=" + executionDay +
                ", executionMonth=" + executionMonth +
                ", executionYear=" + executionYear +
                ", participants=" + participants +
                '}';
    }

    public boolean isAborted() {
        return isAborted;
    }

    public void resetBattleStatesAuthorityBattle() {
        if (participants.size() < 2) {
            throw new RuntimeException("Not enough participants to reset states");
        }

        Person attacker = participants.get(participants.size() - 1);
        Person defender = participants.get(participants.size() - 2);
        Property venue = defender.getProperty();

        // Collect eligible supporters
        List<Person> eligibleSupporters = participants.subList(0, participants.size() - 2);

        // Reset states
        attacker.removeState(State.IN_BATTLE);
        defender.removeState(State.IN_BATTLE);
        venue.removeState(State.IN_BATTLE);
        eligibleSupporters.forEach(support -> support.removeState(State.IN_BATTLE));
        attacker.removeAspiration(Aspiration.ACHIEVE_HIGHER_POSITION);
    }


    public void abort(Event type) {
        try {
            if(type == Event.AuthorityBattle) {
                this.isAborted = true;
                endEvent();
            } else {
                String e = "Attempted to abort " + type + ". Only authority battles are allowed to be aborted.";
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
}
