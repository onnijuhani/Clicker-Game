package model.worldCreation;

import model.Model;
import model.NameCreation;
import model.Settings;

import java.util.*;

public class World extends Area implements Details {


    private static final HashSet<Quarter> allQuarters = new HashSet<>();

    private static final HashSet<Nation> allNations = new HashSet<>();
    private static final HashSet<Nation> allNonPlayerNations = new HashSet<>();
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

        calculateAllQuarters();
        calculateAllNations();
    }

    private void calculateAllQuarters(){
        for(Continent continent : continents){
            for(Nation nation : continent.getContents()){
                allQuarters.addAll(nation.getAllQuarters());
            }
        }
    }

    private void calculateAllNations(){
        for(Continent continent : continents){
            allNations.addAll(continent.getContents());
        }
    }

    public void calculateAllNonPlayerNations(){
        for(Continent continent : continents){
            for(Nation nation : continent.getContents()){
                if(nation == Model.getPlayerRole().getNation()){
                    continue;
                }
                allNonPlayerNations.add(nation);
            }
        }
    }
    public static HashSet<Nation> getAllNations() {
        return allNations;
    }

    public static HashSet<Nation> getAllNonPlayerNations() {
        return allNonPlayerNations;
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

