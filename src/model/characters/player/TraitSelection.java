package model.characters.player;

import model.Model;
import model.characters.Trait;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraitSelection {

    public static Map<Trait, Integer> profile = new HashMap<>();
    public static int selectedTraitsCount = 0;

    public static String getString() {
        if (profile.isEmpty()) {
            return "No traits selected.";
        }

        // Sort the profile entries by their values in descending order
        List<Map.Entry<Trait, Integer>> sortedProfile = profile.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .toList();

        // Extract trait names in sorted order
        List<String> traitNames = sortedProfile.stream()
                .map(entry -> entry.getKey().name())
                .toList();

        // Build the result string
        StringBuilder result = new StringBuilder("Your selected traits are: ");
        for (int i = 0; i < traitNames.size(); i++) {
            if (i > 0 && i == traitNames.size() - 1) {
                result.append(" and ");
            } else if (i > 0) {
                result.append(", ");
            }
            result.append(traitNames.get(i));
        }

        return result.toString();
    }

    public static void setProfile() {
        Model.getPlayerAsPerson().getAiEngine().setProfile(profile);
    }
}
