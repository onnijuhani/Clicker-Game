package model;

import java.io.FileInputStream;
import java.io.IOException;
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

    public static int getInt(String key) {
        return Integer.parseInt(settings.getProperty(key, "0")); // Default value if key not found
    }
    public static double getDouble(String key) {
        return Double.parseDouble(settings.getProperty(key, "0")); // Default value if key not found
    }

}
