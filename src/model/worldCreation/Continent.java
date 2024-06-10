package model.worldCreation;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyFactory;
import model.buildings.PropertyTracker;
import model.characters.RelationsManager;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.NationAuthority;
import model.characters.npc.King;
import model.characters.npc.Noble;
import model.characters.npc.Vanguard;
import model.resourceManagement.TransferPackage;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Continent extends Area implements Details {
    private final World world;
    private Nation[] nations;

    public Continent(String name, World world) {
        this.propertyTracker = new PropertyTracker();
        this.name = name;
        this.world = world;
        this.createNations();

        updateEverything();

    }


    /**
     * this method is extremely poorly optimized but only runs in the beginning so doesn't really matter
     * should be updated for more efficiency if it needs to be called during gameplay
     */
    private void updateEverything(){

        for (Nation nation : nations){
            nation.collectGenerals();

            for (Quarter quarter : nation.getAllQuarters()){
                quarter.calculateQuarterWealth();
                quarter.createQuarterAlliances();
                quarter.createInitialRivalries();
            }
        }
    }

    private void createNations() {

        Random random = Settings.getRandom();
        int numberOfNations = random.nextInt(Settings.getInt("nationAmountMax")) + Settings.getInt("nationAmountMin");
        nations = new Nation[numberOfNations];

        for (int i = 0; i < numberOfNations; i++) {

            String nationName = NameCreation.generateNationName();

            King king = kingFactory();

            Authority authority = new NationAuthority(king);

            king.getRole().setAuthority(authority);

            Nation nation = new Nation(nationName, this, authority);

            authority.setAreaUnderAuthority(nation);

            nations[i] = nation;

            king.getRole().setNation(nation);

            // set home for king
            Quarter home = setKingHome(random, nation, king);

            supportFactory(nation, authority, random, home);

            //quarter where important characters live should have special name
            NameCreation.generateMajorQuarterName(home);


            // add sentinels as allies at the beginning
            RelationsManager relations = king.getPerson().getRelationsManager();
            relations.getListOfSentinels().forEach(relations::addAlly);

        }
    }


    private Quarter setKingHome(Random random, Nation nation, King king) {
        int homeIndex = random.nextInt(nation.getAllQuarters().size());
        Quarter home = nation.getAllQuarters().get(homeIndex);
        king.getPerson().getProperty().setLocation(home);
        home.addCitizen(Status.King, king.getPerson());
        changeKingAreaNames(home);
        return home;
    }


    private King kingFactory() {
        King king = new King();
        TransferPackage startingPackage = new TransferPackage(5000, 15000, 10000);
        king.getPerson().getWallet().addResources(startingPackage);
        Property property = PropertyFactory.createProperty(name, "Nation", king.getPerson());
        propertyTracker.addProperty(property);
        return king;
    }

    private void changeKingAreaNames(Quarter spawnQuarter){
        spawnQuarter.addKingToName(); //quarter
        ((City) spawnQuarter.getHigher()).addKingToName();//city
        ((Province) spawnQuarter.getHigher().getHigher()).addKingToName(); //province
    }

    private void supportFactory(Nation nation, Authority authority, Random random, Quarter home) {
        int nationAmount = nations.length; // doesnt work
        int provinceAmount = nation.getContents().size();

        for (int nob = 0; nob < 4; nob++) {
            Noble noble = new Noble(authority);
            noble.getRole().setNation(nation);

            authority.addSupporter(noble);

            noble.getPerson().getProperty().setLocation(home);
            home.propertyTracker.addProperty(noble.getPerson().getProperty());
            home.addCitizen(Status.Noble,noble.getPerson());


            nobleBonus(noble);

        }

        for (int vang = 0; vang < provinceAmount; vang++) {
            Vanguard vanguard = new Vanguard(authority);
            vanguard.getRole().setNation(nation);
            authority.addSupporter(vanguard);

            //  random province from the nation
            Province province = nation.getContents().get(vang);
            // random city from the province
            City city = province.getContents().get(random.nextInt(province.getContents().size()));
            // random quarter from the city
            Quarter quarter = city.getContents().get(random.nextInt(city.getContents().size()));

            vanguard.getPerson().getProperty().setLocation(quarter);
            quarter.propertyTracker.addProperty(vanguard.getPerson().getProperty());
            quarter.addCitizen(Status.Vanguard, vanguard.getPerson());
            NameCreation.generateMajorQuarterName(quarter);

            // vanguards get bonus
            vanguardBonus(vanguard);

        }


        //King gets 1 extra vanguard to live with him in his home quarter
        Vanguard vanguard = new Vanguard(authority);
        vanguard.getRole().setNation(nation);
        authority.addSupporter(vanguard);

        vanguard.getPerson().getProperty().setLocation(home);
        home.propertyTracker.addProperty(vanguard.getPerson().getProperty());
        home.addCitizen(Status.Vanguard, vanguard.getPerson());

        vanguardBonus(vanguard);

    }

    private static void nobleBonus(Noble noble) {
        noble.getPerson().getProperty().getUtilitySlot().addRandomUtilityBuilding(noble);
        TransferPackage bonus = new TransferPackage(2000,2000,4000);
        noble.getPerson().getWallet().addResources(bonus);
    }

    private static void vanguardBonus(Vanguard vanguard) {
        vanguard.getPerson().getCombatStats().increaseOffence(4);
        vanguard.getPerson().getCombatStats().increaseDefence(14);
        vanguard.getPerson().getProperty().getUtilitySlot().addRandomUtilityBuilding(vanguard);
        TransferPackage bonus = new TransferPackage(5000,15000,10000);
        vanguard.getPerson().getWallet().addResources(bonus);
    }


    @Override
    public String getDetails() {
        return ("Continent: " + name + " Belongs to: " + world.getName());
    }
    @Override
    public String getName() {
        return this.name;
    }
    @Override
    public Area getHigher() {
        return world;
    }

    @Override
    public ArrayList<Nation> getContents() {
        return new ArrayList<>(Arrays.asList(nations));
    }

}
