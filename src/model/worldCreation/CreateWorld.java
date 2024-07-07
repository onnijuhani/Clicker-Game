package model.worldCreation;

import model.Settings;
import model.characters.Character;
import model.characters.Peasant;
import model.characters.Status;
import model.characters.authority.Authority;
import model.time.GenerateManager;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import static model.NameCreation.generateWorldName;

public class CreateWorld implements Serializable {

    public final World world;
    private final Quarter spawnQuarter;
    private Character initialPlayer;

    public CreateWorld() {
        this.world = new World(generateWorldName(), Size.MEDIUM);
        this.spawnQuarter = createSpawn();
        changeSpawnName();
        createPlayer();
    }

    private void changeSpawnName(){
        spawnQuarter.addHomeToName(); //quarter
        spawnQuarter.getHigher().addHomeToName(); //city
        spawnQuarter.getHigher().getHigher().addHomeToName(); //province
        spawnQuarter.getNation().addHomeToName(); //nation
        spawnQuarter.getNation().getHigher().addHomeToName(); //continent
     }

    private Quarter createSpawn() {
        Random random = Settings.getRandom();
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

            if (currentLevel == null) {
                throw new IllegalStateException("Reached a level that does not contain further contents.");
            }
        }
    }

    private void createPlayer(){
        Peasant initialPlayer = new Peasant(true);
        initialPlayer.getPerson().setPlayer(true);

        Quarter homeLocation = initialPlayer.getPerson().getProperty().getLocation();

        if(homeLocation == null) {
            initialPlayer.getPerson().getProperty().setLocation(spawnQuarter);
            homeLocation = spawnQuarter;
        }

        Authority authority = initialPlayer.getRole().getAuthority();

        if(authority == null){
            initialPlayer.getRole().setAuthority(homeLocation.getAuthorityHere());
        }

        initialPlayer.getRole().setNation(spawnQuarter.getNation());

        spawnQuarter.addCitizen(Status.Peasant, initialPlayer.getPerson());
        spawnQuarter.calculateCitizens();
        spawnQuarter.createQuarterAlliances();
        spawnQuarter.updateCitizenCache();


        this.initialPlayer = initialPlayer;

        GenerateManager.unSubscribe(initialPlayer);


//        TransferPackage cheatPackage = new TransferPackage(500,500,500);
//        initialPlayer.getPerson().getWallet().addResources(cheatPackage);
//        initialPlayer.getPerson().getProperty().getVault().addResources(cheatPackage);
//        initialPlayer.getPerson().getWorkWallet().addResources(cheatPackage);

        initialPlayer.getPerson().getCombatStats().increaseOffence(150);

    }

    public Quarter getSpawnQuarter() {
        return spawnQuarter;
    }
    public Character getInitialPlayer() {
        return initialPlayer;
    }
}
