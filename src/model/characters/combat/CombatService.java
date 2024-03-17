package model.characters.combat;

import model.characters.Character;

public class CombatService {
    public static void executeRobbery(Character attacker, Character defender) {
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.robbery();
    }

    public static void executeDuel(Character attacker, Character defender) {
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.Duel();
    }

    public static void executeAuthorityBattle(Character attacker, Character defender) {
        CombatSystem combatSystem = new CombatSystem(attacker, defender);
        combatSystem.authorityBattle();
    }


}