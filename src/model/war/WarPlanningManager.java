package model.war;

import model.characters.Character;
import model.worldCreation.Nation;

import java.util.*;

public class WarPlanningManager {

    public static void addNation(Nation nation) {
        nations.add(nation);
    }

    private final static Set<Nation> nations = new HashSet<>();

    public static HashMap<Nation, Integer> getOtherNationsAndMilitaryPowers(Nation nation){
        HashMap<Nation, Integer> otherNations = new HashMap<>();

        for(Nation n : nations){
            if(n != nation){
                int militaryPower = n.getMilitaryPower();
                otherNations.put(n, militaryPower);
            }
        }
        return otherNations;
    }


    public static ArrayList<NationDetails> getNationsInfo() {
        ArrayList<NationDetails> nationsInfo = new ArrayList<>();

        for (Nation n : WarPlanningManager.nations) {
            int militaryPower = n.getMilitaryPower();
            int quarters = n.numberOfQuarters;
            Character leader = n.getAuthorityHere().getCharacterInThisPosition();
            NationDetails nationDetails = new NationDetails(n, militaryPower, quarters, leader);
            nationsInfo.add(nationDetails);
        }

        // Sort the list by military power in descending order
        nationsInfo.sort(Comparator.comparingInt(NationDetails::militaryPower).reversed());

        return nationsInfo;
    }

    public static List<NationMilitary> getStrongestMilitaries() {
        List<NationMilitary> strongestMilitaries = new ArrayList<>();

        for (Nation nation : nations) {
            nation.getStrongestMilitary().ifPresent(military ->
                    strongestMilitaries.add(new NationMilitary(nation, military)));
        }
        return strongestMilitaries;
    }


    public record NationDetails(Nation nation, int militaryPower, int numberOfQuarters, Character leader) {
        @Override
            public String toString() {
                return nation +
                        " " + militaryPower +
                        " " + numberOfQuarters +
                        " '" + leader;
        }
    }

    public record NationMilitary(Nation nation, Military military) {
        @Override
        public String toString() {
            return "Nation=" + nation +
                    ", militaryPower=" + military.getMilitaryStrength() +
                    ", military=" + military +
                    '}';
        }
    }

}
