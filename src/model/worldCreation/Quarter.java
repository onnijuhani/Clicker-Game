package model.worldCreation;

import model.buildings.PropertyTracker;
import model.characters.authority.Authority;
import model.characters.Character;
import model.characters.authority.QuarterAuthority;
import model.characters.npc.Farmer;
import model.characters.npc.Merchant;
import model.characters.npc.Miner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Quarter extends ControlledArea implements Details {

    private String name;

    @Override
    public String getName() {
        return this.name;
    }

    private HashMap<String, ArrayList<Character>> populationList;

    public HashMap<String, ArrayList<Character>> getPopulationList() {
        return populationList;
    }

    private PropertyTracker allProperties;
    private City city;
    private int numOfPeasants;

    public Quarter(String name, City city, Authority authority) {
        this.name = name;
        this.city = city;
        this.propertyTracker = new PropertyTracker();
        this.authority = authority;
        Authority captain = this.authority;
        this.populationList = new HashMap<>();
        captain.getCharacter().setNation(city.getProvince().getNation());
        captain.setAuthUnder(city.getAuthority());
        createPeasants();
    }

    public String returnAllInformation() {
        Quarter theQuarter = this;
        City theCity = city;
        Province theProvince = city.getProvince();
        Nation theNation = theProvince.getNation();

        return (
                "Quarter: " + theQuarter +
                        ". City: " + theCity +
                        ". Province: " + theProvince +
                        ". Nation: " + theNation +
                        ". Population: " + numOfPeasants
        );
    }


    @Override
    public Area getHigher() {
        return city;
    }

    public String getDetails() {
        return ("Quarter: " + name + " Belongs to city: " + city.getName());
    }

    @Override
    public ArrayList<Quarter> getContents() {
        return new ArrayList<>();
    }

    public String fullHierarchyInfo() {
        Province prov = city.getProvince();
        Nation nat = prov.getNation();
        return name + "\n" +
                "Inside: " + city.getName() + " - City\n" +
                "Of the: " + prov.getName() + " - Province\n" +
                "Part of: " + nat.getName() + " - Nation\n" +
                "In the: " + nat.getHigher().getName() + " - Continent\n" +
                "Of the Mighty: " + nat.getHigher().getHigher().getName();
    }

    private void createPeasants() {
        QuarterAuthority captain = (QuarterAuthority) this.authority;
        captain.getCharacter().setNation(this.city.getProvince().getNation());

        Random random = new Random();
        int numberOfFarmers = random.nextInt(150) + 3;
        numOfPeasants += numberOfFarmers;
        int numberOfMiners = random.nextInt(130) + 3;
        numOfPeasants += numberOfMiners;
        int numberOfMerchants = random.nextInt(50) + 3;
        numOfPeasants += numberOfMerchants;

        ArrayList<Character> farmers = new ArrayList<>();
        ArrayList<Character> miners = new ArrayList<>();
        ArrayList<Character> merchants = new ArrayList<>();

        for (int peasant = 0; peasant < numberOfFarmers; peasant++) {
            Farmer farmer = new Farmer(captain);
            captain.addPeasant(farmer);
            farmers.add(farmer);
        }
        for (int peasant = 0; peasant < numberOfMiners; peasant++) {
            Miner miner = new Miner(captain);
            captain.addPeasant(miner);
            miners.add(miner);
        }
        for (int peasant = 0; peasant < numberOfMerchants; peasant++) {
            Merchant merch = new Merchant(captain);
            captain.addPeasant(merch);
            merchants.add(merch);
        }
        populationList.put("farmers", farmers);
        populationList.put("miners", miners);
        populationList.put("merchants", merchants);
    }
}
