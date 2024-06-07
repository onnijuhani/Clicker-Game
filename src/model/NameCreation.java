package model;

import model.characters.Status;
import model.worldCreation.Quarter;

import java.util.*;

public class NameCreation {

    // these sets exist to make sure same name doesn't exist 2 times.
    private static final Set<String> generatedNationNames = new HashSet<>();
    private static final Set<String> generatedContinentNames = new HashSet<>();
    private static final Set<String> generatedCharacterNames = new HashSet<>();
    private static final Set<String> generatedCityNames = new HashSet<>();

    public static String generateWorldName() {
        // Prefixes for the world names
        String[] prefixes = {
                "Astra", "Nova", "Terra", "Zephyr", "Eclipse", "Luna", "Solaris", "Gaia",
                "Helios", "Celestia", "Orion", "Nebula", "Pandora", "Chronos", "Elysium",
                "Olympus", "Galatea", "Thalassa", "Aether", "Artemis", "Hyperion", "Vulcan"
        };

        // Suffixes for the world names
        String[] suffixes = {
                "Prime", "Secundus", "Major", "Minor", "Alpha", "Beta", "Delta", "Proxima",
                "Centauri", "Tertius", "Quartus", "Pentus", "Hexa", "Septa", "Octa", "Nona",
                "Deca", "Aeon", "Nexus", "Haven", "Refuge", "Sanctuary", "Oasis"
        };

        // Optional: Add extra flair
        String[] extraFlair = {
                "Forgotten", "Hidden", "Lost", "Ancient", "Shrouded", "Mystical", "Ethereal",
                "Sacred", "Forbidden", "Enchanted", "Infinite", "Timeless"
        };

        Random random = Settings.getRandom();
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        String flair = random.nextDouble() < 0.6 ? extraFlair[random.nextInt(extraFlair.length)] + " " : ""; // 60% chance for extra flair

        return flair +""+ prefix +"-"+ suffix;
    }

    public static String generateNationName() {
        Random random = Settings.getRandom();
        String newName;

        do {
            newName = createNationName(random);
        } while (generatedNationNames.contains(newName));

        generatedNationNames.add(newName);
        return newName;
    }

    private static String createNationName(Random random) {

        String[] types = {"Dominion", "Empire", "Ascedancy", "Realm", "Conclave",
                "Federation", "Republic", "Alliance", "Union", "Syndicate", "Coalition",
                "United", "Eternal", "Majestic", "Glorious", "Radiant", "Serene", "Valiant", "Emerald",
                "Crimson", "Golden", "Silver", "Celestial", "Mystic", "Verdant"};

        String[] extraPrefix = {"Noble", "Grand", "Illustrious", "Majestic", "Regal", "Colossal"};

        String[] names = {
                "Stellar", "Radiant", "Ethereal", "Serene Bloom", "Blossom Harmony", "Crystal",
                "Astonishing", "Ascension", "Velvet Trail", "Whispering", "Old Harmony",
                "Infinite", "Pinnacle", "Iron", "Iron Legion", "Warfront", "Steel Enforcer",
                "Ironguard", "Terrific", "Stormfront", "Devastating", "Thunder", "Thunderstrike", "Warbound",
                "Kingdom", "Empire", "Republic", "Federation", "Commonwealth", "Dynasty",
                "Confederation", "Realm", "Alliance", "Principality", "State", "Union",
                "Territory", "Lands", "Dominion", "Protectorate", "Sultanate", "Caliphate"
        };

        double randomValue = random.nextDouble();
        String addExtraPrefix = (randomValue < 0.25) ? "Extra" : "";

        String prefix = names[random.nextInt(names.length)];
        String suffix = types[random.nextInt(types.length)];

        if (!addExtraPrefix.isEmpty()) {
            String extra = extraPrefix[random.nextInt(extraPrefix.length)];
            return extra + " " + prefix + " " + suffix;
        }

        return prefix + " " + suffix;
    }

    public static String generateProvinceName() {
        // Combined prefix and suffix arrays
        String[] prefix = {
                "Iron", "Shadowed", "Obsidian", "Thunder", "Onyx", "Warbound", "Steel", "Blackstone", "Savage", "Dreadnought", "Imperative",
                "Seraphica", "Mythosia", "Equinoxia", "Elysium", "Cosmara", "Verdantis", "Solara", "Chronosia", "Nebulia",
                "Ephemera", "Olympica", "Aurora Major", "Zephyria", "Novus Orbis", "Aetheria", "Heliosia", "Polaris Prime",
                "Maris Lux", "Ventura", "Silvatica", "Terravale", "Lunaria", "Serenia", "Oceana", "Caelum", "Avalon",
                "Celestria", "Atlantica", "Pacifica", "Ignitia", "Aquila Major", "Vespera", "Tempestia", "Aurora Minor",
                "Tranquil", "Serene", "Blossom", "Celestial", "Crystal", "Radiant", "Velvet", "Whispering", "Harmony", "Azure"
        };

        String[] suffix = {
                "Strongarm", "River", "Mountain", "Dominion", "Territory", "Region", "March", "Domain", "Expanse", "Frontier", "Seaside", "Ravine", "Dark",
                "Glade", "Highlands", "Enclave", "Haven", "Valley", "Basin", "Shire", "Falls", "Meadowlands"
        };

        Random random = Settings.getRandom();
        String first = prefix[random.nextInt(prefix.length)];
        String second = suffix[random.nextInt(suffix.length)];

        return first + " " + second;
    }

    public static String generateCityName() {

        String name;
        while (true) {
            String[] prefix = {
                    "Iron", "Shadowed", "Obsidian", "Thunder", "Onyx", "Warbound", "Steel", "Blackstone", "Savage", "Dreadnought",
                    "Tonitru", "Aciros", "Dread", "Umbracampo", "Onyxum", "Noirpetra", "Feroberga", "Salvagehavus", "Obsidiamarcha",
                    "Ferropolis", "Tondor", "Stelburg", "Dreadhaven", "Umbraharbor", "Onyxville", "Noirhaven", "Feroberg", "Savagemere",
                    "Tranquil", "Serene", "Blossom", "Celestial", "Crystal", "Radiant", "Velvet", "Whispering", "Harmony", "Azure",
                    "Célestia", "Tranquilé", "Radiantia", "Cristalia", "Velvétia", "Harmonia", "Azurea", "Murmura", "Serenia", "Blossoma",
                    "Célestiaville", "Tranquilburg", "Radiantburg", "Cristalburg", "Velvetville", "Harmoniavale", "Azurehaven", "Murmuragrove", "Serenburg", "Blossomberg"
            };

            String[] suffix = {
                    "Outpost", "Bastion", "Stronghold", "Garrison", "Arsenal", "Bastille", "City", "Metropolis", "Cityscape", "Haven", "Center",
                    "Capital", "Deep", "Springside", "Sunshine", "Fallsburg", "Waterside"
            };

            Random random = Settings.getRandom();
            String first = prefix[random.nextInt(prefix.length)];
            String second = suffix[random.nextInt(suffix.length)];

            name = first + " " + second;
            if (!generatedCityNames.contains(name)) {
                generatedCityNames.add(name);
                break;
            }
        }


        return name;
    }

    // Test method
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(generateCityName());
        }
    }

    public static String generateContinentName() {
        Random random = Settings.getRandom();
        String newName;

        do {
            newName = createContinentName(random);
        } while (generatedContinentNames.contains(newName));

        generatedContinentNames.add(newName);
        return newName;
    }

    public static String createContinentName(Random random) {

        String[] continentNames = {
                "Terra Magna", "Celestialis", "Pangea Ultima", "Arcadia", "Astralis", "Primordia", "Eldoria", "Terra Nova",
                "Seraphica", "Mythosia", "Equinoxia", "Elysium", "Cosmara", "Verdantis", "Solara", "Chronosia", "Nebulia",
                "Ephemera", "Olympica", "Aurora Major", "Zephyria", "Novus Orbis", "Aetheria", "Heliosia", "Polaris Prime",
                "Maris Lux", "Ventura", "Silvatica", "Terravale", "Lunaria", "Serenia", "Oceana", "Caelum", "Avalon",
                "Celestria", "Atlantica", "Pacifica", "Ignitia", "Aquila Major", "Vespera", "Tempestia", "Aurora Minor"
        };
        String[] prefixName = {
                "Ancient", "Mystic", "Eternal", "Golden", "Cerulean",
                "Shrouded", "Ethereal", "Radiant", "Sacred", "Forgotten",
                "Lost", "Aurelian", "Verdant", "Crimson", "Silent",
                "Whispering", "Emerald", "Majestic", "Celestial", "Silent",
                "Sapphire", "Astral", "Enchanted", "Dusken", "Divine",
                "Mystical", "Ivory", "Silver", "Vivid", "Mythic",
                "Iridescent", "Infinite", "Twilight", "Cascading", "Pristine",
                "Abyssal", "Luminescent", "Nebula", "Harmonic", "Oblivion",
                "Royal", "Sovereign", "Epic", "Aurelia", "Lustrous"
        };

        String name = continentNames[random.nextInt(continentNames.length)];

        double randomValue = random.nextDouble();
        String value = (randomValue < 0.5) ? prefixName[random.nextInt(prefixName.length)] + " " + name : name;

        return value;

    }


    public static ArrayList generateQuarterNames(int numberOfQuarters) {
        String[] quarters = {
                "Quarter", "District", "Zone", "Area", "Block", "Neighborhood",
        };
        Random random = Settings.getRandom();
        String name = quarters[random.nextInt(quarters.length)];
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < numberOfQuarters; i++){
            names.add(name + "-" + i);
        }
        return names;
    }

    public static String generateCharacterName(Boolean isNpc){

        String name;
        while (true) {
            String[] prefixes = {"Al", "El", "Thor", "Le", "Ea", "Ma", "Ra", "Gla", "Zen",
                    "Xan", "Kyro", "Zephyr", "Astra", "Nyx", "Vortex", "Zara", "Jax"};

            String[] suffixes = {"on", "us", "ius", "ar", "zir", "reliux",
                    "lyn", "drin", "mira", "sian", "thos", "lex", "onyx", "tyx", "ria", "tara"};

            String[] lastPrefixes = {"Silver", "Black", "Raven", "Iron", "Storm", "Swift", "Fire",
                    "Thorn", "Frost", "Ember", "Shadow", "Moon", "Star", "Steel", "Thunder", "Stone", "Blaze"};

            String[] lastSuffixes = {"heart", "thorn", "shadow", "bane", "crest", "wind", "forge", "blade",
                    "breaker", "warden", "seeker", "striker", "reaver", "bane", "hunter", "shaper", "keeper", "bearer"};

            Random random = Settings.getRandom();

            String firstName = prefixes[random.nextInt(prefixes.length)] + suffixes[random.nextInt(suffixes.length)];
            String lastName = lastPrefixes[random.nextInt(lastPrefixes.length)] + lastSuffixes[random.nextInt(lastSuffixes.length)];

            String player = "";
            if (!isNpc) {
                player = " (you)";
            }
            name = firstName + " " + lastName + player;

            if (!generatedCharacterNames.contains(name)) {
                generatedCharacterNames.add(name);
                break;
            }
        }

        return name;
    }


    public static void generateMajorQuarterName(Quarter quarter) {
        HashMap<Status, java.util.LinkedList<model.characters.Person>> populationList = quarter.getPopulationMap();

        String[] kingPrefixes = {"Royal"};
        String[] vanguardPrefixes = {"Vanguard's", "Guardian's", "Sentinel"};
        String[] governorPrefixes = {"Governor's", "Provincial", "Territorial" };
        String[] mercenaryPrefixes = {"Mercenary's", "Warrior's", "Soldier's", "Devil's"};
        String[] mayorPrefixes = {"Mayor's", "Urban", "Township's", "Community's", "Citys" };


        String[] suffixes = {"Haven", "Domain", "Lair", "Territory", "Estate", "Sanctum", "Gardens", "Hold", "Quarters", "Landing", "Imperative"};

        Random random = Settings.getRandom();

        boolean hasKing = populationList.containsKey(Status.King) && !populationList.get(Status.King).isEmpty();
        boolean hasVanguard = populationList.containsKey(Status.Vanguard) && !populationList.get(Status.Vanguard).isEmpty();
        boolean hasGovernor = populationList.containsKey(Status.Governor) && !populationList.get(Status.Governor).isEmpty();
        boolean hasMercenary = populationList.containsKey(Status.Mercenary) && !populationList.get(Status.Mercenary).isEmpty();
        boolean hasMayor = populationList.containsKey(Status.Mayor) && !populationList.get(Status.Mayor).isEmpty();

        String selectedSuffix = suffixes[random.nextInt(suffixes.length)];
        String selectedPrefix;
        if (hasKing) {
            selectedPrefix = kingPrefixes[random.nextInt(kingPrefixes.length)];
            quarter.setName(selectedPrefix + " " + selectedSuffix);
        } else if (hasVanguard) {
            selectedPrefix = vanguardPrefixes[random.nextInt(vanguardPrefixes.length)];
            quarter.setName(selectedPrefix + " " + selectedSuffix);
        } else if (hasGovernor) {
            selectedPrefix = governorPrefixes[random.nextInt(governorPrefixes.length)];
            quarter.setName(selectedPrefix + " " + selectedSuffix);
        } else if (hasMercenary) {
            selectedPrefix = mercenaryPrefixes[random.nextInt(mercenaryPrefixes.length)];
            quarter.setName(selectedPrefix + " " + selectedSuffix);
        } else if (hasMayor) {
        selectedPrefix = mayorPrefixes[random.nextInt(mayorPrefixes.length)];
        quarter.setName(selectedPrefix + " " + selectedSuffix);
    }




    }



}


