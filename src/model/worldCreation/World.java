package model.worldCreation;

import model.NameCreation;
import model.Settings;

import java.util.*;

public class World extends Area implements Details {


    private static HashSet<Quarter> allQuarters;
    private final String name;
    @Override
    public String getName() {
        return this.name;
    }
    private Continent[] continents;
    @Override
    public Area getHigher() {
        return this;
    }

    public Size size;

    @Override
    public List<Continent> getContents() {
        return new ArrayList<>(Arrays.asList(continents));
    }

    public World(String name, Size size){
        this.name = name;
        this.size = size;
        this.createContinents();
        allQuarters = new HashSet<>();
        calculateAllQuarters();
    }

    private void calculateAllQuarters(){
        for(Continent continent : continents){
            for(Nation nation : continent.getContents()){
                allQuarters.addAll(nation.getAllQuarters());
            }
        }
    }



    // Method to create random number of continents
    private void createContinents() {
        Random random = Settings.getRandom();
        int numberOfContinents = size == Size.SMALL ? 2 : size == Size.MEDIUM ? 4 : size == Size.LARGE ? 6 : -1;
        continents = new Continent[numberOfContinents];
        for (int i = 0; i < numberOfContinents; i++) {
            continents[i] = new Continent(NameCreation.generateContinentName(), this);
        }
    }

    // Print continent details from World object
    public void printContinents() {
        for (Continent continent : continents) {
            continent.getDetails();
        }
    }

    public String getDetails() {
        return("World Name: " + this.getName());
    }
    public static HashSet<Quarter> getAllQuarters() {
        return allQuarters;
    }


}

