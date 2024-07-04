package model.war;

import model.Settings;
import model.characters.Person;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.payments.PaymentTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.Event;
import model.stateSystem.MessageTracker;
import model.stateSystem.GameEvent;
import model.time.ArmyManager;
import model.time.ArmyObserver;
import model.time.EventManager;

import java.util.HashMap;
import java.util.LinkedList;

@SuppressWarnings("CallToPrintStackTrace")
public class Army implements ArmyObserver, PaymentTracker {
    @Override
    public void armyUpdate(int day) {
        try {
            if(day == expenseDay) {
                payRunningCosts();
                updatePaymentCalendar(owner.getPaymentManager());
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();throw new RuntimeException(e);
        }

    }
    private int numOfSoldiers = 1;
    private int attackPower = 1;
    private int defencePower = 1;
    private Military military;
    private Wallet wallet;
    private Person owner;
    private boolean recruitingInProcess = false;
    private boolean trainingInProcess = false;
    private static final int expenseDay = Settings.getInt("armyExpense");
    private ArmyState state;


    public LinkedList<String> getBattleHistory() {
        return battleHistory;
    }

    public HashMap<String, Integer> getWinRatio() {
        return winRatio;
    }

    private final HashMap<String, Integer> winRatio = new HashMap<>();
    private final LinkedList<String> battleHistory = new LinkedList<>();
    public enum ArmyState{
        DEFENDING, ATTACKING, DEFEATED
    }
    public Army(Military military, Person owner) {
        this.military = military;
        this.owner = owner;
        this.wallet = owner.getWallet();
        ArmyManager.subscribe(this);
        updatePaymentCalendar(owner.getPaymentManager());
    }

    @Override
    public void updatePaymentCalendar(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.EXPENSE, Payment.ARMY_EXPENSE, getRunningCost(), expenseDay);
    }


    private void payRunningCosts() {
        try {
            if(!wallet.subtractResources(getRunningCost())){
                owner.getEventTracker().addEvent(MessageTracker.Message("Minor", "Army expenses not paid"));
                owner.loseStrike();
            }else{
                owner.getEventTracker().addEvent(MessageTracker.Message("Minor", "Army expenses paid: " + getRunningCost().toShortString()));
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    public void clearResources() {
        this.military = null;
        this.owner = null;
        this.wallet = null;
        ArmyManager.unsubscribe(this);
    }


    public boolean increaseDefencePower(){
        try {
            if (trainingInProcess) {
                return false;
            }
            if (wallet == null) {
                return false;
            }
            if (!wallet.subtractResources(new TransferPackage(0, ArmyCost.increaseArmyDefence, 0))) {
                return false;
            }
            trainingInProcess = true;

            GameEvent gameEvent = new GameEvent(Event.ArmyTraining, owner);
            owner.getEventTracker().addEvent(MessageTracker.Message("Minor", "Army Defence Training Started"));

            int daysUntilEvent = 15;

            if(state == ArmyState.DEFENDING || state == ArmyState.ATTACKING){
                daysUntilEvent /= 3;
            }

            EventManager.scheduleEvent(this::finishDefenceIncrease, daysUntilEvent, gameEvent);

            return true;
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    private void finishDefenceIncrease() {
        defencePower++;
        trainingInProcess = false;
        updatePaymentCalendar(owner.getPaymentManager());
        owner.getEventTracker().addEvent(MessageTracker.Message("Minor", "Army Defence Training Finished"));
    }

    public boolean increaseAttackPower() {
        try {
            if (trainingInProcess) {
                return false;
            }

            if (wallet == null) {
                return false;
            }

            if (!wallet.subtractResources(new TransferPackage(0, ArmyCost.increaseArmyAttack, 0))) {
                return false;
            }
            trainingInProcess = true;

            GameEvent gameEvent = new GameEvent(Event.ArmyTraining, owner);
            owner.getEventTracker().addEvent(MessageTracker.Message("Minor", "Army Offence Training Started"));

            int daysUntilEvent = 15;

            if(state == ArmyState.DEFENDING || state == ArmyState.ATTACKING){
                daysUntilEvent /= 3;
            }

            EventManager.scheduleEvent(this::finishAttackIncrease, daysUntilEvent, gameEvent);

            return true;

        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    private void finishAttackIncrease() {
        attackPower++;
        trainingInProcess = false;
        updatePaymentCalendar(owner.getPaymentManager());
        owner.getEventTracker().addEvent(MessageTracker.Message("Minor", "Army Offence Training Finished"));
    }


    public boolean recruitSoldier(int amount){
        try {
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
            owner.getEventTracker().addEvent(MessageTracker.Message("Minor", "Recruiting campaign started"));

            int daysUntilEvent = 30 + amount*3;

            if(state == ArmyState.DEFENDING || state == ArmyState.ATTACKING){
                daysUntilEvent /= 3;
            }

            EventManager.scheduleEvent(() -> finishSoldierRecruit(amount), daysUntilEvent, gameEvent);

            return true;
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }

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


    public int getTotalAttackPower(){
        return Math.max(numOfSoldiers * attackPower, attackPower);
    }

    public int getTotalDefencePower(){
        return Math.max(numOfSoldiers * defencePower, defencePower);
    }

    public int getTotalStrength(){
        return getTotalAttackPower() + getTotalDefencePower();
    }


    public int sendAttackPower(){
        int amount = attackPower;
        attackPower = 0;
        return amount;
    }

    public int sendDefencePower(){
        int amount = defencePower;
        defencePower = 0;
        return amount;
    }

    public int sendSoldiers(){
        int amount = numOfSoldiers;
        numOfSoldiers = 0;
        return amount;
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

    public ArmyState getState() {
        return state;
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

    public void setState(ArmyState state) {
        this.state = state;
    }

    public void enterIntoBattle(){
        numOfSoldiers = 0;
        attackPower = 0;
        defencePower = 0;
    }

    public void returnFromBattle(int numOfSoldiers, int attackPower, int defencePower, String battleInfo){
        this.numOfSoldiers += numOfSoldiers;
        this.attackPower += attackPower;
        this.defencePower += defencePower;
        addBattle(battleInfo);
    }

    public void addBattle(String battleInfo) {
        if(battleHistory.size() > 5){
            battleHistory.removeFirst();
        }

        if (battleInfo.contains("Won")) {
            winRatio.put("Wins", winRatio.getOrDefault("Wins", 0) + 1);
        } else {
            winRatio.put("Lost", winRatio.getOrDefault("Lost", 0) + 1);
        }

        this.battleHistory.add(battleInfo);
    }

}
