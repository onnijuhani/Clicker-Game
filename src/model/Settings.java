package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.Random;

public class Settings {

    private static final Properties settings = new Properties();
    public static final boolean DB = false;


    private static final Random random = new Random(5);

    public static Random getRandom(){
        return random;
    }


    static {
        try (FileInputStream input = new FileInputStream("resources/propertySetting.properties")) {
            settings.load(input);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String formatNumber(int value) {
        if (value >= 1_000_000) {
            return String.format("%.1fM", value / 1_000_000.0);
        } else if (value >= 1_000) {
            return String.format("%.1fK", value / 1_000.0);
        } else {
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            return numberFormat.format(value);
        }
    }
    public static String formatNumber(double value) {
        if (value >= 1_000_000) {
            return String.format("%.1fM", value / 1_000_000.0);
        } else if (value >= 1_000) {
            return String.format("%.1fK", value / 1_000.0);
        } else {
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            return numberFormat.format(value);
        }
    }
    public static String formatShortNumber(double value) {
        if (value >= 1_000_000) {
            return String.format("%.0fM", value / 1_000_000.0);
        } else if (value >= 1_000) {
            return String.format("%.0fK", value / 1_000.0);
        } else {
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            return numberFormat.format(value);
        }
    }
    public static String formatShortNumber(String stringValue) {
        int value;
        try {
            value = Integer.parseInt(stringValue);
        } catch (NumberFormatException e) {
            return stringValue;
        }

        if (value >= 1_000_000) {
            return String.format("%.0fM", value / 1_000_000.0);
        } else if (value >= 1_000) {
            return String.format("%.0fK", value / 1_000.0);
        } else {
            // No need to format if the value is less than 1000
            return String.valueOf(value);
        }
    }

    public static String timeLeftFormat(int[] timeLeft){
        if(timeLeft == null){
            return (String.format("%d days, %d months, %d years left", 0, 0, 0));
        }
        return (String.format("%d days, %d months, %d years left", timeLeft[2], timeLeft[1], timeLeft[0]));
    }

    public static String removeUiNameAddition(String i){
        String name = i;
        if (i.contains("(you)")) {
            name = i.replace("(you)", "").trim();
        }
        if (i.contains("(King)")) {
            name = i.replace("(King)", "").trim();
        }
        if (i.contains("(Home)")) {
            name = i.replace("(Home)", "").trim();
        }
        return name;
    }



    public static int getInt(String key) {
        return Integer.parseInt(settings.getProperty(key, "0")); // Default value if key not found
    }
    public static double getDouble(String key) {
        return Double.parseDouble(settings.getProperty(key, "0")); // Default value if key not found
    }

}
