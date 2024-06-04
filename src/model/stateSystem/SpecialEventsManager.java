package model.stateSystem;

import model.Model;
import model.characters.Person;
import model.characters.authority.Authority;
import model.resourceManagement.TransferPackage;

import java.util.Random;

public class SpecialEventsManager {

    public static void triggerStartingBonus() {
        Person player = Model.getPlayerAsPerson();
        Authority authority = player.getRole().getNation().getAuthorityHere();

        Random random = new Random();
        int food = random.nextInt(20) + 10;
        int alloy = random.nextInt(45) + 20;
        int gold = random.nextInt(30) + 15;
        TransferPackage bonusPackage = new TransferPackage(food, alloy, gold);

        player.getWallet().addResources(bonusPackage);

        PopUpMessageTracker.sendMessage("Your nation's leader " + authority + " has granted you a bonus: " + bonusPackage);
    }





}
