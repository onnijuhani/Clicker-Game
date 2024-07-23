package model.buildings;

import model.Settings;
import model.characters.Person;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.payments.PaymentTracker;
import model.resourceManagement.TransferPackage;
import model.stateSystem.Event;
import model.stateSystem.MessageTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.State;
import model.time.ArmyManager;
import model.time.ArmyObserver;
import model.time.EventManager;
import model.worldCreation.Details;

import java.util.LinkedList;

import static model.Settings.timeLeftFormat;

public class GrandFoundry implements ArmyObserver, PaymentTracker, Details {

    public GrandFoundry(Person owner) {
        this.owner = owner;
        this.controller = owner;
        this.foundriesUnderControl = new LinkedList<>();
        ArmyManager.subscribe(this);
    }



    @Override
    public void armyUpdate(int day) {
        try {
            if(day == expenseDay){
                generatePayment();
                updatePaymentManager(controller.getPaymentManager());
            }
            increaseProduction(); // production increases over time automatically
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }



    private final LinkedList<GrandFoundry> foundriesUnderControl;

    private static final int expenseDay = Settings.getInt("armyExpense");
    private Person controller; // controller gets the monthly payment

    public Person getOwner() {
        return owner;
    }

    private final Person owner; // owner owns the building forever
    private int totalProductionValue = 1000; // increased automatically, gets distributed between resource types
    private int counter = 0; // increases production

    private void increaseProduction(){
        if(counter == 6){
            totalProductionValue += 1000;
            counter = 0;
        }
        counter++;
    }

    public TransferPackage getFullProduction() {
        TransferPackage amount = new TransferPackage(0,0,0);

        for(GrandFoundry foundry : foundriesUnderControl){
            amount = amount.add(foundry.getPaymentAmount());
        }

        if(owner == controller){
            amount = amount.add(getPaymentAmount());
        }
        return amount;
    }
    @Override
    public void updatePaymentManager(PaymentManager calendar) {
        TransferPackage amount = getFullProduction();
        if(amount.isEmpty()){
            calendar.removePayment(PaymentManager.PaymentType.INCOME, Payment.GRAND_FOUNDRY_INCOME);
        }else {
            calendar.addPayment(PaymentManager.PaymentType.INCOME, Payment.GRAND_FOUNDRY_INCOME, amount, expenseDay);
        }
    }
    private void generatePayment(){

        TransferPackage amount = getFullProduction();
        if(amount.isEmpty()) return;

        owner.getWallet().addResources(amount);
        String message = String.format("Grand Foundry generated %s", amount.toShortString());
        owner.getMessageTracker().addMessage(MessageTracker.Message("Utility", message));
    }

    private TransferPackage getPaymentAmount() {
        int food = (int) (totalProductionValue * 0.5);
        int alloys = (int) (totalProductionValue * 0.4);
        int gold = (int) (totalProductionValue * 0.1);

        return new TransferPackage(food, alloys, gold);
    }



    public Person getController() {
        return controller;
    }

    public void setUnderOccupation(Person controller, int days) {
        this.controller = controller;
        controller.getGrandFoundry().addFoundriesUnderControl(this);

        this.owner.addState(State.GRAND_FOUNDRY_UNDER_OCCUPATION);
        controller.addState(State.GRAND_FOUNDRY_OCCUPIED);
        GameEvent gameEvent = new GameEvent(Event.GRAND_FOUNDRY_UNDER_OCCUPATION, owner, controller);
        EventManager.scheduleEvent(this::returnControl, days, gameEvent);
    }

    private void returnControl() {
        this.owner.removeState(State.GRAND_FOUNDRY_UNDER_OCCUPATION);
        this.owner.getMessageTracker().addMessage(MessageTracker.Message("Minor","Control of Grand Foundry Gained back."));
        this.controller.removeState(State.GRAND_FOUNDRY_OCCUPIED);
        this.controller.getMessageTracker().addMessage(MessageTracker.Message("Minor","Control of " + owner + "'s Foundry forfeited."));

        this.controller.getGrandFoundry().removeFoundriesUnderControl(this);

        this.controller = owner;
    }


    public void addFoundriesUnderControl(GrandFoundry foundry) {
        this.foundriesUnderControl.add(foundry);
    }
    private void removeFoundriesUnderControl(GrandFoundry grandFoundry) {
        foundriesUnderControl.remove(grandFoundry);
    }


    @Override
    public String getDetails() {
        StringBuilder details = new StringBuilder();
        if(owner != controller){
            int[] i = owner.getAnyOnGoingEvent(Event.GRAND_FOUNDRY_UNDER_OCCUPATION).getTimeLeftUntilExecution();
            details.append("Controlled by ").append(controller).append(" until\n").append(timeLeftFormat(i)).append("\n");
        }

        if(!foundriesUnderControl.isEmpty()){
            if(foundriesUnderControl.size() == 1){
                GameEvent ongoingEvent = foundriesUnderControl.getFirst().getOwner().getAnyOnGoingEvent(Event.GRAND_FOUNDRY_UNDER_OCCUPATION);
                int[] i = (ongoingEvent != null) ? ongoingEvent.getTimeLeftUntilExecution() : null;
                assert i != null : "on going events are null, should be GRAND_FOUNDRY_UNDER_OCCUPATION";
                details.append("1 enemy foundry under control for\n").append(timeLeftFormat(i));
            }else{
                int amount = foundriesUnderControl.size();
                int days = 0;
                for(GrandFoundry foundry : foundriesUnderControl){
                    GameEvent ongoingEvent = foundry.getOwner().getAnyOnGoingEvent(Event.GRAND_FOUNDRY_UNDER_OCCUPATION);
                    int i = (ongoingEvent != null) ? ongoingEvent.getDaysLeftUntilExecution() : -1;
                    days += i;
                }
                int avgDays = days / amount;
                details.append(amount).append(" enemy foundries under control for average of ").append(avgDays).append( "days.");

            }

        }

        return details.toString();
    }
}
