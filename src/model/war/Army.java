package model.war;

import model.characters.Person;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.Event;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.time.ArmyManager;
import model.time.ArmyObserver;
import model.time.EventManager;

public class Army implements ArmyObserver {

    @Override
    public void armyUpdate(int day) {
        if(day == 20) {
            payRunningCosts();
        }
    }

    private void payRunningCosts() {
        if(!wallet.subtractResources(countRunningCosts())){
            owner.getEventTracker().addEvent(EventTracker.Message("Major", "Army expenses not paid"));
            owner.getStrikesTracker().loseStrike();
        }else{
            owner.getEventTracker().addEvent(EventTracker.Message("Major", "Army expenses paid:\n\t\t\t\t" + countRunningCosts()));
        }
    }

    private int numOfSoldiers = 1;
    private int attackPower = 1;
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

        int daysUntilEvent = 90;

        EventManager.scheduleEvent(this::finishDefenceIncrease, daysUntilEvent, gameEvent);


        return true;
    }

    private void finishDefenceIncrease() {
        defencePower++;
        trainingInProcess = false;
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

        int daysUntilEvent = 60;

        EventManager.scheduleEvent(this::finishAttackIncrease, daysUntilEvent, gameEvent);

        return true;
    }

    private void finishAttackIncrease() {
        attackPower++;
        trainingInProcess = false;
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

        EventManager.scheduleEvent(() -> increaseSoldierAmount(amount), daysUntilEvent, gameEvent);

        return true;

    }



    private void increaseSoldierAmount(int amount) {
        numOfSoldiers += amount;
        recruitingInProcess = false;
    }


    public TransferPackage countRunningCosts(){

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


}
