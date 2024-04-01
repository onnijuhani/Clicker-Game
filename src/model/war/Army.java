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
        if(!wallet.subtractResources(new TransferPackage(0,5_000,0))){
            return false;
        }
        defencePower++;
        return true;
    }
    public boolean increaseAttackPower(){
        if(wallet == null){
            return false;
        }
        if(!wallet.subtractResources(new TransferPackage(0,5_000,0))){
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
        if(!wallet.subtractResources(new TransferPackage(amount*10_000,amount*5_000, amount*500))){
            return false;
        }
        numOfSoldiers = numOfSoldiers + amount;
        return true;

    }


    public TransferPackage countTotalCost(){

        int food = numOfSoldiers * 5000;
        int alloys = attackPower + defencePower * 1000;
        int gold = numOfSoldiers * 100;

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
