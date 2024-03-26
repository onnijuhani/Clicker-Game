package model.characters.ai.actions;

import model.characters.Character;
import model.characters.*;
import model.characters.ai.Aspiration;
import model.characters.ai.actionCircle.WeightedObject;
import model.characters.combat.CombatService;
import model.stateSystem.State;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CombatActions {
    private final Random random;
    private final Person person;
    private Character mainTarget;
    private final Predicate<Person> isInBattle = person -> person.hasState(State.IN_BATTLE);
    private final List<WeightedObject> allActions = new LinkedList<>();



    public CombatActions(Person person) {
        this.person = person;
        this.random = new Random();
        createAllActions();
    }

    private void createAllActions() {
        Duel duel = new Duel(1);
        allActions.add(duel);
    }

    public List getAllActions() {
        return allActions;
    }


    /**
     * select a Set of possible targets, then call executeDuel method.

     */
    class Duel extends WeightedObject implements NPCAction{

        public Duel(int weight) {
            super(weight);
        }
        @Override
        public void execute() {
            if(isInBattle.test(person)){
                return;
            }
            defaultSkip();
        }
        /**
         * default is to attack random enemy
         */
        @Override
        public void defaultAction() {
            // Get all enemies
            Set<Person> enemies = person.getRelationsManager().getEnemies();
            // Exclude enemies already defeated by the person
            Set<Person> listOfPossibleTargets = enemies.stream()
                    .filter(enemy -> !person.getRelationsManager().getListOfDefeatedPersons().contains(enemy))
                    .collect(Collectors.toSet());

            executeDuel(listOfPossibleTargets);
        }

        /**
         * liberals will duel members of Slaver Guild
         */
        public void liberalAction(){
            Set<Person> listOfPossibleTargets = person.getRole().getNation().getSlaverGuild();
            executeDuel(listOfPossibleTargets);
        }
        @Override
        public void defaultSkip() {

            if(person.getAspirations().contains(Aspiration.ACHIEVE_HIGHER_POSITION)){
                mainTarget = person.getRole().getAuthority().getCharacterInThisPosition();
                if(mainTarget.getPerson() == person){
                    return; // Cannot attack self
                }
                if(!(mainTarget instanceof AuthorityCharacter)){
                    return; // quick return if there is a problem
                }
                roadToAchieveHigherPosition();
            }

        }

        private void roadToAchieveHigherPosition(){
            if (mainTarget == null) {
                return;
            }
            Status mainTargetStatus = mainTarget.getRole().getStatus();


            switch (mainTargetStatus) {
                case Captain, Mayor:
//                     Increase offense if target is Captain or Mayor
                    person.getCombatStats().upgradeOffenseWithGold();
                    CombatService.executeAuthorityBattle(person.getCharacter(),mainTarget);
                    break;

                case Governor, King:

                    // Defeat all sentinels if target is Governor or King before attempting the Authority battle
                    if (defeatAllSentinels()) {

                        person.getCombatStats().upgradeOffenseWithGold();

                        int defensePower = mainTarget.getPerson().getCombatStats().getDefenseLevel() + mainTarget.getPerson().getProperty().getDefense().getUpgradeLevel();
                        int attackPower = person.getCombatStats().getOffenseLevel() + person.getCombatStats().getDefenseLevel();

                        // Must be 2 levels higher unless they have trait Aggressive. Aggressive always attacks.
                        if (attackPower >= defensePower+2){
                            if(!(attackPower <= defensePower && person.getAiEngine().getProfile().containsKey(Trait.Aggressive))) {
                                return;
                            }
                        }

                        CombatService.executeAuthorityBattle(person.getCharacter(),mainTarget);

                    } else {
                        person.getCombatStats().upgradeOffenseWithGold();
                    }
                    break;

                default:
                    System.out.println("No specific action for " + mainTargetStatus);
                    break;
            }
        }
        private boolean defeatAllSentinels() {
            // Get all sentinels of the main target
            Set<Person> sentinels = mainTarget.getPerson().getRelationsManager().getListOfSentinels();

            Set<Person> sentinelsCopy = new HashSet<>(sentinels); // prevent ConcurrentModificationException

            Set<Person> undefeatedSentinels = new HashSet<>(); // sentinels that still need to be defeated

            for (Person sentinel : sentinelsCopy) {
                if(!person.getRelationsManager().getListOfDefeatedPersons().contains(sentinel)){
                    undefeatedSentinels.add(sentinel);
                }
            }
            if(!undefeatedSentinels.isEmpty()) {
                executeDuel(undefeatedSentinels);
            }


            return undefeatedSentinels.isEmpty(); // return true if all sentinels have been defeated already

        }


        // remember: both offense and defense are included in DUEL. Not in other combats.
        private void executeDuel(Set<Person> listOfPossibleTargets) {
            if (listOfPossibleTargets.isEmpty()) {
                return;
            }

            // Find the minimum sum of defense and offense levels among undefeated enemies
            int minCombatSum = listOfPossibleTargets.stream()
                    .mapToInt(enemy -> enemy.getCombatStats().getDefenseLevel() + enemy.getCombatStats().getOffenseLevel())
                    .min()
                    .orElse(Integer.MAX_VALUE);

            int attackPower = person.getCombatStats().getOffenseLevel() + person.getCombatStats().getDefenseLevel();

            // Must be 2 levels higher unless they have trait Aggressive
            if (attackPower >= minCombatSum+2){
                if(!(attackPower <= minCombatSum && person.getAiEngine().getProfile().containsKey(Trait.Aggressive))) {
                    return;
                }
            }

            // Filter undefeated enemies to those with the minimum sum of defense and offense levels
            List<Person> weakestTargets = listOfPossibleTargets.stream()
                    .filter(enemy -> (enemy.getCombatStats().getDefenseLevel() + enemy.getCombatStats().getOffenseLevel()) == minCombatSum)
                    .collect(Collectors.toList());

            if (weakestTargets.isEmpty()) {
                return;
            }

            // Shuffle the list of weakest undefeated enemies and pick the first one
            Collections.shuffle(weakestTargets);
            Person randomlySelectedEnemy = weakestTargets.get(0);

            // THIS MUST BE HERE TO UPGRADE THE ATTACK LEVEL ! REMOVE THIS EVENTUALLY !
            person.getCombatStats().upgradeOffenseWithGold();



            // Execute duel against the selected weakest undefeated enemy
            CombatService.executeDuel(person.getCharacter(), randomlySelectedEnemy.getCharacter());
        }
    }


    private Person getRandomPersonFromSet() {
        Person[] array = person.getRelationsManager().getEnemies().toArray(new Person[0]);
        if (array.length == 0) {
            return null;
        }
        int randomIndex = random.nextInt(array.length);
        return array[randomIndex];
    }


    /**
     Execute only if attack level is way higher than what the authority has.
     Often should be skipped.

     ALWAYS skip if they are Loyal, instead add authority as ally every time here (in case they are not for some reason)
     If they are disloyal or ambitious, CALL INCREASE ATTACK POWER IF THEY ARE WEAK AND ALSO UPGRADE GOLD MINE.

     if Liberal has Slaver as authority, they should always increase attack power and upgrade gold production.
     Liberal and Slaver if they are Authority themselves, should always upgrade Defences here UNLESS they are also Ambitious and Aggressive,
     then increase attack.

     Unambitious should Vault resources here.

     Skipping here should improve either personal or property defence.
     */
    class AuthorityBattle implements NPCAction{


        @Override
        public void execute() {

        }

        @Override
        public void defaultAction() {

        }

        @Override
        public void defaultSkip() {

        }

        @Override
        public int compareTo(NPCAction o) {
            return 0;
        }
    }


    /**
     EVERYONE SHOULD CONSIDER THIS IF THEY ARE LOW ON RESOURCES AND THERE IS GOOD TARGET AVAILABLE
     Aggressive attacks here everytime no matter what.
     Passive only considers this if they are very low on resources.
     Disloyal targets allies (if he has any)

     SKIPPING =  INCREASE PROPERTY DEFENCE
     */
    class Robbery implements NPCAction{


        @Override
        public void execute() {

        }

        @Override
        public void defaultAction() {

        }

        @Override
        public void defaultSkip() {

        }

        @Override
        public int compareTo(NPCAction o) {
            return 0;
        }
    }



}





