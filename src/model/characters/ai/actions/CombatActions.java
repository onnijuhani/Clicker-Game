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
import model.shop.UpgradeSystem;
import model.stateSystem.Event;
import model.stateSystem.State;
import model.time.Time;
import model.worldCreation.Nation;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static model.characters.combat.CombatSystem.calculateWinningChance;

@SuppressWarnings("CallToPrintStackTrace")
public class CombatActions extends BaseActions {

    private Character mainTarget;
    private final Predicate<Person> isInBattle = person -> person.hasState(State.IN_BATTLE);
    private final Random random = Settings.getRandom();


    private int attackPowerLevel;
    private int defencePowerLevel;
    private UpgradeSystem propertyDefence;
    private CombatStats combatStats;
    private Property property;
    private double winningChanceRequirement;



    public CombatActions(Person person, NPCActionLogger npcActionLogger, Map<Trait, Integer> profile) {
        super(person, npcActionLogger, profile);
    }
    @Override
    protected void createAllActions() {
        Duel duel = new Duel(person, npcActionLogger,3,profile);
        Robbery robbery = new Robbery(person, npcActionLogger,2,profile);
        EvaluateNeeds evaluateNeeds = new EvaluateNeeds(person, npcActionLogger,3,profile);
        TakeActionOnNeeds takeActionOnNeeds = new TakeActionOnNeeds(person, npcActionLogger,10,profile);
        AuthorityBattle authorityBattle = new AuthorityBattle(person, npcActionLogger,1, profile);

        allActions.add(duel);
        allActions.add(robbery);
        allActions.add(evaluateNeeds);
        allActions.add(takeActionOnNeeds);
        allActions.add(authorityBattle);
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
            update();
            if (saveResourceAbort()) return;

            defaultPowerUpgrade();

            super.execute();

        }
        @Override
        public void defaultAction(){
            if(!person.getPaymentManager().allowNPCToSpendMoney()) return;
            try {
                for (Aspiration aspiration : person.getAspirations()) {
                    switch (aspiration) {
                        case INCREASE_PERSONAL_OFFENCE:
                            if (combatStats.upgradeOffenseWithGold()) {
                                person.removeAspiration(Aspiration.INCREASE_PERSONAL_OFFENCE);
                                logAction("Increased personal offence to level " + attackPowerLevel);
                            }
                        case INCREASE_PERSONAL_DEFENCE:
                            if(combatStats.upgradeDefenceWithGold()){
                                person.removeAspiration(Aspiration.INCREASE_PERSONAL_DEFENCE);
                                logAction("Increased personal defence to level " + defencePowerLevel);
                            }
                        case INCREASE_PROPERTY_DEFENCE:
                            if (property.upgradeDefenceWithAlloys()) {
                                person.removeAspiration(Aspiration.INCREASE_PROPERTY_DEFENCE);
                                logAction("Increased personal defence to level " + propertyDefence.getUpgradeLevel());
                            }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();throw new RuntimeException(e);
            }
        }


        /**
         * Default counter makes sure all stats get upgraded at least slowly but randomly
         */
        private void defaultPowerUpgrade() {

            if(!person.getPaymentManager().allowNPCToSpendMoney()) return;

            if (defaultCounter > 5) {
                int randomNumber = random.nextInt(3);

                switch (randomNumber) {
                    case 0:
                        combatStats.upgradeOffenseWithGold();
                        logAction("Default increase personal offence to level " + attackPowerLevel);
                        break;
                    case 1:
                        combatStats.upgradeDefenceWithGold();
                        logAction("Default increase personal defence to level " + defencePowerLevel);
                        break;
                    case 2:
                        property.upgradeDefenceWithAlloys();
                        logAction("Default increase property defence to level " + propertyDefence.getUpgradeLevel());
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

        @Override
        public void execute(){
            update();
            super.execute();

        }
        // default is to compare against the minimum
        @Override
        public void defaultAction() {
            EnumSet<Aspiration> aspirations = person.getAspirations();
            considerMinimumLevels(combatStats, aspirations, property);
            testCounter++;
        }
        @Override
        public void ambitiousAction(){
            int totalAuthorityLevel = person.getRole().getAuthority().getCharacterInThisPosition().getPerson().getCombatStats().getDefenseLevel();

            if(totalAuthorityLevel >= attackPowerLevel){
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

        /**
         * Aggressive finds the strongest enemy he has to compare
         */
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

        /**
         * Tests the minimum power levels.
         * @param combatStats npc combat stats
         * @param aspirations current aspirations
         * @param property their current property
         */
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

    private boolean saveResourceAbort() {
        return person.getAspirations().contains(Aspiration.SAVE_RESOURCES);
    }


    /**
     * select a Set of possible targets, then call executeDuel method.
     */
    class Duel extends WeightedObject{
        Person latestDuel;
        public Duel(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }
        @Override
        public void execute() {
            if(isInBattle.test(person)){
                return;
            }
            update();
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

            executeDuel(listOfPossibleTargets, latestDuel, this, winningChanceRequirement);
        }

        /**
         * liberals will duel members of Slaver Guild
         */
        public void liberalAction(){
            Set<Person> listOfPossibleTargets = person.getRole().getNation().getSlaverGuild();
            executeDuel(listOfPossibleTargets, latestDuel, this, winningChanceRequirement);
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

            if (testAuthority()) return; // Cannot attack self
            roadToAchieveHigherPosition(latestDuel, this);
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
        public void execute() {
            if(isInBattle.test(person)){
                return;
            }
            mainTarget = person.getRole().getAuthority().getCharacterInThisPosition();
            update();
            super.execute();
        }

        @Override
        public void defaultAction() {
            roadToAchieveHigherPosition(null, this);
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
            update();
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

    /**
     * Updates variables that might change. Must be called in execute() methdods.
     */
    private void update(){
        try {
            attackPowerLevel = person.getCombatStats().getOffenseLevel();
            defencePowerLevel = person.getCombatStats().getDefenseLevel();
            propertyDefence = person.getProperty().getDefenceStats();
            combatStats = person.getCombatStats();
            property = person.getProperty();

            updateWinningChanceRequirement();

        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    private void updateWinningChanceRequirement() {
        try {
            if (mainTarget == null) {
                winningChanceRequirement = 0.65;
                return;
            }

            Trait trait = WeightedObject.pickTrait(profile);
            if(trait == null){
                winningChanceRequirement = 0.65;
                return;
            }

            switch ((trait)) {
                case Ambitious:
                    if (mainTarget == person.getRole().getAuthority().getCharacterInThisPosition()) {
                        winningChanceRequirement = 0.5;
                    } else {
                        winningChanceRequirement = 0.55;
                    }
                    break;
                case Unambitious:
                    winningChanceRequirement = 0.85;
                    break;
                case Slaver:
                    if (mainTarget.getPerson().getAiEngine().getProfile().containsKey(Trait.Liberal)) {
                        winningChanceRequirement = 0.4;
                    } else {
                        winningChanceRequirement = 0.6;
                    }
                    break;
                case Liberal:
                    if (mainTarget.getPerson().getAiEngine().getProfile().containsKey(Trait.Slaver)) {
                        int i = profile.getOrDefault(Trait.Liberal, 0);
                        winningChanceRequirement = Math.max(0.3, Math.min(0.55, 0.5 - i * 0.02));
                    } else {
                        winningChanceRequirement = 0.6;
                    }
                    break;
                case Defender:
                    winningChanceRequirement = 0.65;
                    break;
                case Aggressive:
                    int i = profile.getOrDefault(Trait.Aggressive, 0);
                    winningChanceRequirement = Math.max(0.25, Math.min(0.5, 0.5 - i * 0.02));
                    break;
                case Passive:
                    winningChanceRequirement = 0.8;
                    break;
                case Loyal:
                    if (mainTarget == person.getRole().getAuthority().getCharacterInThisPosition()) {
                        winningChanceRequirement = 1;
                    } else {
                        winningChanceRequirement = 0.80;
                    }
                    break;
                case Disloyal:
                    if (mainTarget == person.getRole().getAuthority().getCharacterInThisPosition()) {
                        winningChanceRequirement = 0.40;
                    } else {
                        winningChanceRequirement = 0.50;
                    }
                    break;
                case Attacker:
                    winningChanceRequirement = 0.60;
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected trait: " + trait);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void roadToAchieveHigherPosition(Person latestTarget, WeightedObject action){
        try {
            if (action instanceof Robbery) {
                return;
            }
            if (testAuthority())
                return; // quick return if there is a problem with the targets role

            if(person.getCombatStats().getDefenseLevel()*2 < person.getCombatStats().getOffenseLevel()){
                person.addAspiration(Aspiration.INCREASE_PERSONAL_DEFENCE);
                return; // return if defenseLevel is low
            }

            assert mainTarget != null;
            Status mainTargetStatus = mainTarget.getRole().getStatus();

            switch (mainTargetStatus) {
                case Captain, Mayor:

                    if (abortForLowWinningChance(mainTarget.getPerson(), winningChanceRequirement, Event.AuthorityBattle))
                        return; // abort since winning chance is too low

                    CombatService.executeAuthorityBattle(person.getCharacter(),mainTarget);
                    if (person.getAnyOnGoingEvent(Event.AuthorityBattle) != null) {
                        action.logAction(String.format("Started authority battle against %s with winning chance %f", mainTarget, winningChanceRequirement));
                    }
                    break;

                case Governor, King:
                    // Defeat all sentinels if target is Governor or King before attempting the Authority battle
                    if (defeatAllSentinels(latestTarget, action)) {
                        if (abortForLowWinningChance(mainTarget.getPerson(), winningChanceRequirement, Event.AuthorityBattle))
                            return; // abort since winning chance is too low
                        CombatService.executeAuthorityBattle(person.getCharacter(),mainTarget);
                        if (person.getAnyOnGoingEvent(Event.AuthorityBattle) != null) {
                            action.logAction(String.format("Started authority battle against %s with winning chance at least %2f", mainTarget, winningChanceRequirement));
                        }
                    }
                    break;

                default:
                    System.out.println("No specific action for " + mainTargetStatus);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    private boolean abortForLowWinningChance(Person mainTarget, double winningChanceRequirement, Event type) {
        double winningChance = calculateWinningChance(type, person, mainTarget);
        return winningChance <= winningChanceRequirement;
    }

    public static boolean abortForLowWinningChance(Person person, Person mainTarget, double winningChanceRequirement, Event type) {
        double winningChance = calculateWinningChance(type, person, mainTarget);
        return winningChance <= winningChanceRequirement;
    }


    private boolean testAuthority() {
        if(mainTarget == null){
            return true;
        }
        if (mainTarget.getPerson() == person) {
            return true;
        }
        return !(mainTarget instanceof AuthorityCharacter);
    }

    private boolean defeatAllSentinels(Person latestTarget, WeightedObject action) {
        try {
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
                executeDuel(undefeatedSentinels, latestTarget, action, winningChanceRequirement);
            }

            return undefeatedSentinels.isEmpty(); // return true if all sentinels have been defeated already
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }

    }

    /**
     * @param listOfPossibleTargets list of targets that are considered targets
     * @param latestTarget last person attacked to make sure npc doesn't constantly attack the same person
     * @param action the class that made the call
     * @param winningChanceRequirement whatever the minimum chance of victory to enter into battle should be
     */
    // both offense and defense are included in DUEL. Not in other combats.
    private void executeDuel(Set<Person> listOfPossibleTargets, Person latestTarget, WeightedObject action, double winningChanceRequirement) {
        try {
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
            if (attackPower >= minCombatSum + 2) {
                if (!(attackPower <= minCombatSum && profile.containsKey(Trait.Aggressive))) {
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

            if (randomlySelectedEnemy == latestTarget) {
                return; // don't duel the same person all the time
            }

            if (abortForLowWinningChance(randomlySelectedEnemy, winningChanceRequirement, Event.DUEL))
                return; // abort since winning chance is too low

            // Execute duel against the selected weakest undefeated enemy
            CombatService.executeDuel(person.getCharacter(), randomlySelectedEnemy.getCharacter());

            if (person.getAnyOnGoingEvent(Event.DUEL) != null) {
                action.logAction(String.format("Started duel against %s", randomlySelectedEnemy.getCharacter()));
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }

    }


    public static void executeDuel(Person person, Set<Person> listOfPossibleTargets) {
        try {
            if (listOfPossibleTargets.isEmpty()) {
                return;
            }

            // Find the minimum sum of defense and offense levels among undefeated enemies
            int minCombatSum = listOfPossibleTargets.stream()
                    .mapToInt(enemy -> enemy.getCombatStats().getDefenseLevel() + enemy.getCombatStats().getOffenseLevel())
                    .min()
                    .orElse(Integer.MAX_VALUE);


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


            Person latestTarget = person.getRelationsManager().getLatestTarget();

            if (randomlySelectedEnemy == latestTarget && !latestTarget.getRole().getStatus().isSentinel()) {
                return; // don't duel the same person all the time unless they are Sentinels
            }

            if (abortForLowWinningChance(person, randomlySelectedEnemy, Trait.getWeightedWinReq(person), Event.DUEL))
                return; // abort since winning chance is too low

            // Execute duel against the selected weakest undefeated enemy
            CombatService.executeDuel(person.getCharacter(), randomlySelectedEnemy.getCharacter());

            if (person.getAnyOnGoingEvent(Event.DUEL) != null) {
                person.logAction("Duel", String.format("Started duel against %s", randomlySelectedEnemy.getCharacter()));
                person.getRelationsManager().setLatestTarget(randomlySelectedEnemy);
            }

        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }

    }

    public static boolean defeatAllSentinels(Person person, Person target) {
        try {

            // Get all sentinels of the main target
            Set<Person> sentinels = target.getPerson().getRelationsManager().getListOfSentinels();

            Set<Person> sentinelsCopy = new HashSet<>(sentinels); // prevent ConcurrentModificationException

            Set<Person> undefeatedSentinels = new HashSet<>(); // sentinels that still need to be defeated

            for (Person sentinel : sentinelsCopy) { // add undefeated sentinels into the list
                if(!person.getRelationsManager().getListOfDefeatedPersons().contains(sentinel)){
                    undefeatedSentinels.add(sentinel);
                }
            }
            if(!undefeatedSentinels.isEmpty()) { // start dueling against those that need to be beaten
                CombatActions.executeDuel(person, undefeatedSentinels);
            }

            return undefeatedSentinels.isEmpty(); // return true if all sentinels have been defeated already
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    public static void achieveHigherPosition(Person person) {
        Person target = person.getRole().getAuthority().getPersonInThisPosition();

        if(person.getCombatStats().getDefenseLevel()*2 < person.getCombatStats().getOffenseLevel()){
            person.addAspiration(Aspiration.INCREASE_PERSONAL_DEFENCE);
            return;
        }

        double wcr = Trait.getWeightedWinReq(person); // winning chance requirement

        Status targetStatus = target.getRole().getStatus(); // get targets status

        switch (targetStatus) {
            case Captain, Mayor:

                if (abortForLowWinningChance(person, target.getPerson(), wcr, Event.AuthorityBattle))
                    return;

                CombatService.executeAuthorityBattle(person.getCharacter(),target.getCharacter());
                if (person.getAnyOnGoingEvent(Event.AuthorityBattle) != null) {
                    person.logAction("Achieve Higher Position", String.format("Started authority battle against %s with winning chance %f", target, wcr));
                }
                break;

            case Governor, King:
                // Defeat all sentinels if target is Governor or King before attempting the Authority battle
                if (defeatAllSentinels(person, target)) {
                    if (abortForLowWinningChance(person, target.getPerson(), wcr, Event.AuthorityBattle))
                        return;
                    CombatService.executeAuthorityBattle(person.getCharacter(),target.getCharacter());
                    if (person.getAnyOnGoingEvent(Event.AuthorityBattle) != null) {
                        person.logAction("Achieve Higher Position", String.format("Started authority battle against %s with winning chance at least %2f", target, wcr));
                    }
                }
                break;

            default:
                System.out.println("No specific action for " + targetStatus);
                break;
        }
    }





}





