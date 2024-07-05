package model.war;

import model.time.WarObserver;
import model.worldCreation.Nation;

import java.util.*;

import static model.war.War.Phase.PHASE1;

public class War implements WarObserver {
    @Override
    public void warUpdate(int day) {
        if(day == 23){
            updatePhase();
            updateSets();
            matchAndBattle();
            updateOnGoingBattles();
        }
    }

    public enum Phase {
        PHASE1, PHASE2, PHASE3
    }

    private Phase currentPhase;
    private final Nation attacker;
    private final Nation defender;


    private final HashSet<Military> attackerMilitariesInPlay = new HashSet<>(); // Militaries that are available to send into battle against others
    private final HashSet<Military> defenderMilitariesInPlay = new HashSet<>();

    private final HashSet<Military> attackerMilitariesInBattle = new HashSet<>(); // Militaries that are currently in battle
    private final HashSet<Military> defenderMilitariesInBattle = new HashSet<>();

    private final HashSet<Military> attackerDefeatedMilitaries = new HashSet<>(); // Militaries that are out of the war completely
    private final HashSet<Military> defenderDefeatedMilitaries = new HashSet<>();



    private final ArrayList<MilitaryBattle> onGoingBattles = new ArrayList<>();




    private void updatePhase(){

        int attackerDefeatRatio = attackerDefeatedMilitaries.size() / (attackerDefeatedMilitaries.size() + attackerMilitariesInPlay.size());
        int defenderDefeatRatio = defenderDefeatedMilitaries.size() / (defenderDefeatedMilitaries.size() + defenderMilitariesInPlay.size());

        if(currentPhase == PHASE1) {
            if(attackerDefeatRatio > 0.8){
                startPhase2(defender + " has defeated 80% of the opponents civilian armies.");
            }
            if(defenderDefeatRatio > 0.8){
                startPhase2(attacker + " has defeated 80% of the opponents civilian armies.");
            }
        }
    }

    private void updateSets(){
        // Attacker
        filterArmies(attackerMilitariesInPlay, attackerDefeatedMilitaries, attackerMilitariesInBattle);

        // Defender
        filterArmies(defenderMilitariesInPlay, defenderDefeatedMilitaries, defenderMilitariesInBattle);
    }

    private void filterArmies(Set<Military> militariesAvailable, Set<Military> defeatedMilitaries, Set<Military> militariesInBattle) {
        Iterator<Military> iterator = militariesAvailable.iterator();
        while (iterator.hasNext()) {
            Military military = iterator.next();

            // If military is defeated, move to defeatedMilitaries
            if (military.getState() == Army.ArmyState.DEFEATED) {
                defeatedMilitaries.add(military);
                iterator.remove();
                militariesInBattle.remove(military);
                return;
            }

            // If military is in battle, move to militariesInBattle
            if (military.getState() == Army.ArmyState.ATTACKING || military.getState() == Army.ArmyState.DEFENDING) {
                militariesInBattle.add(military);
                iterator.remove();
                return;
            }

            militariesInBattle.remove(military);
            militariesAvailable.add(military);

        }
    }


    public War(Nation attacker, Nation defender) {
        this.attacker = attacker;
        this.defender = defender;
        startPhase1();
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
        attackerMilitariesInPlay.addAll(attacker.getMilitariesOwnedByGenerals());
        defenderMilitariesInPlay.addAll(defender.getMilitariesOwnedByGenerals());
    }

    public void startPhase2(String message) {
        setCurrentPhase(Phase.PHASE2);

        attackerMilitariesInPlay.addAll(attacker.getMilitariesOwnedByGenerals());
        defenderMilitariesInPlay.addAll(defender.getMilitariesOwnedByGenerals());

    }

    public void startPhase3() {
        setCurrentPhase(Phase.PHASE3);
        // Focus on attacking the king and bodyguards
    }

    public void addMilitaryBattle(MilitaryBattle battle) {
        if(battle == null){
            return;
        }
        onGoingBattles.add(battle);
    }
    public ArrayList<MilitaryBattle> getOnGoingBattles() {
        return onGoingBattles;
    }

    private void matchAndBattle() {

        List<Military> attackerList = new ArrayList<>(attackerMilitariesInPlay);
        List<Military> defenderList = new ArrayList<>(defenderMilitariesInPlay);

        while (!attackerList.isEmpty() && !defenderList.isEmpty()) {
            // Attacker here means the attacking military, not the attacking nation!
            Military attacker = attackerList.remove(0);
            Military defender = defenderList.remove(0);

            // 2 battles launch at the same time with different party being the attacker
            Military attacker2 = defenderList.remove(0);
            Military defender2 = attackerList.remove(0);

            MilitaryBattle battle1 = MilitaryBattleManager.executeMilitaryBattle(attacker.getOwner(), defender.getOwner());
            MilitaryBattle battle2 = MilitaryBattleManager.executeMilitaryBattle(attacker2.getOwner(), defender2.getOwner());

            addMilitaryBattle(battle1);
            addMilitaryBattle(battle2);
        }
    }

    public void manualCommand(Military attacker, Military defender) {
        if (currentPhase != Phase.PHASE2) return;
        // Implement manual command logic
        battle(attacker, defender);
    }

    private void battle(Military attacker, Military defender) {
        // Implement battle logic, including removing defeated armies
        // and updating eliminatedMilitaries list
    }

    public void finalAssault() {
        if (currentPhase != Phase.PHASE3) return;
        // Implement logic for final assault on the king and bodyguards
    }



}
