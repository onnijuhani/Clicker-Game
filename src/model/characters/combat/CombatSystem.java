package model.characters.combat;

import model.Model;
import model.buildings.Property;
import model.characters.Character;
import model.characters.Peasant;
import model.characters.Person;
import model.characters.Support;
import model.characters.authority.Authority;
import model.characters.npc.Governor;
import model.characters.npc.King;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.shop.UpgradeSystem;
import model.stateSystem.Event;
import model.stateSystem.EventTracker;
import model.stateSystem.GameEvent;
import model.stateSystem.State;
import model.time.EventManager;
import model.time.GenerateManager;

import java.util.*;

public class CombatSystem {
    private final Person attacker;
    private final CombatStats attackerStats;
    private final Person defender;
    private final CombatStats defenderStats;
    private final Property venue;
    private final UpgradeSystem venueStats;
    static int myBreakpointCounter = 0;

    List<Person> eligibleSupporters = new ArrayList<>();

    public CombatSystem(Character attacker, Character defender) {
        this.attacker = attacker.getPerson();
        this.defender = defender.getPerson();
        this.venue = defender.getPerson().getProperty();
        this.attackerStats = attacker.getPerson().getCombatStats();
        this.defenderStats = defender.getPerson().getCombatStats();
        this.venueStats = venue.getDefense();
    }


    /**
     AuthorityBattle is the main battle type where player can achieve better position at the game world.
     */
    public void authorityBattle() {

        if (IsLevelHighEnough(4)) return; // Must be at least level 4 attack

        if (attacker.hasState(State.IN_BATTLE) || defender.hasState(State.IN_BATTLE) || venue.hasState(State.IN_BATTLE)) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Either attacker or property is already in a battle. \nAction not allowed."));
            return; // Can not enter battle
        }

        if (attacker.getRole().getAuthority().getCharacterInThisPosition() != defender.getCharacter()) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "You may only challenge your direct authority"));
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

            attacker.addState(State.IN_BATTLE);
            defender.addState(State.IN_BATTLE);
            venue.addState(State.IN_BATTLE);
            eligibleSupporters.forEach(support -> support.addState(State.IN_BATTLE));

            int daysUntilEvent = 1;

            List<Character> participants = new ArrayList<>(eligibleSupporters.stream().map(Person::getCharacter).toList());
            participants.add(attacker.getCharacter());
            participants.add(defender.getCharacter());
            Character[] participantsArray = participants.toArray(new Character[0]);

            GameEvent gameEvent = new GameEvent(Event.AuthorityBattle, participantsArray);

            attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Challenging the Authority of " + defender));
            defender.getEventTracker().addEvent(EventTracker.Message("Major", "Your Authority is being challenged by " + attacker));
            eligibleSupporters.forEach(support -> support.getEventTracker().addEvent(EventTracker.Message("Major", "Joined " + defender + " in authority battle against " + attacker)));
            EventManager.scheduleEvent(this::decideAuthorityBattle, daysUntilEvent, gameEvent);
    }

    private boolean IsLevelHighEnough(int requiredLevel) {
        if (attacker.getCombatStats().getOffenseLevel() < requiredLevel) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Attack level must be " + requiredLevel +" or higher to enter this fight."));
            return true;
        }
        return false;
    }

    private void decideAuthorityBattle() {

        int totalAttackerOffense = attacker.getCombatStats().getOffenseLevel();

        int totalDefenderDefense = defender.getCombatStats().getDefenseLevel() + venue.getDefense().getUpgradeLevel() +
                eligibleSupporters.stream()
                        .mapToInt(support -> support.getCombatStats().getDefenseLevel())
                        .sum();

        System.out.println("Total Attack: "+ totalAttackerOffense);
        System.out.println("Total Defender Defense: " + totalDefenderDefense);


        boolean attackerWins = battle(totalAttackerOffense, totalDefenderDefense);

        if (attackerWins) {
            System.out.println("Attacker wins!");

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Authority of " + defender.getName() + " has been overtaken."));
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Your Authority has been overtaken by " + attacker.getName()));

            attacker.getRelationsManager().addVictory(defender);

            switchPositions();




        } else {
            System.out.println("Defender wins!");

            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Failed to challenge the Authority of \n" + defender.getName() + ". Your power has been decreased."));
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Successfully defended against\nthe Authority challenge by " + attacker.getName() + ".\nYour power has increased."));


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

            attacker.decreaseOffense(3);
            attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Offense decreased by 3 levels"));

            executeLoyaltyChanges();
        }
        // Reset states
        resetBattleStates();
    }

    private void switchPositions() {
        /*
        ALL THE CONNECTION NEED TO BE CHANGED. DO NOT CHANGE THE ORDER OF THESE SETTERS
         */
        rearrangeConnections();


        // WORK WALLET MUST BE UPDATED.
        attacker.getRole().getPosition().setWorkWallet(attacker.getWorkWallet());
        if(!(defender.getCharacter() instanceof Peasant)){
            defender.getRole().getPosition().setWorkWallet(defender.getWorkWallet());
        }

        // MODEL MUST BE UPDATED TO KNOW THE OBJECTS OF PLAYER.
        Model.updatePlayer();



        //QUARTER MUST BE UPDATED
        attacker.getProperty().getLocation().updateEverything();
        defender.getProperty().getLocation().updateEverything();

        //GENERALS MUST BE UPDATED
        attacker.getRole().getNation().updateGenerals();
        defender.getRole().getNation().updateGenerals();

        //RELATIONS MUST BE UPDATED
        attacker.getPerson().getRelationsManager().updateSets();
        defender.getPerson().getRelationsManager().updateSets();

        attacker.getEventTracker().addEvent(EventTracker.Message(
                "Major", "You are now the " +
                        attacker.getRole().getStatus() +
                        " in the \n\t\t\t\t\t"+
                        attacker.getRole().getPosition().getAreaUnderAuthority()+ " "+
                        attacker.getRole().getPosition().getAreaUnderAuthority().getClass().getSimpleName()
                ));

        // LOSING AUTHORITY POSITION COMPLETELY GRANTS MASSIVE EMPLOYMENT STATS
        LoseAuthorityPosition();

    }

    private void rearrangeConnections() {
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

    private void LoseAuthorityPosition() {
        if (defender.getCharacter() instanceof Peasant)    {
            ((Peasant) defender.getCharacter()).createEmployment(
                    model.Settings.getInt("farmerGenerate")*2,
                    model.Settings.getInt("minerGenerate")*2,
                    model.Settings.getInt("merchantGenerate")*4,
                    defender.getWorkWallet()
            );
            GenerateManager.subscribe((Peasant) defender.getCharacter());
            // TAX RATE WILL ALSO BE LOWER
            attacker.getRole().getPosition().getTaxForm().setTaxInfo(Resource.Food,25);
            attacker.getRole().getPosition().getTaxForm().setTaxInfo(Resource.Alloy,25);
            attacker.getRole().getPosition().getTaxForm().setTaxInfo(Resource.Gold,15);
        }
    }


    private void resetBattleStates() {
        attacker.addState(State.NONE);
        defender.addState(State.NONE);
        venue.addState(State.NONE);
        eligibleSupporters.forEach(support -> support.addState(State.NONE));
    }


    /**
     robbery is about stealing resources from property vault. Only resources and relationships are affected.
     */
    public void robbery() {

        if (IsLevelHighEnough(2)) return; // Must be at least level 2 attack

        if (attacker.hasState(State.IN_BATTLE) || venue.hasState(State.IN_BATTLE)) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Either attacker or property is already in a battle. \nAction not allowed."));
            return; // Can not enter battle
        }

        if (attacker.getRelationsManager().isAlly(defender)) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Attempted to rob \n" + defender.getProperty().getName() +
                            " owned by ally " + defender.getName() + ". \nAction not allowed."));
            return; // Abort the robbery because the defender is an ally
        }

        attacker.addState(State.IN_BATTLE);
        venue.addState(State.IN_BATTLE);

        int daysUntilEvent = 30;

        GameEvent gameEvent = new GameEvent(Event.ROBBERY,attacker.getCharacter(),defender.getCharacter());

        attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Robbery Started"));
        defender.getEventTracker().addEvent(EventTracker.Message("Major", "You are being robbed by "+ attacker));

        EventManager.scheduleEvent(this::decideRobbery, daysUntilEvent, gameEvent);
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
        } else {
            venueStats.upgradeLevel();
            attackerStats.getOffense().decreaseLevel();
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Robbery failed. \nOffense level decreased."));
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Successfully defended a robbery. \nProperty defense increased."));
        }
        executeLoyaltyChanges();
        attacker.addState(State.NONE);
        venue.addState(State.NONE);
    }


    /**
    Duel is a battle type where there is no resource exchange at all. Doesn't affect positions unlike authorityBattle.
     Both offense and defense levels are used for both characters. Venue is not included.
    Attacker might increase/decrease offence while Defender same with defence.
     Can be used to cheaply increase offence stats, but is risky. Gains "defeated" mark for attacker but NOT for defender.
     */
    public void Duel() {

        if (IsLevelHighEnough(3)) return; // Must be at least level 3 attack

        if (attacker.hasState(State.IN_BATTLE) || defender.hasState(State.IN_BATTLE)) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Either attacker or defender is already in a battle. \nAction not allowed."));
            return; // Can not enter battle
        }

        if (attacker.getRelationsManager().isAlly(defender)) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Error", "Attempted to duel \n" + defender +
                            " who is your ally " + ". \nAction not allowed."));
            return; // Abort the duel because the defender is an ally
        }

        attacker.addState(State.IN_BATTLE);
        defender.addState(State.IN_BATTLE);

        int daysUntilEvent = 15;

        GameEvent gameEvent = new GameEvent(Event.DUEL,attacker.getCharacter(),defender.getCharacter());



        attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Duel Started"));
        defender.getEventTracker().addEvent(EventTracker.Message("Major", "You are being attacked (duel) by \n"+ attacker));

        EventManager.scheduleEvent(this::decideDuel, daysUntilEvent, gameEvent);
    }

    private void decideDuel() {
        int effectiveAttackerOffense = attackerStats.getOffenseLevel() + attackerStats.getDefenseLevel() / 2;

        int effectiveDefenderDefense = defenderStats.getOffenseLevel() + defenderStats.getDefenseLevel() / 2;


        boolean attackerWins = battle(effectiveAttackerOffense, effectiveDefenderDefense);

        Random random = new Random();

        if (attackerWins) {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Won the duel against " + defender));

            defender.getEventTracker().addEvent((EventTracker.Message(
                    "Major", "You were defeated by " + attacker
            )));

            if (random.nextInt(100) < 5){
                attacker.getCombatStats().increaseOffence();
                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Minor", "Offense increased for winning the Duel"));
            }
            if (random.nextInt(100) < 5){
                defender.getCombatStats().decreaseDefence();
                defender.getEventTracker().addEvent((EventTracker.Message(
                        "Minor", "Defence decreased for losing the Duel"
                )));
            }
            attacker.getRelationsManager().addVictory(defender);
        } else {
            attacker.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Duel Lost. \nOffense level decreased by 2 levels."));
            defender.getEventTracker().addEvent(EventTracker.Message(
                    "Major", "Duel won. \nDefense level increased."));

            if (random.nextInt(100) < 10) {
                attacker.getCombatStats().decreaseOffense();
                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Minor", "Offense decreased for losing the Duel"));
            } else if (random.nextInt(100) < 5) {
                attacker.getCombatStats().decreaseOffense();
                attacker.getCombatStats().decreaseOffense();
                attacker.getEventTracker().addEvent(EventTracker.Message(
                        "Minor", "Offense decreased by 2 for losing the Duel"));
            }
            if (random.nextInt(100) < 20) {
                defender.getCombatStats().increaseDefence();
                defender.getEventTracker().addEvent(EventTracker.Message(
                        "Minor", "Defense increased for winning the Duel"));
            }
        }
        executeLoyaltyChanges();
        attacker.addState(State.NONE);
        defender.addState(State.NONE);
    }


    private void executeLoyaltyChanges() {
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

        // Base success chance still starts at 0.5
        double baseSuccessChance = 0.5;

        // Calculate stat difference
        double statDifference = effectiveAttackerOffense - effectiveDefenderDefense;

        // Use a non-linear map for offense and defense, considering a simple power law for illustration
        double mappedAttackerOffense = mapValueWithPowerLaw(effectiveAttackerOffense, 1, 10);
        double mappedDefenderDefense = mapValueWithPowerLaw(effectiveDefenderDefense, 1, 10);

        // Calculate success chance modifier based on non-linear scaling
        double successChanceModifier = calculateSuccessChanceModifier(statDifference, mappedAttackerOffense, mappedDefenderDefense);

        // Incorporate a Gaussian distribution based random modifier
        double randomModifier = rand.nextGaussian() * 0.1; // Standard deviation of 0.1 for randomness

        // Calculate final success chance with applied non-linear modifiers
        double finalSuccessChance = calculateFinalSuccessChance(baseSuccessChance, successChanceModifier, randomModifier);
        System.out.println(finalSuccessChance);

        // Determine the battle outcome
        boolean attackerWins = Math.random() < finalSuccessChance;
        return attackerWins;
    }

    private double mapValueWithPowerLaw(int value, int fromMin, int fromMax) {
        // Example with square root to reduce impact as value increases, making high values less dominant
        return Math.sqrt((double)(value - fromMin) / (fromMax - fromMin));
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
