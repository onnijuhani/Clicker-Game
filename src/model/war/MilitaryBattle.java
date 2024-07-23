package model.war;

import model.Settings;
import model.characters.Person;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.resourceManagement.TransferPackage;
import model.stateSystem.MessageTracker;
import model.time.WarManager;
import model.time.WarObserver;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("CallToPrintStackTrace")
public class MilitaryBattle implements WarObserver {

    @Override
    public void warUpdate(int day) {
        days++;

        if(attackingCommander.isPlayer() || defendingCommander.isPlayer()){
            System.out.println();
        }

        if(day == 6) {
            payRunningCosts();
            updatePaymentCalendars();
        }

        if(days >= 300){
            triggerForceEnd();
            return;
        }

        if(Objects.equals(currentTurn, "Attacker")){
            performAttack(attackingArmyStats, defendingArmyStats, "Attacker");
            setCurrentTurn("Defender");
            return;
        }
        if(Objects.equals(currentTurn, "Defender")){
            performAttack(defendingArmyStats, attackingArmyStats, "Defender");
            setCurrentTurn("Attacker");
            return;
        }
    }

    @Override
    public String toString(){
        return String.format("Day %d: %s S:%d, Off:%d, Def:%d - %s S:%d, Off%d, Def:%d ",
                days,
                attackingCommander.getRole(),
                attackingArmyStats.getNumOfSoldiers(),
                attackingArmyStats.getAttackPower()/5000,
                attackingArmyStats.getDefencePower()/5000,

                defendingCommander.getRole(),
                defendingArmyStats.getNumOfSoldiers(),
                defendingArmyStats.getAttackPower()/5000,
                defendingArmyStats.getDefencePower()/5000);
    }

    private void triggerForceEnd() {
        if (attackingArmyStats.getNumOfSoldiers() <= 1) {
            settleBattle("Defender");
            return;
        }
        if (defendingArmyStats.getNumOfSoldiers() <= 1) {
            settleBattle("Attacker");
            return;
        }
    }

    private void updatePaymentCalendars() {
        attackingCommander.getPaymentManager().addPayment(PaymentManager.PaymentType.EXPENSE, Payment.MILITARY_BATTLE_EXPENSE, attackingArmyStats.getWarCost(), 6);
        defendingCommander.getPaymentManager().addPayment(PaymentManager.PaymentType.EXPENSE, Payment.MILITARY_BATTLE_EXPENSE, defendingArmyStats.getWarCost(), 6);
    }
    private String currentTurn = "Attacker"; // attacker starts
    private final Military attackingMilitary;


    private final Military defendingMilitary;
    private final ArmyStats attackingArmyStats;
    private final ArmyStats defendingArmyStats;
    private final int propertyPower;
    private final Person attackingCommander;
    private final Person defendingCommander;
    private final Random random = Settings.getRandom();
    private int days = 0; // tracks how many days have gone by
    private final LinkedList<String> battleLog = new LinkedList<>(); // Logging system
    private boolean isOnGoing = true;

    public MilitaryBattle(Military attacker, Military defender) {
        this.attackingArmyStats = new ArmyStats(attacker.getArmy());
        this.defendingArmyStats = new ArmyStats(defender.getArmy());
        this.attackingMilitary = attacker;
        this.defendingMilitary = defender;
        testMilitaries();

        this.propertyPower = defender.getPower();

        this.attackingCommander = attacker.getOwner();
        this.defendingCommander = defender.getOwner();

        WarManager.subscribe(this);

        setStartingStates();


        attacker.getArmy().enterIntoBattle(this);
        defender.getArmy().enterIntoBattle(this);

    }

    private void testMilitaries() {
        if(this.attackingMilitary.getArmy().getNumOfSoldiers() < 1){
            this.attackingMilitary.getArmy().addOneSoldier();
        }
        if(this.defendingMilitary.getArmy().getNumOfSoldiers() < 1){
            this.defendingMilitary.getArmy().addOneSoldier();
        }
    }

    private void setStartingStates() {
        attackingMilitary.getArmy().setState(Army.ArmyState.ATTACKING);
        defendingMilitary.getArmy().setState(Army.ArmyState.DEFENDING);
        logEvent("Battle started.\nAttacker: " + attackingCommander.getCharacter().getName() + "\nDefender: " + defendingCommander.getCharacter().getName());
    }
    private void performAttack(ArmyStats currentAttackTurn, ArmyStats currentDefenceTurn, String attacker) {
        try {
            testMilitaries();
            // battle ends when 1 army runs out of soldiers
            if (attackingArmyStats.getNumOfSoldiers() <= 1) {
                settleBattle("Defender");
                return;
            }
            if (defendingArmyStats.getNumOfSoldiers() <= 1) {
                settleBattle("Attacker");
                return;
            }


            int attackPower = currentAttackTurn.getAttackPower();
            int defencePower = currentDefenceTurn.getDefencePower();

            if(Objects.equals(attacker, "Attacker")){
                defencePower += propertyPower;
            }

            NobleBonus nobleBonus = getNobleBonus(currentAttackTurn);

            int attackStrength = (attackPower * currentAttackTurn.getNumOfSoldiers() + 5000) * nobleBonus.attackerNobleBonus();
            int defenceStrength = (defencePower * currentDefenceTurn.getNumOfSoldiers() + 5000) * nobleBonus.defenderNobleBonus();

            boolean attackSucceeds = random.nextDouble() < 0.8; // 80% chance of attack success

            // effective power is another way to determine if attack succeeds, it's determined by the share of the strength
            double effectivePower = Math.max((double) attackStrength / (attackStrength + defenceStrength), 0.1); // 10% is the minimum
            double rand = random.nextDouble();

            if (attackSucceeds) {
                if (effectivePower > rand) {
                    standardSoldierLoss(currentAttackTurn, currentDefenceTurn, effectivePower);
                } else {
                    attackerSoldierLoss(currentAttackTurn, effectivePower);
                }
                longBattleSoldierLoss(currentAttackTurn);
            }

            // every attack consumes resources (alloys) based on the strength ratio
            int totalDefenceAndAttack = attackPower + defencePower;
            double attackerLossRatio = (double) attackPower / totalDefenceAndAttack;
            double defenderLossRatio = (double) defencePower / totalDefenceAndAttack;

            // this is for better balance
            attackerLossRatio = (attackerLossRatio + 0.5 ) / 2.0;
            defenderLossRatio = (defenderLossRatio + 0.5 ) / 2.0;

            int attackerLoss = Math.min((int) (defenderLossRatio * (days+800)), 55_000);
            int defenderLoss = Math.min((int) (attackerLossRatio * (days+800)), 55_000);


            // Debug prints
//            System.out.println("Who is attacking? (turn): " + attacker);
//            System.out.println("Attack Power: " + attackPower);
//            System.out.println("Defence Power: " + defencePower);
//            System.out.println("Total Defence and Attack: " + totalDefenceAndAttack);
//            System.out.println("Attacker Loss Ratio: " + attackerLossRatio);
//            System.out.println("Defender Loss Ratio: " + defenderLossRatio);
//            System.out.println("Days: " + days);
//            System.out.println("Attacker Loss: " + attackerLoss);
//            System.out.println("Defender Loss: " + defenderLoss);
//            System.out.println("Attacker soldiers" + currentAttackTurn.getNumOfSoldiers());
//            System.out.println("Defender soldiers" + currentDefenceTurn.getNumOfSoldiers());

            currentDefenceTurn.loseDefencePower(defenderLoss);
            currentAttackTurn.loseAttackPower(attackerLoss);


        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    private void attackerSoldierLoss(ArmyStats currentAttackTurn, double effectivePower) {
        if(effectivePower < 20 && days > 100){
            if(currentAttackTurn == attackingArmyStats){
                logEvent(String.format("Defender lost %d soldier.", 1));
            }else {
                logEvent(String.format("Attacker lost %d soldier.", 1));
            }
        }
    }

    private void standardSoldierLoss(ArmyStats currentAttackTurn, ArmyStats currentDefenceTurn, double effectivePower) {
        if (random.nextDouble() < Math.min(effectivePower, 0.9)) { // effective power also determines if soldier will die, but 90% chance is the best this can go.

            int lostSoldiers = getLostSoldiers(effectivePower, days);

            currentDefenceTurn.loseSoldiers(lostSoldiers);
            if(currentAttackTurn == attackingArmyStats){
                logEvent(String.format("Defender lost %d soldier%s.", lostSoldiers, lostSoldiers>1 ? " " : "s"));
            }else {
                logEvent(String.format("Attacker lost %d soldier%s.", lostSoldiers, lostSoldiers>1 ? " " : "s"));
            }
        }
    }

    private void longBattleSoldierLoss(ArmyStats currentAttackTurn) {
        if(days > 50){
            int lostSoldiers = Math.max(days / 100, 1);
            if(currentAttackTurn == attackingArmyStats){
                logEvent(String.format("Defender lost %d soldier%s.", lostSoldiers, lostSoldiers>1 ? " " : "s"));
            }else {
                logEvent(String.format("Attacker lost %d soldier%s.", lostSoldiers, lostSoldiers>1 ? " " : "s"));
            }
        }
    }

    private static int getLostSoldiers(double effectivePower, int days) {
        int base = Math.max(days / 25, 1);
        return base  +  (int) Math.min(10, Math.floor((effectivePower - 0.5) * 10));
    }

    private NobleBonus getNobleBonus(ArmyStats currentAttackTurn) {
        int attackerNobleBonus = 1;
        int defenderNobleBonus = 1;

        if (currentAttackTurn == attackingArmyStats) {
            if (attackingCommander.getRole().getNation().isNobleWarBonus()) {
                attackerNobleBonus = 2;
            }
            if (defendingCommander.getRole().getNation().isNobleWarBonus()) {
                defenderNobleBonus = 2;
            }
        } else {
            if (defendingCommander.getRole().getNation().isNobleWarBonus()) {
                attackerNobleBonus = 2;
            }
            if (attackingCommander.getRole().getNation().isNobleWarBonus()) {
                defenderNobleBonus = 2;
            }
        }
        return new NobleBonus(attackerNobleBonus, defenderNobleBonus);
    }

    private record NobleBonus(int attackerNobleBonus, int defenderNobleBonus) {
    }

    private void settleBattle(String winner) {
        try {
            if (Objects.equals(winner, "Attacker")) {
                defendingMilitary.getArmy().setState(Army.ArmyState.DEFEATED);
                attackingMilitary.getArmy().setState(null);

                attackingCommander.getMessageTracker().addMessage(MessageTracker.Message("Major", String.format("Your army is victorious against %s", defendingCommander.getName())));
                defendingCommander.getMessageTracker().addMessage(MessageTracker.Message("Major", String.format("Your army has lost against %s", attackingCommander.getName())));
                logEvent("Battle ended.\nAttacker is victorious.");


                int atWarIncrease = attackingCommander.getRole().getNation().isAtWar() ? 2 : 1;
                int days = Math.min(Math.max(this.days, 90) * atWarIncrease, 720);
                defendingCommander.getGrandFoundry().setUnderOccupation(attackingCommander, days);
            }

            if (Objects.equals(winner, "Defender")) {
                defendingMilitary.getArmy().setState(null);
                attackingMilitary.getArmy().setState(Army.ArmyState.DEFEATED);

                attackingCommander.getMessageTracker().addMessage(MessageTracker.Message("Major", String.format("Your army has lost against %s", defendingCommander.getName())));
                defendingCommander.getMessageTracker().addMessage(MessageTracker.Message("Major", String.format("Your army is victorious against %s", attackingCommander.getName())));
                logEvent("Battle ended.\nDefender is victorious.");


                int x = defendingCommander.getRole().getNation().isAtWar() ? 4 : 2;
                int days = Math.min(Math.min(this.days, 360) * x, 1800);
                attackingCommander.getGrandFoundry().setUnderOccupation(defendingCommander, days);
            }

            String attackerMsg;
            String defenderMsg;

            String resultA = Objects.equals(winner, "Attacker") ? "Won" : "Lost";
            String resultD = Objects.equals(winner, "Defender") ? "Won" : "Lost";

            attackerMsg = String.format("%s offensive battle against %s.\nBattle lasted for %d days. Lost %s Soldiers. Returned with %d soldiers, %d offence weapons and %d defence Weapons",
                    resultA,
                    defendingCommander,
                    days,
                    attackingArmyStats.getLostSoldiers(),
                    attackingArmyStats.numOfSoldiers,
                    attackingArmyStats.attackPower / ArmyCost.increaseArmyAttack,
                    attackingArmyStats.defencePower / ArmyCost.increaseArmyDefence);
            defenderMsg = String.format("%s defensive battle against %s.\nBattle lasted for %d days. Lost %s Soldiers. Returned with %d soldiers, %d offence weapons and %d defence Weapons",
                    resultD,
                    attackingCommander,
                    days,
                    defendingArmyStats.getLostSoldiers(),
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
        if(battleLog.size() >= 3){
            battleLog.removeFirst();
        }
        String message = "Day " + days + ": " + event;
        battleLog.add(message);
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
                attackingCommander.loseStrike("Army expenses not paid.");
            }else{
                attackingCommander.getMessageTracker().addMessage(MessageTracker.Message("Minor", "War expenses paid: " + attackingArmyStats.getWarCost().toShortString()));
            }

            if(!defendingCommander.getWallet().subtractResources(defendingArmyStats.getWarCost())){
                defendingCommander.loseStrike("Army expenses not paid.");
            }else{
                defendingCommander.getMessageTracker().addMessage(MessageTracker.Message("Minor", "War expenses paid: " + attackingArmyStats.getWarCost().toShortString()));
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    public Military getAttackingMilitary() {
        return attackingMilitary;
    }
    public Military getDefendingMilitary() {
        return defendingMilitary;
    }

    public class ArmyStats {
        private int numOfSoldiers;
        private int attackPower;
        private int defencePower;
        private int lostSoldiers = 0;

        public ArmyStats(Army army) {
            this.numOfSoldiers = army.getNumOfSoldiers();
            this.attackPower = army.getAttackPower() * ArmyCost.increaseArmyAttack;
            this.defencePower = army.getDefencePower() * ArmyCost.increaseArmyDefence;

        }

        public int getLostSoldiers() {
            return lostSoldiers;
        }

        private void addLostSoldiers(int lostSoldiers) {
            if(lostSoldiers < 1) return;
            this.lostSoldiers += lostSoldiers;
        }

        public void loseSoldiers(int amount){
            this.numOfSoldiers -= amount;
            addLostSoldiers(amount);
            if (this.numOfSoldiers < 0) {
                this.numOfSoldiers = 1;
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

    /**
     * @return returns winning chance from attackers perspective
     */
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
