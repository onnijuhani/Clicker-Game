package model.war;

import model.Settings;
import model.characters.Person;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.payments.PaymentTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.Event;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.time.ArmyManager;
import model.time.ArmyObserver;
import model.time.EventManager;

public class Army implements ArmyObserver, PaymentTracker {

    @Override
    public void armyUpdate(int day) {
        if(day == expenseDay) {
            payRunningCosts();
            updatePaymentCalendar(owner.getPaymentManager());
        }


    }

    @Override
    public void updatePaymentCalendar(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.EXPENSE, Payment.ARMY_EXPENSE, getRunningCost(), expenseDay);
    }


    private void payRunningCosts() {
        if(!wallet.subtractResources(getRunningCost())){
            owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Army expenses not paid"));
            owner.getStrikesTracker().loseStrike();
        }else{
            owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Army expenses paid: " + getRunningCost()));
        }
    }

    private int numOfSoldiers = 1;
    private int attackPower = 1;

    private final int expenseDay = Settings.getInt("armyExpense");
    private int defencePower = 1;
    private Military military;
    private final Wallet wallet;
    private final Person owner;
    private boolean recruitingInProcess = false;
    private boolean trainingInProcess = false;

    public Army(Military military, Person owner) {
        this.military = military;
        this.owner = owner;
        this.wallet = owner.getWallet();
        ArmyManager.subscribe(this);
        updatePaymentCalendar(owner.getPaymentManager());
    }


    public boolean increaseDefencePower(){
        if(trainingInProcess){
            return false;
        }
        if(wallet == null){
            return false;
        }
        if(!wallet.subtractResources(new TransferPackage(0, ArmyCost.increaseArmyDefence,0))){
            return false;
        }
        trainingInProcess = true;

        GameEvent gameEvent = new GameEvent(Event.ArmyTraining, owner);
        owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Army Defence Training Started"));

        int daysUntilEvent = 30;

        EventManager.scheduleEvent(this::finishDefenceIncrease, daysUntilEvent, gameEvent);


        return true;
    }

    private void finishDefenceIncrease() {
        defencePower++;
        trainingInProcess = false;
        updatePaymentCalendar(owner.getPaymentManager());
        owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Army Defence Training Finished"));
    }
    public boolean increaseAttackPower(){
        if(trainingInProcess){
            return false;
        }
        if(wallet == null){
            return false;
        }
        if(!wallet.subtractResources(new TransferPackage(0,ArmyCost.increaseArmyAttack,0))){
            return false;
        }
        trainingInProcess = true;

        GameEvent gameEvent = new GameEvent(Event.ArmyTraining, owner);
        owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Army Offence Training Started"));

        int daysUntilEvent = 30;

        EventManager.scheduleEvent(this::finishAttackIncrease, daysUntilEvent, gameEvent);

        return true;
    }

    private void finishAttackIncrease() {
        attackPower++;
        trainingInProcess = false;
        updatePaymentCalendar(owner.getPaymentManager());
        owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Army Offence Training Finished"));
    }


    public boolean recruitSoldier(int amount){
        if(recruitingInProcess){
            return false;
        }
        if(wallet == null){
            return false;
        }

        TransferPackage cost = ArmyCost.getRecruitingCost().multiply(amount);

        if(!wallet.subtractResources(cost)){
            return false;
        }

        recruitingInProcess = true;

        GameEvent gameEvent = new GameEvent(Event.RecruitSoldier, owner);
        owner.getEventTracker().addEvent(EventTracker.Message("Minor", "Recruiting campaign started"));

        int daysUntilEvent = 30 * amount;

        EventManager.scheduleEvent(() -> finishSoldierRecruit(amount), daysUntilEvent, gameEvent);

        return true;

    }



    private void finishSoldierRecruit(int amount) {
        numOfSoldiers += amount;
        recruitingInProcess = false;
        updatePaymentCalendar(owner.getPaymentManager());
    }


    public TransferPackage getRunningCost(){

        int food = numOfSoldiers * ArmyCost.runningFood;
        int alloys = (attackPower + defencePower) * ArmyCost.runningAlloy;
        int gold = numOfSoldiers * ArmyCost.runningGold;

        return new TransferPackage(food, alloys, gold);
    }


    public int totalAttackPower(){
        return numOfSoldiers * attackPower;
    }

    public int totalDefencePower(){
        return numOfSoldiers * defencePower;
    }


    public Military getMilitaryBuilding() {
        return military;
    }

    public void setMilitaryBuilding(Military military) {
        this.military = military;
    }

    public int getNumOfSoldiers() {
        return numOfSoldiers;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefencePower() {
        return defencePower;
    }

    public Military getMilitary() {
        return military;
    }



}
