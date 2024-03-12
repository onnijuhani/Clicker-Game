package model.characters.combat;

import model.buildings.Property;
import model.characters.Character;

public class CombatService {
    public static void executeRobbery(Character attacker, Character defender, Property venue) {
        CombatSystem combatSystem = new CombatSystem(attacker, defender, venue);
        combatSystem.robbery();
    }

    // Add more methods for other types of battles, following the same pattern.
}