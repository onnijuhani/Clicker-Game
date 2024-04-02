package model.war;

import model.characters.Person;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.EventTracker;
import model.time.ArmyManager;
import model.time.ArmyObserver;

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
    public Army(Military military, Person owner) {
        this.military = military;
        this.owner = owner;
        this.wallet = owner.getWallet();
        ArmyManager.subscribe(this);
    }


    public boolean increaseDefencePower(){
        if(wallet == null){
            return false;
        }
        if(!wallet.subtractResources(new TransferPackage(0, ArmyCost.increaseArmyDefence,0))){
            return false;
        }
        defencePower++;
        return true;
    }
    public boolean increaseAttackPower(){
        if(wallet == null){
            return false;
        }
        if(!wallet.subtractResources(new TransferPackage(0,ArmyCost.increaseArmyAttack,0))){
            return false;
        }
        attackPower++;
        return true;
    }


    public boolean recruitSoldier(){
        if(wallet == null){
            return false;
        }
        TransferPackage cost = ArmyCost.getRecruitingCost();
        if(!wallet.subtractResources(cost)){
            return false;
        }
        numOfSoldiers++;
        return true;

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
