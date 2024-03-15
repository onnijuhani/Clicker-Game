package model.buildings;

import model.buildings.properties.*;
import model.characters.Character;
import model.characters.npc.*;

import java.util.Random;

public class PropertyCreation {

    public static Property createProperty(String name, String area) {

        Random random = new Random();
        double randomValue = random.nextDouble();

        if (area.equals("Nation")) {
            return createNationProperty(name, randomValue);
        } else if (area.equals("Province")){
            return createProvinceProperty(name, randomValue);
        } else if (area.equals("City")) {
            return createCityProperty(name, randomValue);
        } else if (area.equals("Quarter")) {
            return createQuarterProperty(name, randomValue);
        }
        Shack shack = new Shack("empty");
        return shack;
    }

    private static Property createNationProperty(String name, double randomValue) {
        return (randomValue < 0.75) ? new Fortress(name) : new Citadel(name);
    }

    private static Property createProvinceProperty(String name, double randomValue) {
        if (randomValue < 0.4) {
            return new Citadel(name);
        } else if (randomValue < 0.8) {
            return new Castle(name);
        } else {
            return new Manor(name);
        }
    }

    private static Property createCityProperty(String name, double randomValue) {
        if (randomValue < 0.2) {
            return new Castle(name);
        } else if (randomValue < 0.5) {
            return new Manor(name);
        } else if (randomValue < 0.9) {
            return new Mansion(name);
        } else {
            return new Villa(name);
        }
    }

    private static Property createQuarterProperty(String name, double randomValue) {
        if (randomValue < 0.05) {
            return new Mansion(name);
        } else if (randomValue < 0.3) {
            return new Villa(name);
        } else {
            return new Cottage(name);
        }
    }

    public static Property createSupportProperty(Character support){

        Random random = new Random();
        double randomValue = random.nextDouble();

        if (support instanceof Noble){
            return new Manor("Noble's");
        } else if (support instanceof Vanguard) {
            if (randomValue < 0.15) {
                return new Fortress("Vanguard's");
            } else if (randomValue < 0.5){
                return new Citadel("Vanguard's");
            } else {
                return new Castle("Vanguard's");
            }
        } else
            if (randomValue < 0.15) {
                return new Citadel("Mercenary's");
            } else if (randomValue < 0.5){
                return new Castle("Mercenary's");
            } else {
                return new Manor("Mercenary's");
            }
        }

    public static Property createPeasantProperty(Character character){

        if (character instanceof Farmer || character instanceof Miner) {
            Shack shack = new Shack(character.getName());
            shack.setOwner(character);
            return shack;
        } else if (character instanceof Merchant)  {
                Random random = new Random();
                double randomValue = random.nextDouble();

                if (randomValue < 0.05) {
                    Mansion mansion = new Mansion(character.getName());
                    mansion.setOwner(character);
                    return mansion;
                } else if (randomValue < 0.25){
                    Villa villa = new Villa(character.getName());
                    villa.setOwner(character);
                    return villa;
                } else if (randomValue < 0.6){
                    Cottage cottage = new Cottage(character.getName());
                    cottage.setOwner(character);
                    return cottage;
                } else {
                    Shack shack = new Shack(character.getName());
                    shack.setOwner(character);
                    return shack;
                }
            }

        return null;
    }





}
