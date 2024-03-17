package model.worldCreation;

import model.NameCreation;
import model.Settings;
import model.buildings.Property;
import model.buildings.PropertyCreation;
import model.buildings.PropertyTracker;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.authority.NationAuthority;
import model.characters.npc.King;
import model.characters.npc.Noble;
import model.characters.npc.Vanguard;


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

    private void updateEverything(){

        for (Nation nation : nations){
            nation.collectGenerals();

            for (Quarter quarter : nation.getAllQuarters()){
                quarter.calculateQuarterWealth();
            }
        }
    }

    private void createNations() {

        Random random = new Random();
        int numberOfNations = random.nextInt(Settings.getInt("nationAmountMax")) + Settings.getInt("nationAmountMin");
        nations = new Nation[numberOfNations];

        for (int i = 0; i < numberOfNations; i++) {

            String nationName = NameCreation.generateNationName();

            King king = kingFactory();

            Authority authority = new NationAuthority(king);
            king.getRole().setAuthority(authority);


            Nation nation = new Nation(nationName, this, authority);
            nations[i] = nation;

            king.getRole().setNation(nation);

            // set home for king
            Quarter home = setKingHome(random, nation, king);

            supportFactory(nation, authority, random, home);

            //quarter where important characters live should have special name
            NameCreation.generateMajorQuarterName(home);


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
        Property property = PropertyCreation.createProperty(name, "Nation", king.getPerson());
        propertyTracker.addProperty(property);
        return king;
    }

    private void changeKingAreaNames(Quarter spawnQuarter){
        spawnQuarter.addKingToName(); //quarter
        ((City) spawnQuarter.getHigher()).addKingToName();//city
        ((Province) spawnQuarter.getHigher().getHigher()).addKingToName(); //province
    }

    private void supportFactory(Nation nation, Authority authority, Random random, Quarter home) {
        int nationAmount = nations.length;
        int provinceAmount = nation.getContents().size();

        for (int nob = 0; nob < nationAmount; nob++) {
            Noble noble = new Noble(authority);
            noble.getRole().setNation(nation);

            authority.addSupporter(noble);

            noble.getPerson().getProperty().setLocation(home);
            home.propertyTracker.addProperty(noble.getPerson().getProperty());
            home.addCitizen(Status.Noble,noble.getPerson());

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
        }


        //King gets 1 extra vanguard to live with him in his home quarter
        Vanguard vanguard = new Vanguard(authority);
        vanguard.getRole().setNation(nation);
        authority.addSupporter(vanguard);

        vanguard.getPerson().getProperty().setLocation(home);
        home.propertyTracker.addProperty(vanguard.getPerson().getProperty());
        home.addCitizen(Status.Vanguard, vanguard.getPerson());

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
