package model.characters.combat;

import model.buildings.Property;
import model.characters.Character;
import model.characters.player.EventTracker;
import model.shop.UpgradeSystem;

public class CombatSystem {
    private final Character attacker;
    private final CombatStats attackerStats;
    private final Character defender;
    private final CombatStats defenderStats;
    private final Property venue;
    private final UpgradeSystem venueStats;
    public CombatSystem(Character attacker, Character defender, Property venue) {
        this.attacker = attacker;
        this.defender = defender;
        this.venue = venue;
        this.attackerStats = attacker.getCombatStats();
        this.defenderStats = defender.getCombatStats();
        this.venueStats = venue.getDefense();
    }

    public void authorityBattle() {
        int effectiveAttackerOffense = attackerStats.getOffense().getUpgradeLevel();
        int effectiveDefenderDefense = defenderStats.getDefense().getUpgradeLevel() + venueStats.getUpgradeLevel();

        boolean attackerWins = battle(effectiveAttackerOffense, effectiveDefenderDefense);

        if (attackerWins) {
            System.out.println("Attacker wins!");


        } else {
            System.out.println("Defender wins!");

        }
    }

    public void robbery() {
        int effectiveAttackerOffense = attackerStats.getOffense().getUpgradeLevel();
        int effectiveDefenderDefense = venueStats.getUpgradeLevel();

        boolean attackerWins = battle(effectiveAttackerOffense, effectiveDefenderDefense);

        if (attackerWins) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Robbed the "
                            + defender.getProperty().getName() + " vault"));

            defender.getEventTracker().addEvent((EventTracker.Message(
                    "Major", "You have been robbed by " + attacker.getName()
            )));

            System.out.println("Attacker wins!");
            venue.getVault().robbery(attacker, defender);


        } else {
            System.out.println("Defender wins!");

            venueStats.upgradeLevel();
            attackerStats.getOffense().decreaseLevel();

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Robbery failed. Offense level decreased."));
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Successfully defended a robbery. Defense level increased."));

        }

        executeLoyaltyChanges();

    }

    private void executeLoyaltyChanges() {
        attacker.getLoyaltyManager().addEnemy(defender);
        defender.getLoyaltyManager().addEnemy(attacker);

        attacker.getLoyaltyManager().addEnemiesEnemiesAsAllies(defender);
        attacker.getLoyaltyManager().addEnemiesAlliesAsEnemies(defender);

        attacker.getLoyaltyManager().addEnemiesEnemiesAsAllies(attacker);
        attacker.getLoyaltyManager().addEnemiesAlliesAsEnemies(attacker);
    }

    private boolean battle(int effectiveAttackerOffense, int effectiveDefenderDefense) {
        double baseSuccessChance = 0.5;
        double statDifference = effectiveAttackerOffense - effectiveDefenderDefense;
        double successChanceModifier = statDifference * 0.05;
        double finalSuccessChance = Math.min(1, Math.max(0, baseSuccessChance + successChanceModifier));

        double randomValue = Math.random();
        boolean attackerWins;
        attackerWins = randomValue < finalSuccessChance;
        return attackerWins;
    }


}
