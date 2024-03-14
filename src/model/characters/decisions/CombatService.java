package model.characters.decisions;

import model.characters.Character;
import model.characters.combat.CombatSystem;

public class CombatService {
    public static void executeRobbery(Character attacker, Character defender) {
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.robbery();
    }

    public static void executeDuel(Character attacker, Character defender) {
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.Duel();
    }


}