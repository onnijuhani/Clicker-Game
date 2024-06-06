package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Properties;

public class Settings {

    private static final Properties settings = new Properties();
    public static final boolean DB = false;





    static {
        try (FileInputStream input = new FileInputStream("resources/propertySetting.properties")) {
            settings.load(input);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String formatNumber(int value) {
        if (value >= 1_000_000) {
            return String.format("%.2fM", value / 1_000_000.0);
        } else if (value >= 1_000) {
            return String.format("%.2fK", value / 1_000.0);
        } else {
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            return numberFormat.format(value);
        }
    }
    public static String formatNumber(double value) {
        if (value >= 1_000_000) {
            return String.format("%.2fM", value / 1_000_000.0);
        } else if (value >= 1_000) {
            return String.format("%.2fK", value / 1_000.0);
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


    public static int getInt(String key) {
        return Integer.parseInt(settings.getProperty(key, "0")); // Default value if key not found
    }
    public static double getDouble(String key) {
        return Double.parseDouble(settings.getProperty(key, "0")); // Default value if key not found
    }

}
