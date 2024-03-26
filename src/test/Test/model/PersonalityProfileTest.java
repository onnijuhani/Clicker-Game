package model;

import model.characters.Person;
import model.characters.Trait;
import model.characters.ai.AiEngine;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PersonalityProfileTest {

    @Test
    public void testPersonalityProfileGeneration() {

        HashMap<Integer, Integer> testAmounts = new HashMap<>();

        testAmounts.put(2,0);
        testAmounts.put(3,0);
        testAmounts.put(4,0);


        for(int i = 0; i < 20000; i++) {
            Person person = new Person(true);
            AiEngine engine = person.getAiEngine();
            Map<Trait, Integer> profile = engine.getProfile(); // Ensure you have a getter for the profile

            assertNotNull("Profile should not be null", profile);
            assertFalse("Profile should not be empty", profile.isEmpty());
            assertTrue("Profile should have 1 to 3 traits", profile.size() >= 1 && profile.size() <= 4);

            int totalWeight = profile.values().stream().reduce(0, Integer::sum);
            assertEquals("Total weight should be 100%", 100, totalWeight);

            int amount = profile.values().size();
            int newAmount = testAmounts.get(amount) + 1;
            testAmounts.put(amount , newAmount) ;
        }
        System.out.println(testAmounts);
    }
}

