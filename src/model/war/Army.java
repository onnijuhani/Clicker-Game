package model.war;

import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;

public class Army {

    private int numOfSoldiers = 1;
    private int attackPower = 1;
    private int defencePower = 1;
    private Military military;
    private final Wallet wallet;

    public Army(Military military, Wallet wallet) {
        this.military = military;
        this.wallet = wallet;
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


    public boolean hireSoldiers(int amount){
        if(amount == 0){
            return false;
        }
        if(wallet == null){
            return false;
        }
        if(!wallet.subtractResources(new TransferPackage(ArmyCost.hireSoldierFood,ArmyCost.hireSoldierAlloy, ArmyCost.hireSoldierGold))){
            return false;
        }
        numOfSoldiers = numOfSoldiers + amount;
        return true;

    }


    public TransferPackage countRunningCosts(){

        int food = numOfSoldiers * ArmyCost.runningFood;
        int alloys = attackPower + defencePower * ArmyCost.runningAlloy;
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
