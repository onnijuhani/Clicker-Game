package model.characters.ai;

import model.characters.Person;
import model.characters.Personality;
import model.characters.ai.actions.*;

import java.util.*;

public class AiEngine {

    private final CombatActions combatActions;
    private final PowerActions powerActions;
    private final UtilityActions utilityActions;
    private final WarActions warActions;
    private final ManagementActions managementActions;
    private final Person person;
    private Map<Personality, Integer> profile;





    public AiEngine(Person person) {
        this.person = person;
        this.combatActions = new CombatActions(person);
        this.powerActions = new PowerActions(person);
        this.utilityActions = new UtilityActions(person);
        this.warActions = new WarActions(person);
        this.managementActions = new ManagementActions(person);

        generatePersonalityProfile();
    }

    /**
     * generates personality profile
     * adds between 2-4 personality traits and weight for them
     */
    protected void generatePersonalityProfile() {
        Random random = new Random();
        Map<Personality, Integer> profile = new HashMap<>();

        int numberOfTraits = random.nextInt(10) < 1 ? 2 : random.nextInt(4) < 3 ? 3 : 4;

        Personality[][] personalities = initializePersonalityStructure();

        Set<Integer> selectedIndexes = new HashSet<>();
        List<Personality> selectedPersonalities = new ArrayList<>();

        while (selectedPersonalities.size() < numberOfTraits) {
            int randomIndex = random.nextInt(Personality.values().length/2);
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

        if(profile.containsKey(Personality.Ambitious)){
            person.addAspiration(Aspiration.ACHIEVE_HIGHER_POSITION);
        }

        this.profile = profile;
    }

    private Personality[][] initializePersonalityStructure() {

        Personality[][] personalities;

        personalities = new Personality[Personality.values().length/2][2];

        personalities[0][0] = Personality.Ambitious;
        personalities[1][0] = Personality.Slaver;
        personalities[2][0] = Personality.Aggressive;
        personalities[3][0] = Personality.Loyal;
        personalities[4][0] = Personality.Attacker;


        personalities[0][1] = Personality.Unambitious;
        personalities[1][1] = Personality.Liberal;
        personalities[2][1] = Personality.Passive;
        personalities[3][1] = Personality.Disloyal;
        personalities[4][1] = Personality.Defender;


        return personalities;
    }



    public Map<Personality, Integer> getProfile() {
        return profile;
    }

    public CombatActions getCombatActions() {
        return combatActions;
    }

    public PowerActions getPowerActions() {
        return powerActions;
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

}
