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
            performAttack(attackingArmyStats, defendingArmyStats, "Attacker");
            setCurrentTurn("Defender");
            return;
        }
        if(Objects.equals(currentTurn, "Defender")){
            performAttack(defendingArmyStats, attackingArmyStats, "Defender");
            setCurrentTurn("Attacker");
        }



    }

    private void updatePaymentCalendars() {
        System.out.println("wtf "+attackingArmyStats.getWarCost());
        attackingCommander.getPaymentManager().addPayment(PaymentManager.PaymentType.EXPENSE, Payment.MILITARY_BATTLE_EXPENSE, attackingArmyStats.getWarCost(), 6);
        defendingCommander.getPaymentManager().addPayment(PaymentManager.PaymentType.EXPENSE, Payment.MILITARY_BATTLE_EXPENSE, defendingArmyStats.getWarCost(), 6);
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


    private boolean isOnGoing = true;

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
        logEvent("Battle started.\nAttacker: " + attackingCommander.getName() + "\nDefender: " + defendingCommander.getName());
    }
    private void performAttack(ArmyStats attacker, ArmyStats defender, String attackTurn) {
        try {

            // battle ends when 1 army runs out of soldiers
            if (attackingArmyStats.getNumOfSoldiers() == 1) {
                settleBattle("Defender");
                return;
            }
            if (defendingArmyStats.getNumOfSoldiers() == 1) {
                settleBattle("Attacker");
                return;
            }


            int attackPower = attacker.getAttackPower();
            int defencePower = defender.getDefencePower();

            if(Objects.equals(attackTurn, "Attacker")){
                defencePower += propertyPower;
            }

            //calculate effective strengths
            int attackStrength = attackPower * attacker.getNumOfSoldiers() + 5000;
            int defenceStrength = defencePower * defender.getNumOfSoldiers() + 5000;

            // basic success chance is 60%
            boolean attackSucceeds = random.nextDouble() < 0.5; // 50% chance of attack success

            if (attackPower > (defencePower * 100)) { // If attackPower is significantly higher, attack always succeeds.
                attackSucceeds = true;
            }
            if (defencePower > (attackPower * 120)) { // If defencePower is significantly higher, attack always fails.
                attackSucceeds = false;
            }

            // effective power is another way to determine is attack succeeds, it's determined by the share of the strength
            double effectivePower = Math.max((double) attackStrength / (attackStrength + defenceStrength), 0.1); // 10% is the minimum
            double rand = random.nextDouble();

            if (attackSucceeds) {
                if (effectivePower > rand) {
                    if (random.nextDouble() < Math.min(effectivePower, 0.7)) { // effective power also determines if soldier will die, but 70% chance is the best this can go.
                        defender.loseSoldiers(1);
                        if(attacker == attackingArmyStats){
                            logEvent("Defender lost a soldier.");
                        }else {
                            logEvent("Attacker lost a soldier.");
                        }
                    }
                }
            }

            // every attack consumes resources (alloys) based on the strength ratio
            int totalDefenceAndAttack = attackPower + defencePower;
            double attackerLossRatio = (double) attackPower / totalDefenceAndAttack;
            double defenderLossRatio = (double) defencePower / totalDefenceAndAttack;

            // this is for better balance
            attackerLossRatio = (attackerLossRatio + 0.5 ) / 2.0;
            defenderLossRatio = (defenderLossRatio + 0.5 ) / 2.0;

            int attackerLoss = Math.min((int) (defenderLossRatio * (days+1000)), 100_000);
            int defenderLoss = Math.min((int) (attackerLossRatio * (days+1000)), 100_000);




            // Debug prints
            System.out.println("Who is attacking? (turn): " + attackTurn);
            System.out.println("Attack Power: " + attackPower);
            System.out.println("Defence Power: " + defencePower);
            System.out.println("Total Defence and Attack: " + totalDefenceAndAttack);
            System.out.println("Attacker Loss Ratio: " + attackerLossRatio);
            System.out.println("Defender Loss Ratio: " + defenderLossRatio);
            System.out.println("Days: " + days);
            System.out.println("Attacker Loss: " + attackerLoss);
            System.out.println("Defender Loss: " + defenderLoss);
            System.out.println("Attacker soldiers" + attacker.getNumOfSoldiers());
            System.out.println("Defender soldiers" + defender.getNumOfSoldiers());

            defender.loseDefencePower(defenderLoss);
            attacker.loseAttackPower(attackerLoss);


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
                logEvent("Battle ended.\nAttacker is victorious.");


                TransferPackage transferPackage = defendingCommander.getWallet().getBalance();
                if(attackingCommander.getWallet().depositAll(defendingCommander.getWallet())){
                    attackingCommander.getEventTracker().addEvent(EventTracker.Message("Major","You have gained: " + transferPackage.toShortString()));
                    defendingCommander.getEventTracker().addEvent(EventTracker.Message("Major","You have lost: " + transferPackage.toShortString()));
                }

                defendingCommander.getGrandFoundry().setUnderOccupation(attackingCommander, 360);


            }

            if (Objects.equals(winner, "Defender")) {
                defendingMilitary.getArmy().setState(null);
                attackingMilitary.getArmy().setState(null);

                attackingCommander.getEventTracker().addEvent(EventTracker.Message("Major", String.format("Your army has lost against %s", defendingCommander.getName())));
                defendingCommander.getEventTracker().addEvent(EventTracker.Message("Major", String.format("Your army is victorious against %s", attackingCommander.getName())));
                logEvent("Battle ended.\nDefender is victorious.");

                TransferPackage transferPackage = attackingCommander.getWallet().getBalance();
                if(defendingCommander.getWallet().depositAll(attackingCommander.getWallet())){
                    defendingCommander.getEventTracker().addEvent(EventTracker.Message("Major","You have gained: " + transferPackage.toShortString()));
                    attackingCommander.getEventTracker().addEvent(EventTracker.Message("Major","You have lost: " + transferPackage.toShortString()));
                }

                attackingCommander.getGrandFoundry().setUnderOccupation(defendingCommander, 360);

            }

            String attackerMsg;
            String defenderMsg;

            String resultA = Objects.equals(winner, "Attacker") ? "Won" : "Lost";
            String resultD = Objects.equals(winner, "Attacker") ? "Lost" : "Won";

            attackerMsg = String.format("%s offensive battle against %s.\nBattle lasted for %d days. Returned with %d soldiers, %d offence weapons and %d defence Weapons",
                    resultA,
                    defendingCommander,
                    days,
                    attackingArmyStats.numOfSoldiers,
                    attackingArmyStats.attackPower / ArmyCost.increaseArmyAttack,
                    attackingArmyStats.defencePower / ArmyCost.increaseArmyDefence);
            defenderMsg = String.format("%s defensive battle against %s.\nBattle lasted for %d days. Returned with %d soldiers, %d offence weapons and %d defence Weapons",
                    resultD,
                    attackingCommander,
                    days,
                    defendingArmyStats.numOfSoldiers,
                    defendingArmyStats.attackPower / ArmyCost.increaseArmyAttack,
                    defendingArmyStats.defencePower / ArmyCost.increaseArmyDefence);


            attackingMilitary.getArmy().returnFromBattle(attackingArmyStats.numOfSoldiers,
                    attackingArmyStats.attackPower / ArmyCost.increaseArmyAttack,
                    attackingArmyStats.defencePower / ArmyCost.increaseArmyDefence,
                    attackerMsg);

            defendingMilitary.getArmy().returnFromBattle(defendingArmyStats.numOfSoldiers,
                    defendingArmyStats.attackPower / ArmyCost.increaseArmyAttack,
                    defendingArmyStats.defencePower / ArmyCost.increaseArmyDefence,
                    defenderMsg);


            WarManager.unsubscribe(this);

            isOnGoing = false;

            attackingCommander.getPaymentManager().removePayment(PaymentManager.PaymentType.EXPENSE, Payment.MILITARY_BATTLE_EXPENSE);
            defendingCommander.getPaymentManager().removePayment(PaymentManager.PaymentType.EXPENSE, Payment.MILITARY_BATTLE_EXPENSE);

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
            logEvent("+" + amount + " soldiers " + (this == attackingArmyStats ? "attacker" : "defender") + ".");
        }

        public void addAttackPower(Army army) {
            int amount = army.sendAttackPower();
            attackPower += amount * ArmyCost.increaseArmyAttack;
            logEvent("+" + amount + " offence " + (this == attackingArmyStats ? "attacker" : "defender") + ".");
        }

        public void addDefencePower(Army army) {
            int amount = army.sendDefencePower();
            defencePower += amount * ArmyCost.increaseArmyDefence;
            logEvent("+" + amount + " defence " + (this == attackingArmyStats ? "attacker" : "defender") + ".");
        }

        public int getNumOfSoldiers() {
            return numOfSoldiers;
        }

        public int getAttackPower() {
            return attackPower + 5_000;
        }

        public int getDefencePower() {
            return defencePower + 5_000;
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
        int defenderDefencePower = defendingArmyStats.getTotalPower() + propertyPower;
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

    public boolean isOnGoing() {
        return isOnGoing;
    }



}
