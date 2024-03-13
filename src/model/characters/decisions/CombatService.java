package model.characters.decisions;

import model.characters.Character;
import model.characters.combat.CombatSystem;

public class CombatService {
    public static void executeRobbery(Character attacker, Character defender) {
        CombatSystem combatSystem = new CombatSystem(attacker, defender, defender.getProperty());
        combatSystem.robbery();
    }

    // Add more methods for other types of battles, following the same pattern.
}