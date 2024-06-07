package model.stateSystem;

import model.Model;
import model.Settings;
import model.characters.Person;
import model.characters.authority.Authority;
import model.resourceManagement.TransferPackage;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SpecialEventsManager {

    public static void triggerStartingBonus() {
        Person player = Model.getPlayerAsPerson();
        Authority authority = player.getRole().getNation().getAuthorityHere();

        Random random = Settings.getRandom();
        int food = random.nextInt(20) + 10;
        int alloy = random.nextInt(45) + 20;
        int gold = random.nextInt(30) + 15;
        TransferPackage bonusPackage = new TransferPackage(food, alloy, gold);

        player.getWallet().addResources(bonusPackage);

        String nationName = player.getRole().getNation().toString();
        if (nationName.contains("(Home)")) {
            nationName = nationName.replace("(Home)", "").trim();
        }

        List<String> answers = Arrays.asList("Thank you!", "One day I will be King..", "A precious gift");

        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Gift From The King",
                "Your nation's -" + nationName +"- leader " + authority + " has granted you a bonus:\n" + bonusPackage,
                "Properties/kingPic.png",
                PopUpMessageTracker.getRandomButtonText(answers)
        );

        PopUpMessageTracker.sendMessage(message);

    }

    public static void triggerStartingMessage() {
        Person player = Model.getPlayerAsPerson();
        String characterName = player.getName();

        if (characterName.contains("(you)")) {
            characterName = characterName.replace("(you)", "").trim();
        }

        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Welcome to Territorial Clickers",
                "You have finally made a name for yourself in this tumultuous land, " + characterName + "." +
                        " After years of struggle, you have secured your first humble abode—a small but sturdy house that you can call your own." +
                        " The days of uncertainty and wandering are behind you, but the real challenge lies ahead. The land around you is rich with resources, waiting to be harnessed." +
                        " Increase your wealth, train your strength, engage in diplomacy and combat, and leave your mark." +
                        " Conquer your territory and rise to greatness.\n\nYour journey begins now.",
                "Properties/StartingShack.jpg",
                "Begin!"
        );

        PopUpMessageTracker.sendMessage(message);
    }





}
