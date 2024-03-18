package model.characters.combat;

import model.characters.Character;
import model.stateSystem.EventTracker;

public class CombatService {
    private static boolean checkForError(Character attacker, Character defender) {
        if (attacker == defender){
            attacker.getPerson().getEventTracker().addEvent(EventTracker.Message("Error", "Cannot fight yourself"));
            return true;
        }
        return false;
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