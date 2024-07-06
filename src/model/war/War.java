package model.war;

import model.resourceManagement.TransferPackage;
import model.time.WarManager;
import model.time.WarObserver;
import model.worldCreation.Details;
import model.worldCreation.Nation;

import java.util.*;

import static model.war.War.Phase.*;

public class War implements WarObserver, Details {
    @Override
    public void warUpdate(int day) {
        try {
            this.days ++;
            if(days < 360){
                return;
            }
            if(day == 23 || day == 8){
                finalPhaseActions();
                royalsMatchMaking(day);
                updateSets();
                updateOnGoingBattles();
                matchAndPlay(day);
                updatePhase();
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    @Override
    public String getDetails() {
        StringBuilder s = new StringBuilder("Current phase: " + currentPhase + "\n" +
                "Attacker Militaries In Play: " + attackerMilitariesInPlay.size() + "\n" +
                "Defender Militaries In Play: " + defenderMilitariesInPlay.size() +  "\n" +
                "Attacker Militaries In Battle: " + attackerMilitariesInBattle.size() +  "\n" +
                "Defender Militaries In Battle: " + defenderMilitariesInBattle.size() +  "\n" +
                "Attacker Defeated Militaries: " + attackerDefeatedMilitaries.size() + "\n" +
                "Defender Defeated Militaries: " + defenderDefeatedMilitaries.size() + "\n" +
                "On Going Battles: " + onGoingBattles.size() + "\n");

        if(currentPhase == PHASE3){
            s.append("Attacker Royals: ").append(attackerRoyals.size()).append("\n").append("Defender Royals: ").append(defenderRoyals.size());
        }

        return s.toString();
    }

    public enum Phase {
        PHASE1, PHASE2, PHASE3
    }

    private int days = 0;
    private int phase3StartingDay;

    private Phase currentPhase;
    private final Nation attacker; // Attacker means the nation that started the war. Otherwise, there is no difference.
    private final Nation defender;


    private final HashSet<Military> attackerMilitariesInPlay = new HashSet<>(); // Militaries that are available to send into battle against others
    private final HashSet<Military> defenderMilitariesInPlay = new HashSet<>();

    private final HashSet<Military> attackerMilitariesInBattle = new HashSet<>(); // Militaries that are currently in battle
    private final HashSet<Military> defenderMilitariesInBattle = new HashSet<>();

    private final HashSet<Military> attackerDefeatedMilitaries = new HashSet<>(); // Militaries that are out of the war completely
    private final HashSet<Military> defenderDefeatedMilitaries = new HashSet<>();

    private final HashSet<Military> attackerRoyals = new HashSet<>(); // Militaries that are out of the war completely
    private final HashSet<Military> defenderRoyals = new HashSet<>();

    private final ArrayList<MilitaryBattle> onGoingBattles = new ArrayList<>();

    private boolean aWarTax = false; // attacker war tax set
    private boolean dWarTax = false; // defender war tax set

    private void finalPhaseActions(){
        if(currentPhase != PHASE3){
            return;
        }
        double adr = getDefeatRatio(attackerDefeatedMilitaries, attackerMilitariesInPlay, attackerMilitariesInBattle);
        double ddr = getDefeatRatio(defenderDefeatedMilitaries, defenderMilitariesInPlay, defenderMilitariesInBattle);

        checkAndAddWarTax(adr, ddr); // war tax is only set after certain amount of opponents armies have been defeated

        sendResourceAidToRoyals(attacker, attackerRoyals);
        sendResourceAidToRoyals(defender, defenderRoyals);


    }

    private void sendResourceAidToRoyals(Nation nation, HashSet<Military> royals) {
        if(nation.getWallet().isEmpty()){
            return;
        }
        TransferPackage amountPerRoyal =  nation.getWallet().getBalance().divide(royals.size() + 1);
        System.out.println(nation.getWallet());
        for(Military military : royals){
            military.getOwner().getWallet().addResources(amountPerRoyal);

        }
    }

    private void checkAndAddWarTax(double adr, double ddr) {
        if(adr > 0.5 && !aWarTax){
            attacker.setWarTaxToWorkWallets();
            aWarTax = true;
        }
        if(ddr > 0.5 && !dWarTax){
            defender.setWarTaxToWorkWallets();
            dWarTax = true;
        }
    }


    private void updatePhase(){
        if (testForEmpty(attackerDefeatedMilitaries, defenderDefeatedMilitaries)) return;

        double adr = getDefeatRatio(attackerDefeatedMilitaries, attackerMilitariesInPlay, attackerMilitariesInBattle);
        double ddr = getDefeatRatio(defenderDefeatedMilitaries, defenderMilitariesInPlay, defenderMilitariesInBattle);

        if(currentPhase == PHASE1) {
            if(adr > 0.6){
                startPhase2(defender + " has defeated 60% of the opponents civilian armies.");
            }
            if(ddr > 0.6){
                startPhase2(attacker + " has defeated 60% of the opponents civilian armies.");
            }
        }


        if(currentPhase == PHASE2) {
            if(adr >= 0.8){
                startPhase3(defender + " has defeated 80% of the opponents commander armies.");
            }
            if(ddr >= 0.8){
                startPhase3(attacker + " has defeated 80% of the opponents commander armies.");
            }
        }

        if(currentPhase == PHASE3) {
            if(adr >= 0.7 && attackerRoyals.isEmpty()){
                System.out.println(defender + " has won the war against " + attacker);
                endWar();
            }
            if(ddr >= 0.7 && attackerRoyals.isEmpty()){
                System.out.println(attacker + " has won the war against " + defender);
                endWar();
            }
        }




    }

    private static double getDefeatRatio(HashSet<Military> defeatedMilitaries, HashSet<Military> militariesInPlay, HashSet<Military> militariesInBattle) {
        return (double) defeatedMilitaries.size() / (defeatedMilitaries.size() + militariesInPlay.size() + militariesInBattle.size());
    }

    private static boolean testForEmpty(Collection<?>... collections) {
        for (Collection<?> collection : collections) {
            if (collection.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    private void updateSets(){
        // Attacker
        filterArmies(attackerMilitariesInPlay, attackerDefeatedMilitaries, attackerMilitariesInBattle);

        // Defender
        filterArmies(defenderMilitariesInPlay, defenderDefeatedMilitaries, defenderMilitariesInBattle);
    }

    private void filterArmies(Set<Military> militariesAvailable, Set<Military> defeatedMilitaries, Set<Military> militariesInBattle) {
        HashSet<Military> all = new HashSet<>(militariesAvailable);
        all.addAll(militariesInBattle);
        for (Military military : all) {

            Army.ArmyState state = military.getState();

            if(state == null){
                militariesInBattle.remove(military);
                militariesAvailable.add(military);
                continue;
            }

            // If military is defeated, move to defeatedMilitaries
            if (state == Army.ArmyState.DEFEATED) {
                defeatedMilitaries.add(military);
                militariesAvailable.remove(military);
                militariesInBattle.remove(military);
                continue;
            }

            // If military is in battle, move to militariesInBattle
            if (state == Army.ArmyState.ATTACKING || state == Army.ArmyState.DEFENDING) {
                militariesInBattle.add(military);
                militariesAvailable.remove(military);
                continue;
            }



        }
    }


    public War(Nation attacker, Nation defender) {
        this.attacker = attacker;
        this.defender = defender;
        startPhase1();
        WarManager.subscribe(this);
    }

    public void endWar(){
        WarManager.unsubscribe(this);
    }

    private void updateOnGoingBattles(){
        onGoingBattles.removeIf(battle -> !battle.isOnGoing());
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Nation getAttacker() {
        return attacker;
    }

    public Nation getDefender() {
        return defender;
    }

    // Automatic matchmaking and battles
    public void startPhase1() {
        setCurrentPhase(PHASE1);

        // Add civilian militaries to in play set
        attackerMilitariesInPlay.addAll(attacker.getMilitariesOwnedByCivilians());
        defenderMilitariesInPlay.addAll(defender.getMilitariesOwnedByCivilians());
    }

    public void startPhase2(String message) {
        if(currentPhase == PHASE1) {
            System.out.println(message);
            attackerMilitariesInPlay.addAll(attacker.getMilitariesOwnedByCommanders());
            defenderMilitariesInPlay.addAll(defender.getMilitariesOwnedByCommanders());
            System.out.println(attacker.getMilitariesOwnedByCommanders());
        }

        setCurrentPhase(PHASE2);

    }

    public void startPhase3(String message) {
        if(currentPhase == PHASE2) {
            System.out.println(message);

            attackerRoyals.addAll(attacker.getMilitariesOwnedByKingAndHisSentinels());


            defenderRoyals.addAll(defender.getMilitariesOwnedByKingAndHisSentinels());

            setCurrentPhase(PHASE3);
            phase3StartingDay = days;
            finalPhaseActions();
        }

    }

    private void royalsMatchMaking(int day){
        if(currentPhase != PHASE3) return;
        if(days < phase3StartingDay + 180) return;

        testRoyalMilitary(attackerRoyals, attackerDefeatedMilitaries);
        testRoyalMilitary(defenderRoyals, defenderDefeatedMilitaries);

        if(day == 23) {
            matchMaking(attackerRoyals, defenderRoyals, day);
        }else{
            matchMaking(defenderRoyals, attackerRoyals, day);
        }
    }

    private void testRoyalMilitary(HashSet<Military> set, HashSet<Military> defeated) {
        Iterator<Military> iterator = set.iterator();
        while (iterator.hasNext()) {
            Military m = iterator.next();
            if (m.getState() == Army.ArmyState.DEFEATED) {
                iterator.remove();
                defeated.add(m);
            }
        }
    }
    private void matchAndPlay(int day) {
        if(day == 23) {
            matchMaking(attackerMilitariesInPlay, defenderMilitariesInPlay, day);
        }else{
            matchMaking(attackerMilitariesInPlay, defenderMilitariesInPlay, day);
        }
    }

    /**
     * @param aip Attacker Militaries In Play
     * @param dip Defender Militaries In Play
     */
    private void matchMaking(HashSet<Military> aip, HashSet<Military> dip, int day) {
        if (testForEmpty(aip, dip)) return;

        List<Military> attackerList = new ArrayList<>(aip);
        List<Military> defenderList = new ArrayList<>(dip);

        if (day == 8) {
            while (!attackerList.isEmpty() && !defenderList.isEmpty()) {
                // Handle the first pair of militaries
                handleBattle(attackerList.remove(0), defenderList.remove(0), true);

                // If lists are empty after the first battle, break the loop
                if (attackerList.isEmpty() || defenderList.isEmpty()) break;

                // Handle the second pair of militaries with roles reversed
                handleBattle(defenderList.remove(0), attackerList.remove(0), false);
            }
        }else{
            while (!attackerList.isEmpty() && !defenderList.isEmpty()) {
                // Handle the first pair of militaries
                handleBattle(defenderList.remove(0), attackerList.remove(0), false);

                // If lists are empty after the first battle, break the loop
                if (attackerList.isEmpty() || defenderList.isEmpty()) break;

                // Handle the second pair of militaries with roles reversed
                handleBattle(attackerList.remove(0), defenderList.remove(0), true);
            }
        }
    }

    private void handleBattle(Military attacker, Military defender, boolean isOriginalOrder) {
        if (testMilitariesForUndefeated(attacker, defender)) {
            if (attacker == defender) {
                System.out.println("WTF");
            }

            MilitaryBattle battle = SiegeService.executeMilitaryBattle(attacker.getOwner(), defender.getOwner());
            if (addMilitaryBattle(battle)) {
                if (isOriginalOrder) {
                    attackerMilitariesInPlay.remove(attacker);
                    defenderMilitariesInPlay.remove(defender);
                    attackerMilitariesInBattle.add(attacker);
                    defenderMilitariesInBattle.add(defender);
                } else {
                    defenderMilitariesInPlay.remove(attacker);
                    attackerMilitariesInPlay.remove(defender);
                    defenderMilitariesInBattle.add(attacker);
                    attackerMilitariesInBattle.add(defender);
                }
            }
        }
    }


    /**
     * @param battle battle to be added to the class list
     * @return returns false if battle is null, and true if battle started successfully
     */
    public boolean addMilitaryBattle(MilitaryBattle battle) {
        if(battle == null){
            return false;
        }
        onGoingBattles.add(battle);
        return true;
    }
    public ArrayList<MilitaryBattle> getOnGoingBattles() {
        return onGoingBattles;
    }




    /**
     * @param m military 1
     * @param m2 military 2
     * @return Returns true if either army is undefeated, false if either is defeated
     */
    private boolean testMilitariesForUndefeated(Military m, Military m2) {
        boolean isUndefeated = true;
        if(m.getState() == Army.ArmyState.DEFEATED){
            attackerDefeatedMilitaries.add(m);
            attackerMilitariesInPlay.remove(m);
            attackerMilitariesInBattle.remove(m);
            isUndefeated = false;
        }
        if(m2.getState() == Army.ArmyState.DEFEATED){
            defenderDefeatedMilitaries.add(m2);
            defenderMilitariesInPlay.remove(m2);
            defenderMilitariesInBattle.remove(m2);
            isUndefeated = false;
        }

        return isUndefeated;
    }

    public void manualCommand(Military attacker, Military defender) {
        if (currentPhase != PHASE2) return;
        // Implement manual command logic
        battle(attacker, defender);
    }

    private void battle(Military attacker, Military defender) {
        // Implement battle logic, including removing defeated armies
        // and updating eliminatedMilitaries list
    }

    public void finalAssault() {
        if (currentPhase != PHASE3) return;
        // Implement logic for final assault on the king and bodyguards
    }



}
