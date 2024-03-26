package model.characters.combat;

import model.characters.Character;
import model.stateSystem.EventTracker;
import model.stateSystem.State;

public class CombatService {
    private static boolean checkForError(Character attacker, Character defender) {
        if (checkBattleState(attacker, defender)) return false;
        if (attacker == defender){
            attacker.getPerson().getEventTracker().addEvent(EventTracker.Message("Error", "Cannot fight yourself"));
            return true;
        }
        return false;
    }

    private static boolean checkBattleState(Character attacker, Character defender) {
        return attacker.getPerson().hasState(State.IN_BATTLE) || defender.getPerson().hasState(State.IN_BATTLE);
    }

    public static void executeRobbery(Character attacker, Character defender) {
        if (checkForError(attacker, defender)) return;
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.robbery();
    }


    public static void executeDuel(Character attacker, Character defender) {
        if (checkForError(attacker, defender)) return;
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.Duel();
    }

    public static void executeAuthorityBattle(Character attacker, Character defender) {
        if (checkForError(attacker, defender)) return;
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.authorityBattle();
    }


}