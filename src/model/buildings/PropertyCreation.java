package model.buildings;

import model.Settings;
import model.buildings.properties.*;
import model.characters.Peasant;
import model.characters.Person;
import model.characters.npc.Merchant;
import model.characters.npc.Noble;
import model.characters.npc.Vanguard;

import java.util.Random;

public class PropertyCreation {

    public static Property createProperty(String name, String area, Person owner) {

        Random random = Settings.getRandom();
        double randomValue = random.nextDouble();

        return switch (area) {
            case "Nation" -> createNationProperty(name, randomValue, owner);
            case "Province" -> createProvinceProperty(name, randomValue, owner);
            case "City" -> createCityProperty(name, randomValue, owner);
            case "Quarter" -> createQuarterProperty(name, randomValue, owner);
            default -> new Shack("empty", owner.getPerson());
        };
    }

    private static Property createNationProperty(String name, double randomValue, Person owner) {
        return (randomValue < 0.75) ? new Fortress(name, owner.getPerson()) : new Citadel(name, owner.getPerson());
    }

    private static Property createProvinceProperty(String name, double randomValue, Person owner) {
        if (randomValue < 0.4) {
            return new Citadel(name, owner.getPerson());
        } else if (randomValue < 0.8) {
            return new Castle(name, owner.getPerson());
        } else {
            return new Manor(name, owner.getPerson());
        }
    }

    private static Property createCityProperty(String name, double randomValue, Person owner) {
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

    private static Property createQuarterProperty(String name, double randomValue, Person owner) {
        if (randomValue < 0.05) {
            return new Mansion(name, owner.getPerson());
        } else if (randomValue < 0.3) {
            return new Villa(name, owner.getPerson());
        } else {
            return new Cottage(name, owner.getPerson());
        }
    }

    public static Property createSupportProperty(Person support){

        Random random = Settings.getRandom();
        double randomValue = random.nextDouble();

        if (support.getCharacter() instanceof Noble){
            return new Manor("Noble's", support.getPerson());
        } else if (support.getCharacter() instanceof Vanguard) {
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

    public static Property createPeasantProperty(Person person) {

        if (person.getCharacter() instanceof Merchant) {
            Random random = Settings.getRandom();
            double randomValue = random.nextDouble();
            if (randomValue < 0.05) {
                Mansion mansion = new Mansion(person.getName(), person.getPerson());
                mansion.setOwner(person.getPerson());
                return mansion;
            } else if (randomValue < 0.25) {
                Villa villa = new Villa(person.getName(), person.getPerson());
                villa.setOwner(person.getPerson());
                return villa;
            } else if (randomValue < 0.6) {
                Cottage cottage = new Cottage(person.getName(), person.getPerson());
                cottage.setOwner(person.getPerson());
                return cottage;
            } else {
                Shack shack = new Shack(person.getName(), person.getPerson());
                shack.setOwner(person.getPerson());
                return shack;
            }
        } else if (person.getCharacter() instanceof Peasant) {
            Shack shack = new Shack(person.getName(), person.getPerson());
            shack.setOwner(person.getPerson());
            return shack;

        }
        return null;
    }


}
