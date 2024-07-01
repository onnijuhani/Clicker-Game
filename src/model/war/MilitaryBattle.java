package model.war;

import model.Settings;
import model.characters.Person;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;
import model.time.WarManager;
import model.time.WarObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
@SuppressWarnings("CallToPrintStackTrace")
public class MilitaryBattle implements WarObserver {

    @Override
    public void warUpdate(int day) {
        days++;

        if(day == 6) {
            payRunningCosts();
            updatePaymentCalendars();
        }

        if(Objects.equals(currentTurn, "Attacker")){
            performAttack(attackingArmyStats, defendingArmyStats, "Defender");
            setCurrentTurn("Defender");
            return;
        }
        if(Objects.equals(currentTurn, "Defender")){
            performAttack(defendingArmyStats, attackingArmyStats, "Attacker");
            setCurrentTurn("Attacker");
        }



    }

    private void updatePaymentCalendars() {
        System.out.println("wtf "+attackingArmyStats.getWarCost());
        attackingCommander.getPaymentManager().addPayment(PaymentManager.PaymentType.EXPENSE, Payment.WAR_EFFORT, attackingArmyStats.getWarCost(), 6);
        defendingCommander.getPaymentManager().addPayment(PaymentManager.PaymentType.EXPENSE, Payment.WAR_EFFORT, defendingArmyStats.getWarCost(), 6);
    }

    private String currentTurn = "Attacker"; // attacker starts



    private final Military attackingMilitary;
    private final Military defendingMilitary;
    private final ArmyStats attackingArmyStats;
    private final ArmyStats defendingArmyStats;
    private final int propertyPower;
    Person attackingCommander;
    Person defendingCommander;
    private final Random random = Settings.getRandom();
    private int days = 0; // tracks how many days have gone by

    private final List<String> battleLog = new ArrayList<>(); // Logging system

    public MilitaryBattle(Military attacker, Military defender) {
        this.attackingArmyStats = new ArmyStats(attacker.getArmy());
        this.defendingArmyStats = new ArmyStats(defender.getArmy());
        this.attackingMilitary = attacker;
        this.defendingMilitary = defender;

        this.propertyPower = defender.getPower();

        this.attackingCommander = attacker.getOwner();
        this.defendingCommander = defender.getOwner();

        WarManager.subscribe(this);

        setStartingStates();

    }


    private void setStartingStates() {
        attackingMilitary.getArmy().setState(Army.ArmyState.ATTACKING);
        defendingMilitary.getArmy().setState(Army.ArmyState.DEFENDING);
        logEvent("Battle started. Attacker: " + attackingCommander.getName() + ", Defender: " + defendingCommander.getName());
    }

    private void performAttack(ArmyStats attacker, ArmyStats defender, String attackTurn) {
        try {
            int attackPower = attacker.getAttackPower();
            int defencePower = defender.getDefencePower();

            if(Objects.equals(attackTurn, "Attacker")){
                defencePower += propertyPower;
            }

            //calculate effective strengths
            int attackStrength = attackPower * attacker.getNumOfSoldiers() + 1000;
            int defenceStrength = defencePower * defender.getNumOfSoldiers() + 1000;

            // basic success chance is 60%
            boolean attackSucceeds = random.nextDouble() < 0.6; // 60% chance of attack success

            if (attackPower > defencePower * 2) { // If attackPower is significantly higher, attack always succeeds.
                attackSucceeds = true;
            }
            if (defencePower > attackPower * 4) { // If defencePower is significantly higher, attack always fails.
                attackSucceeds = false;
            }

            // effective power is another way to determine is attack succeeds, it's determined by the share of the strength
            double effectivePower = (double) attackStrength / (attackStrength + defenceStrength);
            double rand = random.nextDouble();

            if (attackSucceeds) {
                if (effectivePower > rand) {
                    if (random.nextDouble() < 0.8) { // 80% chance of soldier loss
                        defender.loseSoldiers(1);
                        logEvent( attackTurn + " lost a soldier.");
                    }
                }
            }

            // every attack consumes resources (alloys) based on the strength ratio
            int totalDefenceAndAttack = attackPower + defencePower;
            double attackerLossRatio = (double) attackPower / totalDefenceAndAttack;
            double defenderLossRatio = (double) defencePower / totalDefenceAndAttack;

            int attackerLoss = Math.max((int) (defenderLossRatio * days), 50);
            int defenderLoss = Math.max((int) (attackerLossRatio * days), 50);

            // Debug prints
            System.out.println("Attack Power: " + attackPower);
            System.out.println("Defence Power: " + defencePower);
            System.out.println("Total Defence and Attack: " + totalDefenceAndAttack);
            System.out.println("Attacker Loss Ratio: " + attackerLossRatio);
            System.out.println("Defender Loss Ratio: " + defenderLossRatio);
            System.out.println("Days: " + days);
            System.out.println("Attacker Loss: " + attackerLoss);
            System.out.println("Defender Loss: " + defenderLoss);

            defender.loseDefencePower(defenderLoss);
            attacker.loseAttackPower(attackerLoss);

            // battle ends when 1 army runs out of soldiers
            if (attacker.getNumOfSoldiers() == 0) {
                settleBattle("Defender");
            }
            if (defender.getNumOfSoldiers() == 0) {
                settleBattle("Attacker");
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    private void settleBattle(String winner) {
        try {
            if (Objects.equals(winner, "Attacker")) {
                defendingMilitary.getArmy().setState(Army.ArmyState.DEFEATED);
                attackingMilitary.getArmy().setState(null);

                attackingCommander.getEventTracker().addEvent(EventTracker.Message("Major", String.format("Your army is victorious against %s", defendingCommander.getName())));
                defendingCommander.getEventTracker().addEvent(EventTracker.Message("Major", String.format("Your army has lost against %s", attackingCommander.getName())));
                logEvent("Battle ended. Attacker is victorious.");
            }

            if (Objects.equals(winner, "Defender")) {
                defendingMilitary.getArmy().setState(null);
                attackingMilitary.getArmy().setState(null);

                attackingCommander.getEventTracker().addEvent(EventTracker.Message("Major", String.format("Your army has lost against %s", defendingCommander.getName())));
                defendingCommander.getEventTracker().addEvent(EventTracker.Message("Major", String.format("Your army is victorious against %s", attackingCommander.getName())));
                logEvent("Battle ended. Defender is victorious.");
            }

            attackingMilitary.getArmy().returnFromBattle(attackingArmyStats.numOfSoldiers,
                    attackingArmyStats.attackPower / ArmyCost.increaseArmyAttack,
                    attackingArmyStats.defencePower / ArmyCost.increaseArmyDefence);

            defendingMilitary.getArmy().returnFromBattle(defendingArmyStats.numOfSoldiers,
                    defendingArmyStats.attackPower / ArmyCost.increaseArmyAttack,
                    defendingArmyStats.defencePower / ArmyCost.increaseArmyDefence);
            WarManager.unsubscribe(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void logEvent(String event) {
        battleLog.add(event);
    }

    public List<String> getBattleLog() {
        return battleLog;
    }
    public void setCurrentTurn(String currentTurn) {
        this.currentTurn = currentTurn;
    }
    public ArmyStats getAttackingArmyStats() {
        return attackingArmyStats;
    }
    public ArmyStats getDefendingArmyStats() {
        return defendingArmyStats;
    }
    public int getDays() {
        return days;
    }
    private void payRunningCosts() {
        try {
            if(!attackingCommander.getWallet().subtractResources(attackingArmyStats.getWarCost())){
                attackingCommander.getEventTracker().addEvent(EventTracker.Message("Minor", "Army expenses not paid"));
                attackingCommander.loseStrike();
            }else{
                attackingCommander.getEventTracker().addEvent(EventTracker.Message("Minor", "War expenses paid: " + attackingArmyStats.getWarCost().toShortString()));
            }

            if(!defendingCommander.getWallet().subtractResources(defendingArmyStats.getWarCost())){
                defendingCommander.getEventTracker().addEvent(EventTracker.Message("Minor", "Army expenses not paid"));
                defendingCommander.loseStrike();
            }else{
                defendingCommander.getEventTracker().addEvent(EventTracker.Message("Minor", "War expenses paid: " + attackingArmyStats.getWarCost().toShortString()));
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    public Military getAttackingMilitary() {
        return attackingMilitary;
    }


    public class ArmyStats {
        private int numOfSoldiers;
        private int attackPower;
        private int defencePower;

        public ArmyStats(Army army) {
            this.numOfSoldiers = army.getNumOfSoldiers();
            this.attackPower = army.getAttackPower() * ArmyCost.increaseArmyAttack;
            this.defencePower = army.getDefencePower() * ArmyCost.increaseArmyDefence;

            army.enterIntoBattle();
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

        public void addSoldiers(Army army) {
            int amount = army.sendSoldiers();
            numOfSoldiers += amount;
            logEvent("+" + amount + "soldiers " + (this == attackingArmyStats ? "attacker" : "defender") + ".");
        }

        public void addAttackPower(Army army) {
            int amount = army.sendAttackPower();
            attackPower += amount * ArmyCost.increaseArmyAttack;
            logEvent("+" + amount + "offence " + (this == attackingArmyStats ? "attacker" : "defender") + ".");
        }

        public void addDefencePower(Army army) {
            int amount = army.sendDefencePower();
            defencePower += amount * ArmyCost.increaseArmyDefence;
            logEvent("+" + amount + "defence " + (this == attackingArmyStats ? "attacker" : "defender") + ".");
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

        public TransferPackage getWarCost(){

            int food = numOfSoldiers * ArmyCost.runningFood;
            int alloys = (attackPower / ArmyCost.increaseArmyAttack + defencePower / ArmyCost.increaseArmyDefence) * ArmyCost.runningAlloy;
            int gold = numOfSoldiers * ArmyCost.runningGold;



            return new TransferPackage(food, alloys, gold);
        }

        public int getTotalPower() {
            return numOfSoldiers * attackPower + numOfSoldiers * defencePower;
        }
    }

    public double calculateWinningChance() {
        int defenderDefencePower = defendingArmyStats.getTotalPower();
        int attackerAttackPower = attackingArmyStats.getTotalPower();
        return (double) attackerAttackPower / (defenderDefencePower + attackerAttackPower);
    }

    public static double calculateWinningChancePreWar(Military attacker, Military defender) {

        int defenderDefencePower = defender.getMilitaryStrength();

        int attackerAttackPower = attacker.getMilitaryStrength();

        double attackRatio = (double) attackerAttackPower / defenderDefencePower;
        attackRatio = Math.min(Math.max(attackRatio, 0.0), 3.0);

        return 0.5 * attackRatio / (1.0 + attackRatio);
    }



}
