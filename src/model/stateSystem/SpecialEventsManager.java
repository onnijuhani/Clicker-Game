package model.stateSystem;

import model.Model;
import model.Settings;
import model.characters.Person;
import model.characters.authority.*;
import model.resourceManagement.TransferPackage;
import model.time.Time;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class SpecialEventsManager {
    private static boolean firstQuarterAuthPosReached = false;
    private static boolean firstCityAuthPosReached = false;
    private static boolean firstProvinceAuthPosReached = false;
    private static boolean firstNationAuthPosReached = false;

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

    public static String getFirstAuthorityPositionMessage(Authority authority) {

        if (authority instanceof QuarterAuthority) {
            if (firstQuarterAuthPosReached) {
                return "";
            }
            String message = "\n\nYou have now reached your first Authority Position as a Captain in your home quarter." +
                    " Every " + Time.quarterTax + "th day, you will automatically collect tax payments from workers under your authority." +
                    " You also gain the ability to increase or decrease tax rates. You will now pay taxes to the Mayor of your city.";
            firstQuarterAuthPosReached = true;
            return message;
        }

        if (authority instanceof CityAuthority) {
            if (firstCityAuthPosReached) {
                return "";
            }
            String message = "\n\nYou have now reached an Authority Position as a Mayor in a city." +
                    " Every " + Time.cityTax + "th day, you will automatically collect tax payments from Captains in your city and pay your own taxes" +
                    " to the Governor of the Province. Note that the Governor has the support of fierce Mercenaries. Take them down before attempting to challenge your Governor.";
            firstCityAuthPosReached = true;
            return message;
        }

        if (authority instanceof ProvinceAuthority) {
            if (firstProvinceAuthPosReached) {
                return "";
            }
            String message = "\n\nYou have now reached an Authority Position as a Governor in a Province." +
                    " Every " + Time.provinceTax + "th day, you will automatically collect tax payments from Mayors in your province and pay your own taxes" +
                    " directly to the King of your Nation. This position also comes with direct supporters known as Mercenaries who will fight on your side if attacked. However, you will" +
                    " have to pay them a salary from the tax money you collect (automatic).";
            firstProvinceAuthPosReached = true;
            return message;
        }

        if (authority instanceof NationAuthority) {
            if (firstNationAuthPosReached) {
                return "";
            }
            String message = "\n\nYou have now reached the highest Authority Position as the King of your Nation." +
                    " Every " + Time.nationTax + "th day, you will automatically collect tax payments from Governors in your nation." +
                    " As a King, you receive your own Vanguards, the most powerful fighters in the world. You will also have 4 Nobles to help you gain influence over rivaling Nations.";
            firstNationAuthPosReached = true;
            return message;
        }

        return "";
    }





}
