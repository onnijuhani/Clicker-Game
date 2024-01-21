package model.worldCreation;

import model.NameCreation;
import model.buildings.Property;
import model.buildings.PropertyCreation;
import model.buildings.PropertyTracker;
import model.characters.authority.Authority;
import model.characters.authority.NationAuthority;
import model.characters.npc.King;
import model.characters.npc.Noble;
import model.characters.npc.Vanguard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Continent extends Area implements Details {

    private String name;

    @Override
    public String getName() {
        return this.name;
    }

    private World world;

    @Override
    public Area getHigher() {
        return world;
    }

    private Nation[] nations;


    public Continent(String name, World world) {
        this.propertyTracker = new PropertyTracker();
        this.name = name;
        this.world = world;
        this.createNations();
    }


    public String getDetails() {
        return ("Continent: " + name + " Belongs to: " + world.getName());
    }

    private void createNations() {

        Random random = new Random();
        int numberOfNations = random.nextInt(3) + 1;
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

            int provinceAmount = nation.getContents().size();

            for (int nob = 0; nob < 4; nob++) {
                Noble noble = new Noble(authority);
                authority.addSupporter(noble);
            }

            for (int vang = 0; vang < provinceAmount; vang++) {
                Vanguard vanguard = new Vanguard(authority);
                authority.addSupporter(vanguard);
                vanguard.getProperty().setLocation(nation.getContents().get(vang).getContents().get(0).getContents().get(0));
                nation.getContents().get(vang).getContents().get(0).getContents().get(0).propertyTracker.addProperty(vanguard.getProperty());
            }
        }
    }

    @Override
    public ArrayList<Nation> getContents() {
        return new ArrayList<>(Arrays.asList(nations));
    }

}
