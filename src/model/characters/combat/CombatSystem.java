package model.characters.combat;

import model.Model;
import model.Settings;
import model.buildings.Property;
import model.characters.Character;
import model.characters.*;
import model.characters.ai.Aspiration;
import model.characters.authority.Authority;
import model.characters.npc.Governor;
import model.characters.npc.King;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.WorkWallet;
import model.shop.UpgradeSystem;
import model.stateSystem.Event;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.State;
import model.time.EventManager;
import model.time.Time;
import model.worldCreation.Area;

import java.util.*;

public class CombatSystem {
    private final Person attacker;
    private final CombatStats attackerStats;
    private final Person defender;
    private final CombatStats defenderStats;
    private final Property venue;
    private final UpgradeSystem venueStats;
    static int myBreakpointCounter = 0; // used for debugging

    List<Person> eligibleSupporters = new ArrayList<>();

    public CombatSystem(Character attacker, Character defender) {
        this.attacker = attacker.getPerson();
        this.defender = defender.getPerson();
        this.venue = defender.getPerson().getProperty();
        this.attackerStats = attacker.getPerson().getCombatStats();
        this.defenderStats = defender.getPerson().getCombatStats();
        this.venueStats = venue.getDefenceStats();
    }


    /**
     AuthorityBattle is the main battle type where player can achieve better position at the game world.
     */
    public void authorityBattle() {

        if (IsLevelHighEnough(4)) return; // Must be at least level 4 to attack

        if (attacker.hasState(State.IN_BATTLE) || defender.hasState(State.IN_BATTLE) || venue.hasState(State.IN_BATTLE)) {
            if(attacker.isPlayer()) {
                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Error", "Either attacker or property is already in a battle. \nAction not allowed."));
            }
            return; // Can not enter battle
        }

        if (attacker.getRole().getAuthority().getCharacterInThisPosition() != defender.getCharacter()) {
            if(attacker.isPlayer()) {
                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Error", "You may only challenge your direct authority"));
            }
            return;
        }

            if (defender.getCharacter() instanceof Governor) {
                Authority position = ((Governor) defender.getCharacter()).getAuthorityPosition(); // what position is challenged

                ArrayList<Support> supporters = position.getSupporters(); // get governor's mercenaries
                Set<Person> defeatedCharactersSet = attacker.getRelationsManager().getListOfDefeatedPersons(); // characters that attacker has defeated

                eligibleSupporters = supporters.stream()
                        .map(Support::getPerson)
                        .filter(Objects::nonNull)
                        .filter(person -> !defeatedCharactersSet.contains(person))
                        .toList();
            }
            if (defender.getCharacter() instanceof King) {
                Authority position = ((King) defender.getCharacter()).getAuthorityPosition(); // what position is challenged

                ArrayList<Support>  supporters = position.getSupporters(); // get king's Vanguards and Nobles
                Set<Person> defeatedCharactersSet = attacker.getRelationsManager().getListOfDefeatedPersons(); // characters that attacker has defeated

                eligibleSupporters = supporters.stream() //get the Vanguards and Nobles that can actually join
                        .map(Support::getPerson)
                        .filter(Objects::nonNull)
                        .filter(person -> !defeatedCharactersSet.contains(person))
                        .toList();
            }
            makeEnemies();
            attacker.addState(State.IN_BATTLE);
            defender.addState(State.IN_BATTLE);
            venue.addState(State.IN_BATTLE);
            eligibleSupporters.forEach(support -> support.addState(State.IN_BATTLE));

            int daysUntilEvent = getDaysUntilEvent("authorityBattleTime");

            List<Person> participants = new ArrayList<>(eligibleSupporters.stream().map(Person::getPerson).toList());
            participants.add(attacker);
            participants.add(defender);
            Person[] participantsArray = participants.toArray(new Person[0]);

            GameEvent gameEvent = new GameEvent(Event.AuthorityBattle, participantsArray);


            attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Challenging the Authority of " + defender.getCharacter()));
            defender.getEventTracker().addEvent(EventTracker.Message("Major", "Your Authority is being challenged by " + attacker.getCharacter()));


            eligibleSupporters.forEach(support -> support.getEventTracker().addEvent(EventTracker.Message("Major", "Joined " +
                    defender.getCharacter() +
                    "\nin authority battle against " +
                    attacker.getCharacter())));
            EventManager.scheduleEvent(this::decideAuthorityBattle, daysUntilEvent, gameEvent);
    }

    private boolean IsLevelHighEnough(int requiredLevel) {
        if (attacker.getCombatStats().getOffenseLevel() < requiredLevel) {
            if(attacker.isPlayer()) {
                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Error", "Attack level must be " + requiredLevel + " or higher to enter this fight."));
            }
            return true;
        }
        return false;
    }

    private void decideAuthorityBattle() {

        int totalAttackerOffense = attacker.getCombatStats().getOffenseLevel();

        int totalDefenderDefense = defender.getCombatStats().getDefenseLevel() +
                eligibleSupporters.stream()
                        .mapToInt(support -> support.getCombatStats().getDefenseLevel())
                        .sum();

        boolean attackerWins = battle(totalAttackerOffense, totalDefenderDefense);

        if (attackerWins) {

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Authority of " + defender.getCharacter() + " has been overtaken."));
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Your Authority has been overtaken by " + attacker.getCharacter()));

            attacker.getRelationsManager().processResults(defender);


            // TODO remove this
            if(Settings.DB) {
                System.out.println("Attacker won " + attacker.getCharacter() +
                        " " + defender.getCharacter() + "\n" + attacker.getProperty().getLocation().getFullHierarchyInfo());
            }

            switchPositions();

        } else {

                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Major", "Failed to challenge the Authority of \n" + defender.getCharacter() + ". Your power has been decreased."));
                defender.getEventTracker().addEvent(EventTracker.Message(
                        "Major", "Successfully defended against\nthe Authority challenge by " + attacker.getCharacter()+ ".\nYour power has increased."));


            String compensationFromWallet = TransferPackage.fromArray(attacker.getWallet().getWalletValues()).toString();

            defender.getWallet().depositAll(attacker.getWallet());
            TransferPackage halfVaultBalance = TransferPackage.fromArray(attacker.getProperty().getVault().getHalfValues());
            defender.getProperty().getVault().subtractResources(halfVaultBalance);
            defender.getWallet().addResources(halfVaultBalance);

            attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Paid entire wallet (" +
                    compensationFromWallet +
                    ")\nand 50% from the vault ("
                    + halfVaultBalance +
                    ")\nas compensation for disloyalty."
            ));

            attacker.decreasePersonalOffence(3);

            attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Offense decreased by 3 levels"));


            defender.getRelationsManager().processResults(attacker);


            // TODO remove this
            if(Settings.DB) {
                System.out.println("Defender won " + attacker.getCharacter() + " " +
                        defender.getCharacter() + "\n" + attacker.getProperty().getLocation().getFullHierarchyInfo());
            }

        }

        makeEnemies();
        // Reset states
        resetBattleStatesAuthorityBattle();

    }

    private void switchPositions() {
        /*
        ALL THE CONNECTIONS NEED TO BE CHANGED. !!! DO NOT CHANGE THE ORDER OF THESE SETTERS !!!
         */
        rearrangeConnections();

        Role attackerRole = attacker.getRole();
        Role defenderRole = defender.getRole();

        // WORK WALLET MUST BE UPDATED.
        attackerRole.getPosition().setWorkWallet(attacker.getWorkWallet());
        if(!(defender.getCharacter() instanceof Peasant)){
            defenderRole.getPosition().setWorkWallet(defender.getWorkWallet());
        }

        // MODEL MUST BE UPDATED TO KNOW THE INSTANCES OF PLAYER.
        if(attacker.isPlayer() || defender.isPlayer()){
            Model.updatePlayer();
        }

        //QUARTER MUST BE UPDATED
        attacker.getProperty().getLocation().updateEverything();
        defender.getProperty().getLocation().updateEverything();

        //GENERALS MUST BE UPDATED
        attackerRole.getNation().updateGenerals();
        defenderRole.getNation().updateGenerals();

        //RELATIONS MUST BE UPDATED
        attacker.getPerson().getRelationsManager().updateSets();
        defender.getPerson().getRelationsManager().updateSets();

        attacker.getEventTracker().addEvent(EventTracker.Message(
                "Major", "You are now the " +
                        attackerRole.getStatus() +
                        " in the \n"+
                        attackerRole.getPosition().getAreaUnderAuthority()+ " "+
                        attackerRole.getPosition().getAreaUnderAuthority().getClass().getSimpleName()
                ));



        Area area;
        if(!(defender.getCharacter() instanceof Peasant)){
            area = defenderRole.getPosition().getAreaUnderAuthority();
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "You are now the " +
                            defenderRole.getStatus() +
                            " in the \n"+
                            area+ " "+
                            area.getClass().getSimpleName()
            ));
        }else {
            area = defender.getProperty().getLocation();
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "You are now " +
                            defenderRole.getStatus() +
                            " in the \n"+
                            area+ " "+
                            area.getClass().getSimpleName()
            ));
        }


        loseAuthorityPosition();
    }

    private void rearrangeConnections() {
        /*
        NEVER CHANGE THE ORDER OF THESE SETTERS
         */
        attacker.setCharacter(defender.getCharacter());
        defender.setCharacter(attacker.getRole().getCharacter());

        attacker.setRole(attacker.getCharacter().getRole());
        defender.setRole(defender.getCharacter().getRole());

        attacker.getRole().setCharacter(attacker.getCharacter());
        defender.getRole().setCharacter(defender.getCharacter());


        attacker.getCharacter().setRole(attacker.getRole());
        attacker.getCharacter().setPerson(attacker);

        defender.getCharacter().setRole(defender.getRole());
        defender.getCharacter().setPerson(defender);

        attacker.getRole().setPerson(attacker);
        defender.getRole().setPerson(defender);

    }

    private void loseAuthorityPosition() {
        if (defender.getCharacter() instanceof Peasant) {
            Peasant peasantCharacter = (Peasant) defender.getCharacter();
            if (peasantCharacter == null) {
                return;
            }

            Employment employment = peasantCharacter.getEmployment();
            if (employment == null) {
                new Employment(100, 100, 50, defender.getWorkWallet());
                return;
            }

            WorkWallet defenderWorkWallet = defender.getWorkWallet();
            if (defenderWorkWallet == null) {
                return;
            }
            employment.setWorkWallet(defenderWorkWallet);
        }
    }


    private void resetBattleStatesAuthorityBattle() {
        attacker.removeState(State.IN_BATTLE);
        defender.removeState(State.IN_BATTLE);
        venue.removeState(State.IN_BATTLE);
        eligibleSupporters.forEach(support -> support.removeState(State.IN_BATTLE));
        attacker.removeAspiration(Aspiration.ACHIEVE_HIGHER_POSITION);
    }


    /**
     robbery is about stealing resources from property vault. Only resources and relationships are affected.
     */
    public void robbery() {


        if(Time.year < 1){
            if(attacker.isPlayer()){
                attacker.getEventTracker().addEvent(EventTracker.Message("Error", "Robbery is only possible after first year"));
            }
            return;
        }

        // check if attackers level is high enough
        if (IsLevelHighEnough(4)) return;

        // check if the venue is too big for the attacker
        if(venueStats.getUpgradeLevel() * 4 > attacker.getCombatStats().getOffenseLevel()) return;

        // check that no one is in battle
        if (attacker.hasState(State.IN_BATTLE) || venue.hasState(State.IN_BATTLE)) {
            if (attacker.isPlayer()) {
                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Error", "Either attacker or property is already in a battle. \nAction not allowed."));
            }
            return; // Can not enter battle
        }

        // check for loyalty issues
        if (attacker.getRelationsManager().isAlly(defender) && !attacker.getAiEngine().getProfile().containsKey(Trait.Disloyal)) {
            if (attacker.isPlayer()) {
                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Error", "Attempted to rob \n" + defender.getProperty().getName() +
                                " owned by ally " + defender.getName() + ". \nAction not allowed."));
            }
            return; // Abort the robbery because the defender is an ally and attacker is not disloyal
        }


        attacker.addState(State.IN_BATTLE);
        venue.addState(State.IN_BATTLE);
        defender.addState(State.IN_DEFENCE);

        int daysUntilEvent = getDaysUntilEvent("robberyTime");

        GameEvent gameEvent = new GameEvent(Event.ROBBERY,attacker,defender);

        attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Robbery Started"));
        defender.getEventTracker().addEvent(EventTracker.Message("Major", "You are being robbed by "+ attacker));

        EventManager.scheduleEvent(this::decideRobbery, daysUntilEvent, gameEvent);
    }

    /**
     * Having bigger Property grants longer battles
     * @param settingsGet what type of battle it is should be gotten from settings.
     * @return the time it takes to finish the battle
     */
    private int getDaysUntilEvent(String settingsGet) {

        if(Objects.equals(settingsGet, "duelTime")){  // Duel is special in a way that defenders defence Level matters rather than Venue type.
            int baseTime = Settings.getInt(settingsGet);
            int baseLevel = defender.getCombatStats().getDefenseLevel();
            return baseTime * baseLevel;
        }

        int baseTime = Settings.getInt(settingsGet);
        int baseLevel = venue.getPropertyEnum().ordinal();
        return Math.min(baseTime * baseLevel, baseTime*15);
    }

    private void decideRobbery() {
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

            venue.getVault().robbery(attacker, defender);

            defender.addAspiration(Aspiration.INCREASE_PROPERTY_DEFENCE);

        } else {

            attackerStats.getOffense().decreaseLevel();

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Robbery failed. \nOffense level decreased."));
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Successfully defended a robbery. \n"));

            attacker.addAspiration(Aspiration.INCREASE_PERSONAL_OFFENCE);

        }

        makeEnemies();

        attacker.removeState(State.IN_BATTLE);
        venue.removeState(State.IN_BATTLE);
        defender.removeState(State.IN_DEFENCE);
    }


    /**
     Duel is a battle type where there is no resource exchange at all. Doesn't affect positions unlike authorityBattle.
     Both offense and defense levels are used for both characters. Venue is not included.
     */
    public void Duel() {

        if (IsLevelHighEnough(3)) return; // Must be at least level 3 attack

        if (attacker.hasState(State.IN_BATTLE) || defender.hasState(State.IN_BATTLE)) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Either attacker or defender is already in a battle. \nAction not allowed."));
            return; // Can not enter battle
        }

        if (attacker.getRelationsManager().isAlly(defender) && !attacker.getAiEngine().getProfile().containsKey(Trait.Disloyal)) {
            if (attacker.isPlayer()) {
                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Error", "Attempted to duel \n" + defender.getName() +
                                " who is your ally " + ". \nAction not allowed."));
            }
            return; // Abort the duel because the defender is an ally and attacker is not disloyal
        }

        makeEnemies();
        attacker.addState(State.IN_BATTLE);
        defender.addState(State.IN_BATTLE);

        int daysUntilEvent = getDaysUntilEvent("duelTime");

        GameEvent gameEvent = new GameEvent(Event.DUEL,attacker,defender);

        attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Duel Started against " + defender.getCharacter()));
        defender.getEventTracker().addEvent(EventTracker.Message("Major", "You are being attacked (duel) by \n"+ attacker.getCharacter()));

        EventManager.scheduleEvent(this::decideDuel, daysUntilEvent, gameEvent);
    }

    private void decideDuel() {
        int effectiveAttackerOffense = attackerStats.getOffenseLevel() + attackerStats.getDefenseLevel() / 2;
        int effectiveDefenderDefense = defenderStats.getOffenseLevel() / 2 + defenderStats.getDefenseLevel();

        boolean attackerWins = battle(effectiveAttackerOffense, effectiveDefenderDefense);

        Random random = new Random();

        if (attackerWins) { // Usually nothing happens as duel doesn't really have a price, only defeat/victory

            attacker.getRelationsManager().processResults(defender);

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Won the duel against " + defender.getCharacter()));

            defender.getEventTracker().addEvent((EventTracker.Message(
                    "Major", "You were defeated by " + attacker.getCharacter())));

            defender.addAspiration(Aspiration.INCREASE_PERSONAL_DEFENCE);

        } else {

                defender.getRelationsManager().processResults(attacker);

                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Major", "Duel Lost against " + defender.getCharacter()));
                defender.getEventTracker().addEvent(EventTracker.Message(
                        "Major", "Duel won against " + attacker.getCharacter()));


            if (random.nextInt(100) < 10) {
                attacker.getCombatStats().decreaseOffense();
                if(attacker.isPlayer()) {
                    attacker.getEventTracker().addEvent(EventTracker.Message(
                            "Minor", "Offense decreased for losing the Duel"));
                }
            } else if (random.nextInt(100) < 5) {
                attacker.getCombatStats().decreaseOffense();
                attacker.getCombatStats().decreaseOffense();
                if(attacker.isPlayer()) {
                    attacker.getEventTracker().addEvent(EventTracker.Message(
                            "Minor", "Offense decreased by 2 for losing the Duel"));
                }
            }

            attacker.addAspiration(Aspiration.INCREASE_PERSONAL_OFFENCE);
        }
        makeEnemies();
        attacker.removeState(State.IN_BATTLE);
        defender.removeState(State.IN_BATTLE);
    }

    /**
     * adds attacker and defender as enemies of each other
     */
    private void makeEnemies() {
        attacker.getRelationsManager().addEnemy(defender);
        defender.getRelationsManager().addEnemy(attacker);
    }



    private void executeRadicalLoyaltyChanges() {
        attacker.getRelationsManager().addEnemy(defender);
        defender.getRelationsManager().addEnemy(attacker);

        attacker.getRelationsManager().addEnemiesEnemiesAsAllies(defender);
        attacker.getRelationsManager().addEnemiesAlliesAsEnemies(defender);

        attacker.getRelationsManager().addEnemiesEnemiesAsAllies(attacker);
        attacker.getRelationsManager().addEnemiesAlliesAsEnemies(attacker);
    }

    private boolean battle(int effectiveAttackerOffense, int effectiveDefenderDefense) {
        // Initialize a Random object for generating Gaussian distributed values
        Random rand = new Random();

        // Base success chance starts at 0.5
        double baseSuccessChance = 0.5;

        // Calculate stat difference
        double successChanceModifier = getSuccessChanceModifier(effectiveAttackerOffense, effectiveDefenderDefense);

        // Incorporate a Gaussian distribution based random modifier
        double randomModifier = rand.nextGaussian() * 0.1; // Standard deviation of 0.1 for randomness

        // Calculate final success chance with applied non-linear modifiers
        double finalSuccessChance = calculateFinalSuccessChance(baseSuccessChance, successChanceModifier, randomModifier);

        // Determine the battle outcome
        return Math.random() < finalSuccessChance;
    }

    private double getSuccessChanceModifier(int effectiveAttackerOffense, int effectiveDefenderDefense) {
        double statDifference = effectiveAttackerOffense - effectiveDefenderDefense;

        // Use a non-linear map for offense and defense, considering a simple power law for illustration
        double mappedAttackerOffense = mapValueWithPowerLaw(effectiveAttackerOffense);
        double mappedDefenderDefense = mapValueWithPowerLaw(effectiveDefenderDefense);

        // Calculate success chance modifier based on non-linear scaling
        return calculateSuccessChanceModifier(statDifference, mappedAttackerOffense, mappedDefenderDefense);
    }

    private double mapValueWithPowerLaw(int value) {
        // square root to reduce impact as value increases, making high values less dominant
        return Math.sqrt((double)(value - 1) / (10 - 1));
    }

    private double calculateSuccessChanceModifier(double statDifference, double mappedOffense, double mappedDefense) {
        // This example uses a simple modification, adjusting it based on your needs
        double modifier = statDifference * 0.05; // Scale the stat difference impact
        modifier += (mappedOffense - mappedDefense) * 0.1; // Enhance the impact of mapped values difference
        return modifier;
    }

    private double calculateFinalSuccessChance(double baseChance, double modifier, double randomMod) {
        // Apply all modifiers ensuring the result stays within 0 and 1
        double finalChance = baseChance + modifier + randomMod;
        finalChance = Math.max(0, Math.min(finalChance, 1)); // Clamp between 0 and 1
        return finalChance;
    }




}
