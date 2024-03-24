package model.characters.ai.actions;

import model.characters.Person;
import model.characters.combat.CombatService;

import java.util.*;
import java.util.stream.Collectors;

public class CombatActions {
    private final Random random;
    private final Person person;
    private final PriorityQueue<NPCAction> actionQueue = new PriorityQueue<>();
    private Person mainTarget;

    public CombatActions(Person person) {
        this.person = person;
        this.random = new Random();
        Duel duel = new Duel();
        actionQueue.add(duel);
    }

    public void execute(){
        NPCAction next = actionQueue.poll();
        assert next != null;
        next.execute();
        actionQueue.add(next);

    }



    /**
     Should select random enemy to duel but only if there can be found enemy what is weaker than what they are.
     IF THEY ARE TOO WEAK TO ATTACK ANYONE, CALL INCREASE PERSONAL ATTACK POWER METHOD EVERY TIME!!!
     if there are no enemies SKIP unless they are Aggressive, then always attack randomly. If they are also Attackers, attack no matter what.
     if they are Liberal they should always select Slaver as their opponent.
     if they are Disloyal, attack allies, superiors and characters in their own city. Disloyal should be able to do attack allies!!!
     if they are Passive, they should skip this one ALWAYS unless they are also Slaver, Ambitious or Attacker, then have small chance of attack
     if they are defender, they should often skip this one unless they are also Slaver, Ambitious or Aggressive, then have higher chance of attack

     Skipping default = increase personal defence
     */
    class Duel implements NPCAction{
        int importanceLevel = 0;

        @Override
        public void execute() {
            defaultAction();

        }


        /**
         * default is to attack random enemy
         */
        @Override
        public void defaultAction() {
            // Get all enemies
            Set<Person> enemies = person.getRelationsManager().getEnemies();
            // Exclude enemies already defeated by the person
            Set<Person> undefeatedEnemies = enemies.stream()
                    .filter(enemy -> !person.getRelationsManager().getListOfDefeatedPersons().contains(enemy))
                    .collect(Collectors.toSet());

            if (undefeatedEnemies.isEmpty()) {
                return; // Early return if there are no undefeated enemies
            }

            // Find the minimum sum of defense and offense levels among undefeated enemies
            int minCombatSum = undefeatedEnemies.stream()
                    .mapToInt(enemy -> enemy.getCombatStats().getDefenseLevel() + enemy.getCombatStats().getOffenseLevel())
                    .min()
                    .orElse(Integer.MAX_VALUE);

            // Filter undefeated enemies to those with the minimum sum of defense and offense levels
            List<Person> weakestUndefeatedEnemies = undefeatedEnemies.stream()
                    .filter(enemy -> (enemy.getCombatStats().getDefenseLevel() + enemy.getCombatStats().getOffenseLevel()) == minCombatSum)
                    .collect(Collectors.toList());

            if (weakestUndefeatedEnemies.isEmpty()) {
                return; // Double-check to ensure the list is not empty
            }

            // Shuffle the list of weakest undefeated enemies and pick the first one
            Collections.shuffle(weakestUndefeatedEnemies);
            Person randomlySelectedEnemy = weakestUndefeatedEnemies.get(0);

            // THIS MUST BE HERE TO UPGRADE THE ATTACK LEVEL ! REMOVE THIS EVENTUALLY !
            person.getCombatStats().upgradeOffenseWithGold();

            // Execute duel against the selected weakest undefeated enemy
            CombatService.executeDuel(person.getCharacter(), randomlySelectedEnemy.getCharacter());
        }


        @Override
        public void defaultSkip() {

        }

        @Override
        public int compareTo(NPCAction o) {
            return importanceLevel;
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





