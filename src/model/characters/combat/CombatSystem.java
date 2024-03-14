package model.characters.combat;

import model.buildings.Property;
import model.characters.Character;
import model.characters.GameEvent;
import model.stateSystem.Event;
import model.stateSystem.EventTracker;
import model.shop.UpgradeSystem;
import model.stateSystem.State;
import model.time.TimeEventManager;

public class CombatSystem {
    private final Character attacker;
    private final CombatStats attackerStats;
    private final Character defender;
    private final CombatStats defenderStats;
    private final Property venue;
    private final UpgradeSystem venueStats;

    public CombatSystem(Character attacker, Character defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.venue = defender.getProperty();
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

        if (attacker.getState() == State.IN_BATTLE || venue.getState() == State.IN_BATTLE) {
            System.out.println("Either attacker or property is already in a battle.");
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Either attacker or property is already in a battle. Action not allowed."));
            return; // Can not enter battle
        }

        if (attacker.getLoyaltyManager().isAlly(defender)) {
            System.out.println("Cannot attack allies!");
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Attempted to rob " + defender.getProperty().getName() +
                            " owned by ally " + defender.getName() + ". Action not allowed."));
            return; // Abort the robbery because the defender is an ally
        }

        attacker.setState(State.IN_BATTLE);
        venue.setState(State.IN_BATTLE);

        int daysUntilEvent = 30;

        GameEvent gameEvent = new GameEvent(Event.ROBBERY,attacker,defender);



        attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Robbery Started"));
        defender.getEventTracker().addEvent(EventTracker.Message("Major", "You are being robbed by "+ attacker));

        TimeEventManager.scheduleEvent(this::decideRobbery, daysUntilEvent, gameEvent);
    }

    private void decideRobbery() {
        int effectiveAttackerOffense = attackerStats.getOffense().getUpgradeLevel();
        System.out.println("effectiveAttackerOffense "+effectiveAttackerOffense);
        int effectiveDefenderDefense = venueStats.getUpgradeLevel();
        System.out.println("effectiveDefenderDefense "+effectiveDefenderDefense);

        boolean attackerWins = battle(effectiveAttackerOffense, effectiveDefenderDefense);

        if (attackerWins) {
            System.out.println("Attacker wins!");

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Robbed the "
                            + defender.getProperty().getName() + " vault"));

            defender.getEventTracker().addEvent((EventTracker.Message(
                    "Major", "You have been robbed by " + attacker.getName()
            )));


            venue.getVault().robbery(attacker, defender);

        } else {
            System.out.println("Defender wins!");

            venueStats.upgradeLevel();
            attackerStats.getOffense().decreaseLevel();

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Robbery failed. Offense level decreased."));
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Successfully defended a robbery. Property defense increased."));

        }
        executeLoyaltyChanges();
        attacker.setState(State.NONE);
        venue.setState(State.NONE);
    }


    /*
    Duel is a battle type where there is no resource exchange at all. Doesn't affect positions unlike authoritybattle.
    Attacker will increase/decrease offence while Defender same with defence.
     Can be used to cheaply increase offence stats, but is risky. Loss adds -2 levels on offence. Gains "defeated" mark for attacker but NOT for defender.
     */
    public void Duel() {

        if (attacker.getCombatStats().getOffenseLevel() < 3) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Attack level must be 3 or higher to duel."));
            return; // Must be at least level 3 attack
        }

        if (attacker.getState() == State.IN_BATTLE || defender.getState() == State.IN_BATTLE) {
            System.out.println(attacker.getState());
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Either attacker or defender is already in a battle. Action not allowed."));
            return; // Can not enter battle
        }

        if (attacker.getLoyaltyManager().isAlly(defender)) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Attempted to duel " + defender +
                            " who is your ally " + ". Action not allowed."));
            return; // Abort the duel because the defender is an ally
        }

        attacker.setState(State.IN_BATTLE);
        defender.setState(State.IN_BATTLE);

        int daysUntilEvent = 15;

        GameEvent gameEvent = new GameEvent(Event.DUEL,attacker,defender);



        attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Duel Started"));
        defender.getEventTracker().addEvent(EventTracker.Message("Major", "You are being attacked (duel) by "+ attacker));

        TimeEventManager.scheduleEvent(this::decideDuel, daysUntilEvent, gameEvent);
    }

    private void decideDuel() {
        int effectiveAttackerOffense = attackerStats.getOffenseLevel() + attackerStats.getDefenseLevel();

        int effectiveDefenderDefense = defenderStats.getOffenseLevel() + defenderStats.getDefenseLevel();


        boolean attackerWins = battle(effectiveAttackerOffense, effectiveDefenderDefense);

        if (attackerWins) {

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Won the duel against " + defender));

            defender.getEventTracker().addEvent((EventTracker.Message(
                    "Major", "You were defeated by " + attacker
            )));
            attacker.getCombatStats().increaseOffence();
            defender.getCombatStats().decreaseDefence();

            attacker.getLoyaltyManager().addDefeatedCharacter(defender);


        } else {

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Duel Lost. Offense level decreased by 2 levels."));
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Duel won. Defense level increased."));

            attacker.getCombatStats().decreaseOffense(); // rework these methods eventually to take input of how many levels should be affected
            attacker.getCombatStats().decreaseOffense();
            defender.getCombatStats().increaseDefence();
            System.out.println("häviö");
        }
        executeLoyaltyChanges();
        attacker.setState(State.NONE);
        defender.setState(State.NONE);
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
