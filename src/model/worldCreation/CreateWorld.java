package model.worldCreation;

import java.util.List;
import java.util.Random;

import static model.NameCreation.generateWorldName;

public class CreateWorld {
    private final Size size;
    private World world;
    private Quarter spawnQuarter;

    public CreateWorld() {
        this.size = Size.MEDIUM;
        this.world = new World(generateWorldName(), this.size);
        this.spawnQuarter = createSpawn(); //setting this in the method itself crashes the game. Must be assigned here
        changeSpawnName();
    }

    private void changeSpawnName(){
        spawnQuarter.addHomeToName(); //quarter
        spawnQuarter.getHigher().addHomeToName(); //city
        spawnQuarter.getHigher().getHigher().addHomeToName(); //province
        spawnQuarter.getNation().addHomeToName(); //nation
        spawnQuarter.getNation().getHigher().addHomeToName(); //continent
     }

    private Quarter createSpawn() {
        Random random = new Random();
        Area currentLevel = this.world; // Starting from the world

        while (true) {
            List<?> contents = currentLevel.getContents();
            if (contents.isEmpty()) {
                throw new IllegalStateException("No contents found at the current level.");
            }

            // Select a random quarter
            currentLevel = (Area) contents.get(random.nextInt(contents.size()));

            // Check if the current level is a Quarter and return the spawn
            if (currentLevel instanceof Quarter) {
                return (Quarter) currentLevel;
            }

            if (!(currentLevel instanceof HasContents)) {
                throw new IllegalStateException("Reached a level that does not contain further contents.");
            }
        }
    }

    public Quarter getSpawnQuarter() {
        return spawnQuarter;
    }
}
