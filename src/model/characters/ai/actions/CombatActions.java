package model.characters.ai.actions;

import model.Settings;
import model.buildings.Property;
import model.characters.Character;
import model.characters.*;
import model.characters.ai.Aspiration;
import model.characters.ai.actionCircle.WeightedObject;
import model.characters.combat.CombatService;
import model.characters.combat.CombatStats;
import model.resourceManagement.Resource;
import model.stateSystem.State;
import model.time.Time;
import model.worldCreation.Nation;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CombatActions extends BaseActions {

    private Character mainTarget;
    private final Predicate<Person> isInBattle = person -> person.hasState(State.IN_BATTLE);
    private final Random random = Settings.getRandom();

    public CombatActions(Person person, NPCActionLogger npcActionLogger, Map<Trait, Integer> profile) {
        super(person, npcActionLogger, profile);
    }
    @Override
    protected void createAllActions() {
        Duel duel = new Duel(person, npcActionLogger,5,profile);
        Robbery robbery = new Robbery(person, npcActionLogger,5,profile);
        EvaluateNeeds evaluateNeeds = new EvaluateNeeds(person, npcActionLogger,5,profile);
        TakeActionOnNeeds takeActionOnNeeds = new TakeActionOnNeeds(person, npcActionLogger,5,profile);

        allActions.add(duel);
        allActions.add(robbery);
        allActions.add(evaluateNeeds);
        allActions.add(takeActionOnNeeds);
    }

    public List getAllActions() {
        return allActions;
    }

    class TakeActionOnNeeds extends WeightedObject{
        int defaultCounter = 0;

        public TakeActionOnNeeds(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        @Override
        public void execute(){
            if (resourceAbort()) return;

            defaultUpgrade();

            super.execute();

        }
        @Override
        public void defaultAction(){

            CombatStats combatStats = person.getCombatStats();
            Property property = person.getProperty();

            for (Aspiration aspiration : person.getAspirations()) {
                switch (aspiration) {
                    case INCREASE_PERSONAL_OFFENCE:
                        combatStats.upgradeOffenseWithGold();
                        person.removeAspiration(Aspiration.INCREASE_PERSONAL_OFFENCE);
                        break;
                    case INCREASE_PERSONAL_DEFENCE:
                        combatStats.upgradeDefenceWithGold();
                        person.removeAspiration(Aspiration.INCREASE_PERSONAL_DEFENCE);
                        break;
                    case INCREASE_PROPERTY_DEFENCE:
                        property.upgradeDefenceWithGold();
                        person.removeAspiration(Aspiration.INCREASE_PROPERTY_DEFENCE);
                        break;
                }
            }



        }


        /**
         * Default counter makes sure all stats get upgraded at least slowly but randomly
         */
        private void defaultUpgrade() {


            CombatStats combatStats = person.getCombatStats();
            Property property = person.getProperty();

            if (defaultCounter > 5) {

                Random random = Settings.getRandom();
                int randomNumber = random.nextInt(3);


                switch (randomNumber) {
                    case 0:
                        combatStats.upgradeOffenseWithGold();
                        break;
                    case 1:
                        combatStats.upgradeDefenceWithGold();
                        break;
                    case 2:
                        property.upgradeDefenceWithGold();
                        break;
                    default:
                        break;
                }


                defaultCounter = 0;
            } else {

                defaultCounter++;
            }
        }

    }

    class EvaluateNeeds extends WeightedObject{

        int testCounter = 0;

        public EvaluateNeeds(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }


        // default is to compare against the minimum
        @Override
        public void defaultAction() {

            CombatStats combatStats = person.getCombatStats();
            EnumSet<Aspiration> aspirations = person.getAspirations();
            Property property = person.getProperty();

            considerMinimumLevels(combatStats, aspirations, property);
            testCounter++;
        }

        @Override
        public void ambitiousAction(){
            int authorityDefenceLevel = person.getRole().getAuthority().getCharacterInThisPosition().getPerson().getCombatStats().getDefenseLevel();
            int authorityPropertyDefenceLevel = person.getRole().getAuthority().getCharacterInThisPosition().getPerson().getProperty().getDefenceStats().getUpgradeLevel();
            int totalAuthorityLevel = authorityDefenceLevel + authorityPropertyDefenceLevel;

            if(totalAuthorityLevel >= person.getCombatStats().getOffenseLevel()){
                person.addAspiration(Aspiration.INCREASE_PERSONAL_OFFENCE);
            }
        }
        @Override
        public void attackerAction(){
            person.addAspiration(Aspiration.INCREASE_PERSONAL_OFFENCE);
        }
        @Override
        public void defenderAction(){
            person.addAspiration(Aspiration.INCREASE_PERSONAL_DEFENCE);
            person.addAspiration(Aspiration.INCREASE_PROPERTY_DEFENCE);
        }

        @Override
        public void aggressiveAction(){

            Set<Person> enemies = person.getRelationsManager().getEnemies();
            int offenceLevel = person.getCombatStats().getOffenseLevel();
            int defenceLevel = person.getCombatStats().getDefenseLevel();

            int maxEnemyOffenceFound = 0;
            int maxEnemyDefenceFound = 0;

            for (Person enemy : enemies){
                CombatStats enemyStats = enemy.getCombatStats();
                if(enemyStats.getOffenseLevel() > maxEnemyOffenceFound){
                     maxEnemyOffenceFound = enemyStats.getOffenseLevel();
                }
                if(enemyStats.getDefenseLevel() > maxEnemyDefenceFound){
                    maxEnemyDefenceFound = enemyStats.getDefenseLevel();
                }
            }

            if (maxEnemyDefenceFound >= offenceLevel){
                person.addAspiration(Aspiration.INCREASE_PERSONAL_OFFENCE);
            }

            if (maxEnemyOffenceFound >= defenceLevel + 2){
                person.addAspiration(Aspiration.INCREASE_PERSONAL_DEFENCE);
            }

        }

        private static void considerMinimumLevels(CombatStats combatStats, EnumSet<Aspiration> aspirations, Property property) {
            int currentYear = Time.getYear() + 1;
            if(currentYear > 20 ) {
                currentYear = 20; // current year is a good minimum level to compare early on
            }
            int minLevelDesired = currentYear / 2;

            if(combatStats.getOffenseLevel() < minLevelDesired){
                aspirations.add(Aspiration.INCREASE_PERSONAL_OFFENCE);

            }
            if(combatStats.getDefenseLevel() < minLevelDesired){
                aspirations.add(Aspiration.INCREASE_PERSONAL_DEFENCE);
            }
            if(property.getDefenceStats().getUpgradeLevel() < minLevelDesired){
                aspirations.add(Aspiration.INCREASE_PROPERTY_DEFENCE);
            }
        }
    }

    private boolean resourceAbort() {
        return person.getAspirations().contains(Aspiration.SAVE_RESOURCES) || person.getWallet().isLowBalance();
    }


    /**
     * select a Set of possible targets, then call executeDuel method.
     */
    class Duel extends WeightedObject{


        public Duel(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        @Override
        public void execute() {
            if(isInBattle.test(person)){
                return;
            }
            super.execute();
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
        public void passiveAction(){
            defaultSkip();
        }
        public void slaverAction(){
            defaultSkip();
        }
        public void unambitiousAction(){
            defaultSkip();
        }


        @Override
        public void ambitiousAction() {

            if(random.nextInt(6) > 2) { //ambitious duels, but has small chance to road to achieve higher Position
                defaultAction();
                return;
            }

            mainTarget = person.getRole().getAuthority().getCharacterInThisPosition();
            if(mainTarget.getPerson() == person){
                return; // Cannot attack self
            }
            if(!(mainTarget instanceof AuthorityCharacter)){
                return; // quick return if there is a problem with the targets role
            }
            roadToAchieveHigherPosition();

        }

        private void roadToAchieveHigherPosition(){
            if (mainTarget == null) {
                return;
            }
            Status mainTargetStatus = mainTarget.getRole().getStatus();


            switch (mainTargetStatus) {
                case Captain, Mayor:
//                     Increase offense if target is Captain or Mayor
                    CombatService.executeAuthorityBattle(person.getCharacter(),mainTarget);
                    break;

                case Governor, King:

                    // Defeat all sentinels if target is Governor or King before attempting the Authority battle
                    if (defeatAllSentinels()) {


                        int defensePower = mainTarget.getPerson().getCombatStats().getDefenseLevel() + mainTarget.getPerson().getProperty().getDefenceStats().getUpgradeLevel();
                        int attackPower = person.getCombatStats().getOffenseLevel() + person.getCombatStats().getDefenseLevel();

                        // Must be 2 levels higher unless they have trait Aggressive. Aggressive always attacks.
                        if (attackPower >= defensePower+2){
                            if(!(attackPower <= defensePower && person.getAiEngine().getProfile().containsKey(Trait.Aggressive))) {
                                return;
                            }
                        }

                        CombatService.executeAuthorityBattle(person.getCharacter(),mainTarget);

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
    class AuthorityBattle extends WeightedObject{


        public AuthorityBattle(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
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
    class Robbery extends WeightedObject {


        public Robbery(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        @Override
        public void execute() {

            if(Time.year < 2){
                return;
            }

            if(isInBattle.test(person)){
                return;
            }

            super.execute();
        }


        /**
         * default is to rob random enemy. Add mainTarget to list if they have one
         */
        @Override
        public void defaultAction() {
            if(Settings.DB) {System.out.println("default rob 1" + this.getClass().getSimpleName());}
            Set<Person> possibleTargets = person.getRelationsManager().getEnemies();
            if(Settings.DB) {System.out.println("default rob 2" + this.getClass().getSimpleName());}
            if(!(mainTarget == null)){
                possibleTargets.add(mainTarget.getPerson());
            }
            if(Settings.DB) {System.out.println("default rob 3" + this.getClass().getSimpleName());}
            executeRobbery(possibleTargets);

        }
        @Override
        public void liberalAction() {
            if(Settings.DB){System.out.println("librobbery 1");}
            Set<Person> possibleTargets = person.getRelationsManager().getEnemies();
            if(Settings.DB){System.out.println("librobbery 2");}
            possibleTargets.removeIf(target -> !target.getAiEngine().getProfile().containsKey(Trait.Slaver)); // Liberal only want to attack slavers
            if(Settings.DB){System.out.println("librobbery 3");}
            if(possibleTargets.isEmpty()){
                possibleTargets.addAll(person.getRole().getNation().getSlaverGuild()); // if its empty, add all slaver guilders.
            }if(Settings.DB){System.out.println("librobbery 4");}
            if(!(mainTarget == null)){
                possibleTargets.add(mainTarget.getPerson());
            }if(Settings.DB){System.out.println("librobbery 5");}
            executeRobbery(possibleTargets);
        }
        @Override
        public void slaverAction() {

            Set<Person> possibleTargets = person.getRelationsManager().getEnemies();

            possibleTargets.removeIf(target -> target.getAiEngine().getProfile().containsKey(Trait.Slaver)); // Remove other Slavers just in case

            if(possibleTargets.isEmpty()){
                possibleTargets.addAll(person.getRole().getNation().getFreedomFighters()); // if its empty, add all liberal guilders.
            }
            if(!(mainTarget == null)){
                possibleTargets.add(mainTarget.getPerson());
            }
            executeRobbery(possibleTargets);
        }

        @Override
        public void disloyalAction() {

            Set<Person> possibleTargets = person.getRelationsManager().getAllies(); // disloyal prefers to attack allies and authorities

            possibleTargets.addAll(person.getProperty().getLocation().getPersonsLivingHere()); // add everyone from his quarter

            if(!(mainTarget == null)){
                possibleTargets.add(mainTarget.getPerson());
            }

            possibleTargets.add(person.getRole().getAuthority().getCharacterInThisPosition().getPerson()); // add authorities
            possibleTargets.add(person.getRole().getAuthority().getCharacterInThisPosition().getRole().getAuthority().getCharacterInThisPosition().getPerson());

            executeRobbery(possibleTargets);
        }

        @Override
        public void passiveAction(){
            defaultSkip();
        }

        @Override
        public void aggressiveAction(){

            Set<Person> possibleTargets = person.getRelationsManager().getEnemies();

            Nation nation = person.getRole().getNation(); // aggressive picks random quarter and attacks randomly

            possibleTargets.addAll(nation.getAllQuarters().get(random.nextInt(nation.numberOfQuarters)).getPersonsLivingHere());


            if(!(mainTarget == null)){
                possibleTargets.add(mainTarget.getPerson());
            }

            executeRobbery(possibleTargets);

        }
        private void executeRobbery(Set<Person> possibleTargets){
            if (possibleTargets.isEmpty()) {
                return;
            }
            if(Settings.DB){System.out.println("robbery 1");}

            Resource resource = decideWhatResourceToTarget();

            if(Settings.DB){System.out.println("robbery 2");}

            double riskRewardRatio = 0; // higher the better
            Person selectedTarget = null;

            for (Person target : possibleTargets){

                double riskReward = calculateRiskReward(target, resource);

                if(riskReward > riskRewardRatio){
                    riskRewardRatio = riskReward;
                    selectedTarget = target;
                }
            }
            if(Settings.DB){System.out.println("robbery 3");}

            if (selectedTarget == null){
                return;
            }
            if(Settings.DB){System.out.println("robbery 4");}

            if(selectedTarget.getProperty().getVault().isLowBalance()){
                return; // double check to not rob low balance vault
            }



            if(Settings.DB){System.out.println("robbery 6");}
            if (selectedTarget.getProperty().getDefenceStats().getUpgradeLevel() - 3 > person.getCombatStats().getOffenseLevel()){
                if(!(person.getAiEngine().getProfile().containsKey(Trait.Aggressive))){
                    return; // abort if the level difference is just too big and person is not aggressive
                }
            }


            if(Settings.DB){System.out.println("robbery 8");}
            CombatService.executeRobbery(person.getCharacter(), selectedTarget.getCharacter());

        }

        private double calculateRiskReward(Person target, Resource resource) {
            Property targetProperty = target.getProperty();

            double defence = targetProperty.getDefenceStats().getUpgradeLevel();
            double amount = targetProperty.getVault().getResource(resource);
            double offence = person.getCombatStats().getOffenseLevel();
            double base = 10;

            if(targetProperty.getVault().isLowBalance()){
                return 0;
            }

            return amount * ( Math.max(offence, 1)/ Math.max(defence, 1) ) / base;
        }

        private Resource decideWhatResourceToTarget() {
            // check if there is need for certain resource and target it, prefer Gold to other resources.
            Resource resource;
            EnumSet<Aspiration> aspirations = person.getAspirations();
            if(aspirations.contains(Aspiration.GET_FOOD_INSTANTLY)){
                resource = Resource.Food;
            }else if (aspirations.contains(Aspiration.GET_ALLOYS_INSTANTLY)){
                resource = Resource.Alloy;
            }else{
                resource = Resource.Gold;
            }
            return resource;
        }

    }

}





