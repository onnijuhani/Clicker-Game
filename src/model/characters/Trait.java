package model.characters;

import model.Settings;

import java.util.Map;

public enum Trait {
    Ambitious, Unambitious,

    Slaver, Liberal,

    Aggressive, Passive,

    Loyal, Disloyal,

    Attacker, Defender;

    public double getWinReq() {
        return switch (this) {
            case Ambitious -> 50.0;
            case Unambitious -> 80.0;
            case Slaver -> 40.0;
            case Liberal -> 70.0;
            case Aggressive -> 35.0;
            case Passive -> 75.0;
            case Loyal -> 85.0;
            case Disloyal -> 25.0;
            case Attacker -> 45.0;
            case Defender -> 65.0;
            default -> 100.0;
        };
    }

    public static Trait pickTrait(Map<Trait, Integer> profile) {
        int totalSum = profile.values().stream().mapToInt(Integer::intValue).sum();
        int randomNum = Settings.getRandom().nextInt(totalSum);
        int cumulativeProbability = 0;

        for (Map.Entry<Trait, Integer> entry : profile.entrySet()) {
            cumulativeProbability += entry.getValue();
            if (randomNum < cumulativeProbability) {
                return entry.getKey();
            }
        }
        // This should never happen if the probabilities sum up to 100
        throw new IllegalStateException("Unable to pick a trait.");
    }

    public static double getWeightedWinReq(Person person) {
        Map<Trait, Integer> profile = person.getAiEngine().getProfile();

        if (profile.isEmpty()) {
            throw new IllegalArgumentException("Profile cannot be empty");
        }

        double totalWeight = profile.values().stream().mapToInt(Integer::intValue).sum();
        double weightedSum = 0;

        for (Map.Entry<Trait, Integer> entry : profile.entrySet()) {
            double winReq = entry.getKey().getWinReq();
            int weight = entry.getValue();
            weightedSum += winReq * weight;
        }

        return weightedSum / totalWeight;
    }
}


