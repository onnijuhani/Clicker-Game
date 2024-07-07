package model.characters.combat;

import model.buildings.properties.Citadel;
import model.buildings.properties.Fortress;
import model.characters.Character;
import model.characters.Status;
import model.characters.npc.King;
import model.characters.npc.Vanguard;
import model.stateSystem.MessageTracker;
import model.stateSystem.State;

public class CombatService {
    /**
     * @param attacker attakcer
     * @param defender defender
     * @return returns true if error was found, cannot enter battle
     */
    public static boolean checkForError(Character attacker, Character defender) {
        if (checkBattleState(attacker, defender)) return true;
        if (isKingOrNoble(attacker)) return true;
        if (checkIfAttackingSelf(attacker, defender)) return true;
        return false;
    }

    private static boolean checkIfAttackingSelf(Character attacker, Character defender) {
        return attacker == defender;
    }

    private static boolean checkBattleState(Character attacker, Character defender) {
        return attacker.getPerson().hasState(State.IN_BATTLE) || defender.getPerson().hasState(State.IN_BATTLE);
    }
    private static boolean checkVanguard(Character attacker) {
        return attacker instanceof Vanguard;
    }

    private static boolean isKingOrNoble(Character attacker){
        if(!attacker.getPerson().isPlayer()) {
            return
                    attacker.getRole().getStatus() == Status.King ||
                     attacker.getRole().getStatus() == Status.Noble;
                    // NPC King or Noble never attacks others
        }
        return false;
    }

    public static void executeRobbery(Character attacker, Character defender) {
        if (isKingOrNoble(attacker)) return;
        if (checkBattleState(attacker, defender)) return;
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.robbery();
    }


    public static void executeDuel(Character attacker, Character defender) {
        if (checkForError(attacker, defender)) return;
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.Duel();
    }

    public static void executeAuthorityBattle(Character attacker, Character defender) {
        if (checkVanguard(attacker)) return; // vanguard doesnt challenge king
        if (checkForError(attacker, defender)) return;
        if (weakPropertyTest(attacker, defender)) return;
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.authorityBattle();
    }

    private static boolean weakPropertyTest(Character attacker, Character defender) {
        if(defender instanceof King){
            if(attacker.getPerson().getProperty() instanceof Fortress || attacker.getPerson().getProperty() instanceof Citadel) {
                return false;
            } // fortress or citadel is needed to challenge king
            else{
                attacker.getPerson().getMessageTracker().addMessage(MessageTracker.Message("Error", "You need to construct Citadel or Fortress to be King"));
                return true; // is too weak property
            }
        }
        return false;

    }




}