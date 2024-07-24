package model.characters.ai;

import model.Settings;
import model.characters.Person;
import model.characters.Trait;
import model.characters.ai.actionCircle.WeightedCircle;
import model.characters.ai.actions.*;

import java.util.*;

public class AiEngine {
    private final CombatActions combatActions;
    private final UtilityActions utilityActions;
    private final WarActions warActions;
    private final ManagementActions managementActions;
    private final Person person;



    private Map<Trait, Integer> profile;
    private final WeightedCircle actionCircle = new WeightedCircle(25,1);
    private final NPCActionLogger npcActionLogger = new NPCActionLogger();
    private boolean isRunning = true; // whether AI engine is executed or not, used for player's autoplay functionality

    public AiEngine(Person person) {
        this.person = person;
        generatePersonalityProfile();

        this.combatActions = new CombatActions(person, npcActionLogger ,profile);
        this.utilityActions = new UtilityActions(person, npcActionLogger, profile);
        this.warActions = new WarActions(person, npcActionLogger, profile);
        this.managementActions = new ManagementActions(person, npcActionLogger, profile);

        setUpActionLoop();
    }

    public void setProfile(Map<Trait, Integer> profile) {
        this.profile = profile;
    }


    public void setRunning(boolean running) {
        isRunning = running;
    }

    public void executeAiEngine() {
        if(isRunning) {
            actionCircle.executeLoop();
        }
    }

    private void setUpActionLoop() {
        actionCircle.addAll(utilityActions.getAllActions());
        actionCircle.addAll(combatActions.getAllActions());
        actionCircle.addAll(managementActions.getAllActions());
        actionCircle.addAll(warActions.getAllActions());


    }

    /**
     * generates personality profile
     * adds between 2-4 personality traits and weight for them
     */
    protected void generatePersonalityProfile() {
        Random random = Settings.getRandom();
        Map<Trait, Integer> profile = new HashMap<>();

        int numberOfTraits = random.nextInt(10) < 1 ? 2 : random.nextInt(4) < 3 ? 3 : 4;

        Trait[][] personalities = initializePersonalityStructure();

        Set<Integer> selectedIndexes = new HashSet<>();
        List<Trait> selectedPersonalities = new ArrayList<>();

        while (selectedPersonalities.size() < numberOfTraits) {
            int randomIndex = random.nextInt(Trait.values().length/2);
            if (!selectedIndexes.contains(randomIndex)) {
                int randomChoice = random.nextInt(2);
                selectedPersonalities.add(personalities[randomIndex][randomChoice]);
                selectedIndexes.add(randomIndex);
            }
        }


        int remainingWeight = 100;
        for (int i = 0; i < numberOfTraits; i++) {
            if (i == numberOfTraits - 1) {
                // Assign the remaining weight to the last trait
                profile.put(selectedPersonalities.get(i), remainingWeight);
            } else {
                // Distribute the weight, ensuring at least 1% is assigned
                int weight = 1 + random.nextInt(remainingWeight - (numberOfTraits - i) - 1);
                remainingWeight -= weight;
                profile.put(selectedPersonalities.get(i), weight);
            }
        }

        initialAspirations(profile);

        this.profile = profile;
    }

    private void initialAspirations(Map<Trait, Integer> profile) {
        if(profile.containsKey(Trait.Ambitious) && profile.containsKey(Trait.Disloyal)){
            if(profile.get(Trait.Ambitious) > 30 || profile.get(Trait.Disloyal) > 30){
                person.addAspiration(Aspiration.ACHIEVE_HIGHER_POSITION);
            }
        }

        if(profile.containsKey(Trait.Defender)){
            if(profile.get(Trait.Defender) > 45){
                person.addAspiration(Aspiration.INCREASE_PERSONAL_DEFENCE);
                person.addAspiration(Aspiration.INCREASE_PROPERTY_DEFENCE);
            }
        }

        if(profile.containsKey(Trait.Attacker)){
            if(profile.get(Trait.Attacker) > 45) {
                person.addAspiration(Aspiration.INCREASE_PERSONAL_OFFENCE);
            }
        }
    }

    private Trait[][] initializePersonalityStructure() {

        Trait[][] personalities;

        personalities = new Trait[Trait.values().length/2][2];

        personalities[0][0] = Trait.Ambitious;
        personalities[1][0] = Trait.Slaver;
        personalities[2][0] = Trait.Aggressive;
        personalities[3][0] = Trait.Loyal;
        personalities[4][0] = Trait.Attacker;


        personalities[0][1] = Trait.Unambitious;
        personalities[1][1] = Trait.Liberal;
        personalities[2][1] = Trait.Passive;
        personalities[3][1] = Trait.Disloyal;
        personalities[4][1] = Trait.Defender;


        return personalities;
    }



    public Map<Trait, Integer> getProfile() {
        return profile;
    }

    public CombatActions getCombatActions() {
        return combatActions;
    }


    public UtilityActions getUtilityActions() {
        return utilityActions;
    }

    public WarActions getWarActions() {
        return warActions;
    }

    public ManagementActions getManagementActions() {
        return managementActions;
    }

    public Person getPerson() {
        return person;
    }

    public NPCActionLogger getNpcActionLogger() {
        return npcActionLogger;
    }

}
