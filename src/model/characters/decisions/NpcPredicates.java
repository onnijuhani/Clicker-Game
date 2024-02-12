package model.characters.decisions;

import model.characters.Character;
import model.characters.authority.Authority;
import model.resourceManagement.Resource;
import model.worldCreation.Quarter;

import java.util.function.Predicate;

public class NpcPredicates {


    /**
     * total amount, including wallet and vault
     */
    public static Predicate<Character> isWealthy() {
        return character -> {
            boolean walletAmount = character.getWallet().hasResources(5000,5000,10000);
            boolean vaultAmount = character.getProperty().getVault().hasResources(5000,5000,10000);
            return walletAmount && vaultAmount;
        };
    }


    /**
     * total amount, including wallet and vault
     */
    public static Predicate<Character> hasCombinedResource(Resource type, int minimumAmount) {
        return character -> {
            int walletAmount = character.getWallet().getResource(type);
            int vaultAmount = character.getProperty().getVault().getResource(type);
            return (walletAmount + vaultAmount) >= minimumAmount;
        };
    }


    /**
     * checks if character lives in a certain quarter
     */
    public static Predicate<Character> livesInQuarter(Quarter quarter) {
        return character -> quarter.equals(character.getProperty().getLocation());
    }


    /**
     * checks if character is under the authority of certain Authority
     */
    public static Predicate<Character> isSubordinateTo(Authority authority){
        return character -> authority.equals(character.getAuthority());
    }





}
