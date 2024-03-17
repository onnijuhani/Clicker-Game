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
        return new Shack("empty", owner.getPerson());
    }

    private static Property createNationProperty(String name, double randomValue, Character owner) {
        return (randomValue < 0.75) ? new Fortress(name, owner.getPerson()) : new Citadel(name, owner.getPerson());
    }

    private static Property createProvinceProperty(String name, double randomValue, Character owner) {
        if (randomValue < 0.4) {
            return new Citadel(name, owner.getPerson());
        } else if (randomValue < 0.8) {
            return new Castle(name, owner.getPerson());
        } else {
            return new Manor(name, owner.getPerson());
        }
    }

    private static Property createCityProperty(String name, double randomValue, Character owner) {
        if (randomValue < 0.2) {
            return new Castle(name, owner.getPerson());
        } else if (randomValue < 0.5) {
            return new Manor(name, owner.getPerson());
        } else if (randomValue < 0.9) {
            return new Mansion(name, owner.getPerson());
        } else {
            return new Villa(name, owner.getPerson());
        }
    }

    private static Property createQuarterProperty(String name, double randomValue, Character owner) {
        if (randomValue < 0.05) {
            return new Mansion(name, owner.getPerson());
        } else if (randomValue < 0.3) {
            return new Villa(name, owner.getPerson());
        } else {
            return new Cottage(name, owner.getPerson());
        }
    }

    public static Property createSupportProperty(Character support){

        Random random = new Random();
        double randomValue = random.nextDouble();

        if (support instanceof Noble){
            return new Manor("Noble's", support.getPerson());
        } else if (support instanceof Vanguard) {
            if (randomValue < 0.15) {
                return new Fortress("Vanguard's", support.getPerson());
            } else if (randomValue < 0.5){
                return new Citadel("Vanguard's", support.getPerson());
            } else {
                return new Castle("Vanguard's", support.getPerson());
            }
        } else
            if (randomValue < 0.15) {
                return new Citadel("Mercenary's", support.getPerson());
            } else if (randomValue < 0.5){
                return new Castle("Mercenary's", support.getPerson());
            } else {
                return new Manor("Mercenary's", support.getPerson());
            }
        }

    public static Property createPeasantProperty(Character character){

        if (character instanceof Farmer || character instanceof Miner) {
            Shack shack = new Shack(character.getName(), character.getPerson());
            shack.setOwner(character.getPerson());
            return shack;
        } else if (character instanceof Merchant)  {
                Random random = new Random();
                double randomValue = random.nextDouble();

                if (randomValue < 0.05) {
                    Mansion mansion = new Mansion(character.getName(), character.getPerson());
                    mansion.setOwner(character.getPerson());
                    return mansion;
                } else if (randomValue < 0.25){
                    Villa villa = new Villa(character.getName(), character.getPerson());
                    villa.setOwner(character.getPerson());
                    return villa;
                } else if (randomValue < 0.6){
                    Cottage cottage = new Cottage(character.getName(), character.getPerson());
                    cottage.setOwner(character.getPerson());
                    return cottage;
                } else {
                    Shack shack = new Shack(character.getName(), character.getPerson());
                    shack.setOwner(character.getPerson());
                    return shack;
                }
            }

        return null;
    }





}
