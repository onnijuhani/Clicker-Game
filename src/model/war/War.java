package model.war;

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
            if(day == 23 || day == 8){
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
        String text ="Current phase: " + currentPhase + "\n" +
                "Attacker Militaries In Play: " + attackerMilitariesInPlay.size() + "\n" +
                "Defender Militaries In Play: " + defenderMilitariesInPlay.size() +  "\n" +
                "Attacker Militaries In Battle: " + attackerMilitariesInBattle.size() +  "\n" +
                "Defender Militaries In Battle: " + defenderMilitariesInBattle.size() +  "\n" +
                "Attacker Defeated Militaries: " + attackerDefeatedMilitaries.size() + "\n" +
                "Defender Defeated Militaries: " + defenderDefeatedMilitaries.size() + "\n" +
                "On Going Battles: " + onGoingBattles.size();
        return text;
    }

    public enum Phase {
        PHASE1, PHASE2, PHASE3
    }

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




    private void updatePhase(){
        if (testForEmpty(attackerDefeatedMilitaries, defenderDefeatedMilitaries)) return;

        double attackerDefeatRatio = (double) attackerDefeatedMilitaries.size() / (attackerDefeatedMilitaries.size() + attackerMilitariesInPlay.size() + attackerMilitariesInBattle.size());
        double defenderDefeatRatio = (double) defenderDefeatedMilitaries.size() / (defenderDefeatedMilitaries.size() + defenderMilitariesInPlay.size() + defenderMilitariesInBattle.size());

        if(currentPhase == PHASE1) {
            if(attackerDefeatRatio > 0.6){
                startPhase2(defender + " has defeated 60% of the opponents civilian armies.");
            }
            if(defenderDefeatRatio > 0.6){
                startPhase2(attacker + " has defeated 60% of the opponents civilian armies.");
            }
        }


        if(currentPhase == PHASE2) {
            if(attackerDefeatRatio >= 0.6){
                startPhase3(defender + " has defeated 60% of the opponents commander armies.");
            }
            if(defenderDefeatRatio >= 0.6){
                startPhase3(attacker + " has defeated 60% of the opponents commander armies.");
            }
        }

        if(currentPhase == PHASE3) {
            if(attackerDefeatRatio >= 1){
                System.out.println(defender + " has won the war against " + attacker);
                endWar();
            }
            if(defenderDefeatRatio >= 1){
                System.out.println(attacker + " has won the war against " + defender);
                endWar();
            }
        }




    }

    public boolean testForEmpty(Collection<?>... collections) {
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
            attackerMilitariesInPlay.addAll(attacker.getMilitariesOwnedByKingAndHisSentinels());
            attackerRoyals.addAll(attacker.getMilitariesOwnedByKingAndHisSentinels());

            defenderMilitariesInPlay.addAll(defender.getMilitariesOwnedByKingAndHisSentinels());
            defenderRoyals.addAll(defender.getMilitariesOwnedByKingAndHisSentinels());
        }
        setCurrentPhase(PHASE3);
    }

    private void royalsMatchMaking(int day){
        if(currentPhase != PHASE3) return;

        testRoyalMilitary(attackerRoyals, attackerDefeatedMilitaries);
        testRoyalMilitary(defenderRoyals, defenderDefeatedMilitaries);

        if(day == 23) {
            matchMaking(attackerRoyals, defenderRoyals);
        }else{
            matchMaking(defenderRoyals, attackerRoyals);
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
            matchMaking(attackerMilitariesInPlay, defenderMilitariesInPlay);
        }else{
            matchMaking(defenderMilitariesInPlay, attackerMilitariesInPlay);
        }
    }

    private void matchMaking(HashSet<Military> attackerSet, HashSet<Military> defenderSet) {
        if (testForEmpty(attackerSet, defenderSet)) return;

        List<Military> attackerList = new ArrayList<>(attackerSet);
        List<Military> defenderList = new ArrayList<>(defenderSet);

        while (!attackerList.isEmpty() && !defenderList.isEmpty()) {
            // Attacker here means the attacking military, not the attacking nation!
            Military attacker = attackerList.remove(0);
            Military defender = defenderList.remove(0);

            if (testMilitariesForUndefeated(attacker, defender)) {
                MilitaryBattle battle1 = MilitaryBattleManager.executeMilitaryBattle(attacker.getOwner(), defender.getOwner());
                if(addMilitaryBattle(battle1)) {
                    defenderMilitariesInPlay.remove(defender);
                    attackerMilitariesInPlay.remove(attacker);
                    attackerMilitariesInBattle.add(attacker);
                    defenderMilitariesInBattle.add(defender);
                }
            }

            if (attackerList.isEmpty() || defenderList.isEmpty()) break;

            // 2 battles launch at the same time with different party being the attacker
            Military attacker2 = defenderList.remove(0);
            Military defender2 = attackerList.remove(0);

            if (testMilitariesForUndefeated(attacker2, defender2)) {
                MilitaryBattle battle2 = MilitaryBattleManager.executeMilitaryBattle(attacker2.getOwner(), defender2.getOwner());
                if(addMilitaryBattle(battle2)) {
                    defenderMilitariesInPlay.remove(attacker2);
                    attackerMilitariesInPlay.remove(defender2);
                    attackerMilitariesInBattle.add(defender2);
                    defenderMilitariesInBattle.add(attacker2);
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
