import java.util.Random;
import java.util.ArrayList;
public class NameCreation {

    public static String generateNationName(Orientation style) { //style = orientation name

        // 2 Orientations imperial and democratic
        String[] imperial = {"Dominion", "Empire", "Ascedancy", "Realm", "Conclave"};
        String[] democratic = {"Federation", "Republic", "Alliance", "Union", "Syndicate", "Coalition"};
        String[] extraPrefix = {"Noble", "Grand", "Illustrious", "Majestic", "Regal", "Colossal", };
        String[] democraticNames = {
                "Stellar", "Radiant", "Ethereal", "Serene Bloom", "Blossom Harmony", "Crystal",
                "Astonishing", "Ascension", "Velvet Trail", "Whispering", "Old Harmony",
        };
        String[] imperialNames = {
                "Infinite", "Pinnacle", "Iron", "Iron Legion", "Warfront", "Steel Enforcer",
                "Ironguard", "Terrific", "Stormfront", "Devastating", "Thunder", "Thunderstrike", "Warbound",
        };

        Random random = new Random();
        double randomValue = random.nextDouble();
        String addExtraPrefix = (randomValue < 0.25) ? "Orientation" : (randomValue < 0.5)  ? "Extra" : "" ;

        String prefix = "";
        String suffix = "";


        if (style.equals(Orientation.Imperial)) {
            suffix = imperial[random.nextInt(imperial.length)];
            prefix = imperialNames[random.nextInt(imperial.length)];
        } else if (style.equals(Orientation.Democratic)) {
            suffix = democratic[random.nextInt(democratic.length)];
            prefix = democraticNames[random.nextInt(democratic.length)];
        }

        if (addExtraPrefix.equals("Orientation")) {
            return style + " " + prefix + " " + suffix;
        } else if (addExtraPrefix.equals("Extra")) {
            String extra = extraPrefix[random.nextInt(extraPrefix.length)];
            return extra + " " + prefix + " " + suffix;
        }

        return prefix + " " + suffix;
    }



    public static String generateProvinceName(Orientation style) {
        String[] prefix = new String[0];
        String[] suffix = new String[0];

        if (style.equals(Orientation.Imperial)) {
            prefix = new String[]{
                    "Iron", "Shadowed", "Obsidian", "Thunder", "Onyx", "Warbound", "Steel", "Blackstone", "Savage", "Dreadnought", "Imperative",
                    "Seraphica", "Mythosia", "Equinoxia", "Elysium", "Cosmara", "Verdantis", "Solara", "Chronosia", "Nebulia",
                    "Ephemera", "Olympica", "Aurora Major", "Zephyria", "Novus Orbis", "Aetheria", "Heliosia", "Polaris Prime",
                    "Maris Lux", "Ventura", "Silvatica", "Terravale", "Lunaria", "Serenia", "Oceana", "Caelum", "Avalon",
                    "Celestria", "Atlantica", "Pacifica", "Ignitia", "Aquila Major", "Vespera", "Tempestia", "Aurora Minor"
            };
            suffix = new String[]{
                    "Strongarm", "River", "Mountain", "Dominion", "Territory", "Region", "March", "Domain", "Expanse", "Frontier", "Seaside", "Ravine", "Dark",
                    "Mountains"
            };
        }
        if (style.equals(Orientation.Democratic)) {
            prefix = new String[]{
                    "Tranquil", "Serene", "Blossom", "Celestial", "Crystal", "Radiant", "Velvet", "Whispering", "Harmony", "Azure",
                    "Seraphica", "Mythosia", "Equinoxia", "Elysium", "Cosmara", "Verdantis", "Solara", "Chronosia", "Nebulia",
                    "Ephemera", "Olympica", "Aurora Major", "Zephyria", "Novus Orbis", "Aetheria", "Heliosia", "Polaris Prime",
                    "Maris Lux", "Ventura", "Silvatica", "Terravale", "Lunaria", "Serenia", "Oceana", "Caelum", "Avalon",
                    "Celestria", "Atlantica", "Pacifica", "Ignitia", "Aquila Major", "Vespera", "Tempestia", "Aurora Minor"
            };
            suffix = new String[]{
                    "Glade", "Highlands", "Enclave", "Haven", "Expanse", "Valley", "Basin", "Shire", "Frontier", "Vale", "River", "Mountain", "Mountains",
                    "Falls", "Meadowlands"
            };
        }

        Random random = new Random();
        String first = prefix[random.nextInt(prefix.length)];
        String second = suffix[random.nextInt(suffix.length)];

        return first + " " + second;
    }

    public static String generateCityName(Orientation style) {
        String[] prefix = new String[0];
        String[] suffix = new String[0];

        if (style.equals(Orientation.Imperial)) {
            prefix = new String[]{
                    "Iron", "Shadowed", "Obsidian", "Thunder", "Onyx", "Warbound", "Steel", "Blackstone", "Savage", "Dreadnought",
                    "Tonitru", "Aciros", "Dread", "Umbracampo", "Onyxum", "Noirpetra", "Feroberga", "Salvagehavus", "Obsidiamarcha",
                    "Ferropolis", "Tondor", "Stelburg", "Dreadhaven", "Umbraharbor", "Onyxville", "Noirhaven", "Feroberg", "Savagemere",
            };
            suffix = new String[]{
                    "Outpost", "Bastion", "Stronghold", "Garrison", "Arsenal", "Bastille", "City", "Metropolis", "Cityscape", "Haven", "Center",
                    "Capital", "Deep"
            };
        }
        if (style.equals(Orientation.Democratic)) {
            prefix = new String[]{
                    "Tranquil", "Serene", "Blossom", "Celestial", "Crystal", "Radiant", "Velvet", "Whispering", "Harmony", "Azure",
                    "Célestia", "Tranquilé", "Radiantia", "Cristalia", "Velvétia", "Harmonia", "Azurea", "Murmura", "Serenia", "Blossoma",
                    "Célestiaville", "Tranquilburg", "Radiantburg", "Cristalburg", "Velvetville", "Harmoniavale", "Azurehaven", "Murmuragrove", "Serenburg", "Blossomberg"
            };
            suffix = new String[]{
                    "Outpost", "Bastion", "Stronghold", "Garrison", "Arsenal", "Bastille", "City", "Metropolis", "Cityscape", "Haven", "Center",
                    "Capital", "Springside", "Sunshine", "Fallsburg", "Waterside",
            };
        }

        Random random = new Random();
        String first = prefix[random.nextInt(prefix.length)];
        String second = suffix[random.nextInt(suffix.length)];

        return first + " " + second;
    }



    public static String generateContinentNames() {

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


