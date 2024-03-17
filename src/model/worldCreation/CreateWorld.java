package model.worldCreation;

import model.characters.Character;
import model.characters.Peasant;
import model.characters.Status;
import model.characters.authority.Authority;
import model.resourceManagement.TransferPackage;
import model.time.GenerateManager;
import model.time.NpcManager;
import model.time.TaxEventManager;

import java.util.List;
import java.util.Random;

import static model.NameCreation.generateWorldName;

public class CreateWorld {
    private final World world;
    private final Quarter spawnQuarter;
    private Character initialPlayer;

    public CreateWorld() {
        Size size = Size.MEDIUM;
        this.world = new World(generateWorldName(), size);
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

            if (currentLevel == null) {
                throw new IllegalStateException("Reached a level that does not contain further contents.");
            }
        }
    }

    private void createPlayer(){
        Peasant initialPlayer = new Peasant();
        initialPlayer.getPerson().setPlayer(true);

        Quarter homeLocation = initialPlayer.getPerson().getProperty().getLocation();

        if(homeLocation == null) {
            System.out.println("homelocation");
            initialPlayer.getPerson().getProperty().setLocation(spawnQuarter);
            homeLocation = spawnQuarter;
        }

        Authority authority = initialPlayer.getRole().getAuthority();

        if(authority == null){
            System.out.println("authority");
            initialPlayer.getRole().setAuthority(homeLocation.getAuthorityHere());
        }

        initialPlayer.getRole().setNation(spawnQuarter.getNation());

        spawnQuarter.addCitizen(Status.Peasant, initialPlayer.getPerson());
        spawnQuarter.calculateCitizens();
        spawnQuarter.createQuarterAlliances();
        spawnQuarter.updateCitizenCache();


        this.initialPlayer = initialPlayer;

        TaxEventManager.subscribe(initialPlayer);
        GenerateManager.unSubscribe(initialPlayer);
        System.out.println(NpcManager.isSubscriped(initialPlayer));
        System.out.println(TaxEventManager.isSubscriped(initialPlayer));


        TransferPackage cheatPackage = new TransferPackage(100000,100000,100000);
        initialPlayer.getPerson().getWallet().addResources(cheatPackage);
        initialPlayer.getPerson().getProperty().getVault().addResources(cheatPackage);
        initialPlayer.getPerson().getWorkWallet().addResources(cheatPackage);



    }

    public Quarter getSpawnQuarter() {
        return spawnQuarter;
    }
    public Character getInitialPlayer() {
        return initialPlayer;
    }
}
