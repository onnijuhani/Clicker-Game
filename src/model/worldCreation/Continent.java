package model.worldCreation;

import model.NameCreation;
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
        int numberOfNations = random.nextInt(3) + 3;
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
            home.addPop(Status.King,king);

            supportFactory(nation, authority, random);
        }
    }

    private void supportFactory(Nation nation, Authority authority, Random random) {
        int nationAmount = nations.length;
        int provinceAmount = nation.getContents().size();

        for (int nob = 0; nob < nationAmount; nob++) {
            Noble noble = new Noble(authority);
            noble.setNation(nation);
            authority.addSupporter(noble);

            Province province = nation.getContents().get(random.nextInt(nation.getContents().size()));
            City city = province.getContents().get(random.nextInt(province.getContents().size()));
            Quarter quarter = city.getContents().get(random.nextInt(city.getContents().size()));

            noble.getProperty().setLocation(quarter);
            quarter.propertyTracker.addProperty(noble.getProperty());
            quarter.addPop(Status.Noble,noble);

        }

        for (int vang = 0; vang < provinceAmount; vang++) {
            Vanguard vanguard = new Vanguard(authority);
            vanguard.setNation(nation);
            authority.addSupporter(vanguard);

            //  random province from the nation
            Province province = nation.getContents().get(random.nextInt(nation.getContents().size()));
            // random city from the province
            City city = province.getContents().get(random.nextInt(province.getContents().size()));
            // random quarter from the city
            Quarter quarter = city.getContents().get(random.nextInt(city.getContents().size()));

            vanguard.getProperty().setLocation(quarter);
            quarter.propertyTracker.addProperty(vanguard.getProperty());
            quarter.addPop(Status.Vanguard, vanguard);
        }
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
