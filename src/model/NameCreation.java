package model;

import java.util.Random;
import java.util.ArrayList;
public class NameCreation {

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

        // Optional: Add extra flair with a random chance
        String[] extraFlair = {
                "Forgotten", "Hidden", "Lost", "Ancient", "Shrouded", "Mystical", "Ethereal",
                "Sacred", "Forbidden", "Enchanted", "Infinite", "Timeless"
        };

        Random random = new Random();
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        String flair = random.nextDouble() < 0.6 ? extraFlair[random.nextInt(extraFlair.length)] + " " : ""; // 60% chance for extra flair

        return flair +""+ prefix +"-"+ suffix;
    }

    public static String generateNationName() {

        // Combine imperial and democratic types into a single array
        String[] types = {"Dominion", "Empire", "Ascedancy", "Realm", "Conclave",
                "Federation", "Republic", "Alliance", "Union", "Syndicate", "Coalition",
                "Great", "United", "Free", "New", "Ancient", "Grand", "Royal", "Sovereign",
                "Eternal", "Majestic", "Glorious", "Radiant", "Serene", "Valiant", "Emerald",
                "Crimson", "Golden", "Silver", "Celestial", "Mystic", "Verdant"};

        String[] extraPrefix = {"Noble", "Grand", "Illustrious", "Majestic", "Regal", "Colossal"};

        // Combine names into a single array
        String[] names = {
                "Stellar", "Radiant", "Ethereal", "Serene Bloom", "Blossom Harmony", "Crystal",
                "Astonishing", "Ascension", "Velvet Trail", "Whispering", "Old Harmony",
                "Infinite", "Pinnacle", "Iron", "Iron Legion", "Warfront", "Steel Enforcer",
                "Ironguard", "Terrific", "Stormfront", "Devastating", "Thunder", "Thunderstrike", "Warbound",
                "Kingdom", "Empire", "Republic", "Federation", "Commonwealth", "Dynasty",
                "Confederation", "Realm", "Alliance", "Principality", "State", "Union",
                "Territory", "Lands", "Dominion", "Protectorate", "Sultanate", "Caliphate"
        };

        Random random = new Random();
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

        Random random = new Random();
        String first = prefix[random.nextInt(prefix.length)];
        String second = suffix[random.nextInt(suffix.length)];

        return first + " " + second;
    }

    public static String generateCityName() {
        // Combined prefix and suffix arrays
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

        Random random = new Random();
        String first = prefix[random.nextInt(prefix.length)];
        String second = suffix[random.nextInt(suffix.length)];

        return first + " " + second;
    }

    // Test method
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(generateCityName());
        }
    }



    public static String generateContinentName() {

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

        Random random = new Random();
        String name = continentNames[random.nextInt(continentNames.length)];

        double randomValue = random.nextDouble();
        String value = (randomValue < 0.5) ? prefixName[random.nextInt(prefixName.length)] + " " + name : name;

        return value;

    }


    public static ArrayList generateQuarterNames(int numberOfQuarters) {
        String[] quarters = {
                "Quarter", "District", "Zone", "Area", "Block", "Neighborhood",
        };
        Random random = new Random();
        String name = quarters[random.nextInt(quarters.length)];
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < numberOfQuarters; i++){
            names.add(name + "-" + i);
        }
        return names;
    }

    public static String generateCharacterName(){

        String[] prefixes = {"Al", "El", "Thor", "Le", "Ea", "Ma", "Ra", "Gla", "Zen",
                "Xan", "Kyro", "Zephyr", "Astra", "Nyx", "Vortex", "Zara", "Jax"};

        String[] suffixes = {"on", "ius", "us", "us", "on", "ius", "ar", "zir", "reliux",
                "lyn", "drin", "mira", "sian", "thos", "lex", "onyx", "tyx", "ria", "tara"};

        String[] lastPrefixes = {"Silver", "Black", "Raven", "Iron", "Storm", "Swift", "Fire",
                "Thorn", "Frost", "Ember", "Shadow", "Moon", "Star", "Steel", "Thunder", "Stone", "Blaze"};

        String[] lastSuffixes = {"heart", "thorn", "shadow", "bane", "crest", "wind", "forge", "blade",
                "breaker", "warden", "seeker", "striker", "reaver", "bane", "hunter", "shaper", "keeper", "bearer"};

        Random random = new Random();

        String firstName = prefixes[random.nextInt(prefixes.length)] + suffixes[random.nextInt(suffixes.length)];
        String lastName = lastPrefixes[random.nextInt(lastPrefixes.length)] + lastSuffixes[random.nextInt(lastSuffixes.length)];

        return firstName + " " + lastName;
    }



}


