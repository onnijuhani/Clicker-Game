package model.buildings;

import model.buildings.properties.*;
import model.characters.Character;
import model.characters.npc.*;

import java.util.Random;

public class PropertyCreation {

    public static Property createProperty(String name, String area, Character owner) {

        Random random = new Random();
        double randomValue = random.nextDouble();

        if (area.equals("Nation")) {
            return createNationProperty(name, randomValue, owner);
        } else if (area.equals("Province")){
            return createProvinceProperty(name, randomValue, owner);
        } else if (area.equals("City")) {
            return createCityProperty(name, randomValue, owner);
        } else if (area.equals("Quarter")) {
            return createQuarterProperty(name, randomValue, owner);
        }
        return new Shack("empty", owner);
    }

    private static Property createNationProperty(String name, double randomValue, Character owner) {
        return (randomValue < 0.75) ? new Fortress(name, owner) : new Citadel(name, owner);
    }

    private static Property createProvinceProperty(String name, double randomValue, Character owner) {
        if (randomValue < 0.4) {
            return new Citadel(name, owner);
        } else if (randomValue < 0.8) {
            return new Castle(name, owner);
        } else {
            return new Manor(name, owner);
        }
    }

    private static Property createCityProperty(String name, double randomValue, Character owner) {
        if (randomValue < 0.2) {
            return new Castle(name, owner);
        } else if (randomValue < 0.5) {
            return new Manor(name, owner);
        } else if (randomValue < 0.9) {
            return new Mansion(name, owner);
        } else {
            return new Villa(name, owner);
        }
    }

    private static Property createQuarterProperty(String name, double randomValue, Character owner) {
        if (randomValue < 0.05) {
            return new Mansion(name, owner);
        } else if (randomValue < 0.3) {
            return new Villa(name, owner);
        } else {
            return new Cottage(name, owner);
        }
    }

    public static Property createSupportProperty(Character support){

        Random random = new Random();
        double randomValue = random.nextDouble();

        if (support instanceof Noble){
            return new Manor("Noble's", support);
        } else if (support instanceof Vanguard) {
            if (randomValue < 0.15) {
                return new Fortress("Vanguard's", support);
            } else if (randomValue < 0.5){
                return new Citadel("Vanguard's", support);
            } else {
                return new Castle("Vanguard's", support);
            }
        } else
            if (randomValue < 0.15) {
                return new Citadel("Mercenary's", support);
            } else if (randomValue < 0.5){
                return new Castle("Mercenary's", support);
            } else {
                return new Manor("Mercenary's", support);
            }
        }

    public static Property createPeasantProperty(Character character){

        if (character instanceof Farmer || character instanceof Miner) {
            Shack shack = new Shack(character.getName(), character);
            shack.setOwner(character);
            return shack;
        } else if (character instanceof Merchant)  {
                Random random = new Random();
                double randomValue = random.nextDouble();

                if (randomValue < 0.05) {
                    Mansion mansion = new Mansion(character.getName(), character);
                    mansion.setOwner(character);
                    return mansion;
                } else if (randomValue < 0.25){
                    Villa villa = new Villa(character.getName(), character);
                    villa.setOwner(character);
                    return villa;
                } else if (randomValue < 0.6){
                    Cottage cottage = new Cottage(character.getName(), character);
                    cottage.setOwner(character);
                    return cottage;
                } else {
                    Shack shack = new Shack(character.getName(), character);
                    shack.setOwner(character);
                    return shack;
                }
            }

        return null;
    }





}
