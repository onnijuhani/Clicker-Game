package model;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {

    private static final Properties settings = new Properties();

    static {
        try (FileInputStream input = new FileInputStream("resources/propertySetting.properties")) {
            settings.load(input);


        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, e.g., by logging or re-throwing a runtime exception
        }
    }

    public static int get(String key) {
        return Integer.parseInt(settings.getProperty(key, "0")); // Default value if key not found
    }
}
