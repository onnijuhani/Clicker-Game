package model.war;

import model.characters.Character;
import model.worldCreation.Nation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WarPlanningManager {



    public static void addNation(Nation nation) {
        nations.add(nation);
    }

    private static Set<Nation> nations;

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


    public static ArrayList<NationDetails> getNationsInfo(Nation nation){
        ArrayList<NationDetails> nations = new ArrayList<>();

        for(Nation n : WarPlanningManager.nations){

            int militaryPower = n.getMilitaryPower();
            int quarters = n.numberOfQuarters;
            Character leader = n.getAuthorityHere().getCharacterInThisPosition();
            NationDetails nationDetails = new NationDetails(n, militaryPower, quarters, leader);
            nations.add(nationDetails);
        }
        return nations;
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
                return "Nation=" + nation +
                        "militaryPower=" + militaryPower +
                        ", numberOfQuarters=" + numberOfQuarters +
                        ", leaderName='" + leader + '\'' +
                        '}';
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
