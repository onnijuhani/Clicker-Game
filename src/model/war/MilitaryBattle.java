package model.war;

import model.Settings;
import model.characters.Person;

import java.util.Random;

public class MilitaryBattle {

    private final ArmyStats attackingArmy;
    private final ArmyStats defendingArmy;
    private final int propertyPower;
    Person attackingCommander;
    Person defendingCommander;
    private final Random random = Settings.getRandom();

    public MilitaryBattle(Military attacker, Military defender) {
        this.attackingArmy = new ArmyStats(attacker.getArmy());
        this.defendingArmy = new ArmyStats(defender.getArmy());

        this.propertyPower = defender.getPower();

        this.attackingCommander = attacker.getOwner();
        this.defendingCommander = defender.getOwner();
    }


    private void performAttack(ArmyStats attacker, ArmyStats defender) {
        int attackPower = attacker.getAttackPower();
        int defencePower = defender.getDefencePower() + propertyPower;

        int attackStrength = attackPower / attacker.getNumOfSoldiers();
        int defenceStrength = defencePower / defender.getNumOfSoldiers();


        boolean attackSucceeds = random.nextDouble() < 0.6; // 60% chance of attack success

        if(attackPower > defencePower*2){ // If attackPower is significantly higher, attack always succeeds.
            attackSucceeds = true;
        }
        if(defencePower > attackPower*4){ // If defencePower is significantly higher, attack always fails.
            attackSucceeds = false;
        }

        double effectivePower = (double) attackStrength / (attackStrength + defenceStrength);
        double rand = random.nextDouble();

        if (attackSucceeds) {
            if(effectivePower > rand){
                if (random.nextDouble() < 0.5) { // 50% chance of soldier loss
                    defender.loseSoldiers(1);
                }
            }
        }

        defender.loseDefencePower(10);
        attacker.loseAttackPower(10);

        System.out.println("Attacker soldiers: " + attacker.getNumOfSoldiers());
        System.out.println("Defender soldiers: " + defender.getNumOfSoldiers());
    }







    public ArmyStats getAttackingArmy() {
        return attackingArmy;
    }

    public ArmyStats getDefendingArmy() {
        return defendingArmy;
    }




    public class ArmyStats {
        private int numOfSoldiers;
        private int attackPower;
        private int defencePower;

        public ArmyStats(Army military) {
            this.numOfSoldiers = military.getNumOfSoldiers();
            this.attackPower = military.getAttackPower();
            this.defencePower = military.getDefencePower();
        }

        public void loseSoldiers(int amount){
            this.numOfSoldiers -= amount;
            if (this.numOfSoldiers < 0) {
                this.numOfSoldiers = 0;
            }
        }
        public void loseAttackPower(int amount){
            this.attackPower -= amount;
            if (this.attackPower < 0) {
                this.attackPower = 0;
            }
        }
        public void loseDefencePower(int amount){
            this.defencePower -= amount;
            if (this.defencePower < 0) {
                this.defencePower = 0;
            }
        }

        public void addSoldiers(int amount){
            numOfSoldiers += amount;
        }
        public void addAttackPower(int amount){
            attackPower += amount;
        }
        public void addDefencePower(int amount){
            defencePower += amount;
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
    }
}
