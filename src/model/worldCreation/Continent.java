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
    private World world;
    private Nation[] nations;

    public Continent(String name, World world) {
        this.propertyTracker = new PropertyTracker();
        this.name = name;
        this.world = world;
        this.createNations();
    }

    private void createNations() {

        Random random = new Random();
        int numberOfNations = random.nextInt(Settings.get("nationAmountMax")) + Settings.get("nationAmountMin");
        nations = new Nation[numberOfNations];

        for (int i = 0; i < numberOfNations; i++) {

            String nationName = NameCreation.generateNationName();

            King king = new King();
            Property property = PropertyCreation.createProperty(name, "Nation");
            property.setOwner(king);
            propertyTracker.addProperty(property);

            Authority authority = new NationAuthority(king);

            Nation nation = new Nation(nationName, this, authority);
            nations[i] = nation;

            // set home for king
            int homeIndex = random.nextInt(nation.getAllQuarters().size());
            Quarter home = nation.getAllQuarters().get(homeIndex);
            home.addCharacter(Status.King,king);
            changeKingAreaNames(home);

            supportFactory(nation, authority, random, home);
            NameCreation.generateMajorQuarterName(home);

        }
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
            noble.setNation(nation);
            authority.addSupporter(noble);

            noble.getProperty().setLocation(home);
            home.propertyTracker.addProperty(noble.getProperty());
            home.addCharacter(Status.Noble,noble);

        }

        for (int vang = 0; vang < provinceAmount; vang++) {
            Vanguard vanguard = new Vanguard(authority);
            vanguard.setNation(nation);
            authority.addSupporter(vanguard);

            //  random province from the nation
            Province province = nation.getContents().get(vang);
            // random city from the province
            City city = province.getContents().get(random.nextInt(province.getContents().size()));
            // random quarter from the city
            Quarter quarter = city.getContents().get(random.nextInt(city.getContents().size()));

            vanguard.getProperty().setLocation(quarter);
            quarter.propertyTracker.addProperty(vanguard.getProperty());
            quarter.addCharacter(Status.Vanguard, vanguard);
            NameCreation.generateMajorQuarterName(quarter);
        }


        //King gets 1 extra vanguard to live with him in his home quarter
        Vanguard vanguard = new Vanguard(authority);
        vanguard.setNation(nation);
        authority.addSupporter(vanguard);

        vanguard.getProperty().setLocation(home);
        home.propertyTracker.addProperty(vanguard.getProperty());
        home.addCharacter(Status.Vanguard, vanguard);

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
