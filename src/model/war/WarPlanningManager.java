package model.war;

import model.Model;
import model.characters.Character;
import model.worldCreation.Nation;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WarPlanningManager {



    private final static Set<Nation> nations = new HashSet<>();
    private final static Set<Nation> impartialNations = new HashSet<>();
    private final static Set<Nation> overLordNations = new HashSet<>();
    private final static Set<Nation> vassalNations = new HashSet<>();



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

    public static void addNation(Nation nation) {
        nations.add(nation);
        filterNations();
    }


    public enum NationType {
        VASSAL, OVERLORD, IMPARTIAL
    }

    private final static Map<NationType, WarStrategy> strategyMap = Map.of(
            NationType.VASSAL, new VassalWarStrategy(),
            NationType.OVERLORD, new OverlordWarStrategy(),
            NationType.IMPARTIAL, new ImpartialWarStrategy()
    );


    public static void filterNations() {
        for (Nation nation : nations) {
            if (nation.isVassal()) {
                vassalNations.add(nation);
            } else if (nation.isOverlord()) {
                overLordNations.add(nation);
            } else {
                impartialNations.add(nation);
            }
        }
    }


    public static void makeWarDecisions() {
        for (Nation nation : nations) {
            WarStrategy strategy = getStrategyForNation(nation);
            Set<Nation> potentialTargets = getPotentialTargets(nation);

            strategy.decideTarget(nation, potentialTargets).ifPresent(target -> {
                declareWar(nation, target);
            });
        }
    }

    private static void declareWar(Nation attacker, Nation defender) {
        if(attacker.getAuthorityHere().getCharacterInThisPosition() == Model.getPlayerAsCharacter()){
            return; // Automatic war matchmaking is off if player is King!
        }
        WarService.startWar(attacker, defender);
    }


    private static WarStrategy getStrategyForNation(Nation nation) {
        if (nation.isVassal()) return strategyMap.get(NationType.VASSAL);
        if (nation.isOverlord()) return strategyMap.get(NationType.OVERLORD);
        return strategyMap.get(NationType.IMPARTIAL);
    }

    private static Set<Nation> getPotentialTargets(Nation nation) {
        if (nation.isVassal()) {
            return Set.of(nation.getOverlord());
        } else if (nation.isOverlord()) {
            return impartialNations;
        } else {
            return Stream.concat(impartialNations.stream(), overLordNations.stream()).collect(Collectors.toSet());
        }
    }


    public interface WarStrategy {
        Optional<Nation> decideTarget(Nation nation, Set<Nation> potentialTargets);
    }

    public static class VassalWarStrategy implements WarStrategy {
        @Override
        public Optional<Nation> decideTarget(Nation nation, Set<Nation> potentialTargets) {
            // Vassals can only attack their overlord
            if (nation.isVassal()) {
                return Optional.ofNullable(nation.getOverlord());
            }
            return Optional.empty();
        }
    }

    public static class OverlordWarStrategy implements WarStrategy {
        @Override
        public Optional<Nation> decideTarget(Nation nation, Set<Nation> potentialTargets) {
            // Overlords can attack anyone in the impartial list
            return potentialTargets.stream()
                    .min(Comparator.comparingInt(Nation::getMilitaryPower));
        }
    }

    public static class ImpartialWarStrategy implements WarStrategy {
        @Override
        public Optional<Nation> decideTarget(Nation nation, Set<Nation> potentialTargets) {
            // Impartials can attack other impartials or overlords
            return potentialTargets.stream()
                    .min(Comparator.comparingInt(Nation::getMilitaryPower));
        }
    }



}
