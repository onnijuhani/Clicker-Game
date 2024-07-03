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
                "Your nation's -" + nationName +"- leader " + authority + " has granted you a gift:\n" + bonusPackage,
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



    public static void triggerFirstSlaveFacilityMessage(){
        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Slave Facility Established",
                "You have opened a section of your own at the local slave facility. " +
                        "This will significantly enhance your ability to obtain resources, but it comes with high moral and social costs. " +
                        "You may later join the Slaver Guild that runs the facility to team up with other citizens who hold slaves there. " +
                        "Be aware that you are now a target for citizens who believe in the liberal rights of everyone. " +
                        "Additionally, you will not be able to construct a Worker Center anymore.",
                "Properties/slaveFacility.jpg",
                "Understood"
        );

        PopUpMessageTracker.sendMessage(message);
    }

    public static void triggerFirstWorkerFacilityMessage(){
        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Worker Center Established",
                "You have opened a section of your own at the local Worker Center. " +
                        "This will significantly improve your resource production by providing a structured and efficient environment for workers. " +
                        "Your liberal stance will attract citizens who value fair treatment and equal rights, increasing your influence among like-minded individuals. " +
                        "However, be aware that you will not be able to construct a Slave Facility, as holding slaves is incompatible with your commitment to fair labor.",
                "Properties/workerCenter.jpg",
                "Progress"
        );

        PopUpMessageTracker.sendMessage(message);
    }

    public static void triggerFirstMysticMineMessage(){
        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Mystical Mine",
                "Mystical Mine is a mysterious facility that generates random amounts of alloys and gold, harnessing the arcane energies hidden beneath the earth." +
                        " The unpredictable output of the mine can lead to unexpected windfalls, providing a unique edge in your resource management." +
                        " However, be prepared for the occasional dry spell, as the mystical forces at play can be capricious.",
                "Properties/mysticMine.jpg",
                "Intriguing"
        );

        PopUpMessageTracker.sendMessage(message);
    }

    public static void triggerFirstGameOverWarning(){
        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Strike Lost",
                "You have missed a payment and lost a strike. Ensure you have sufficient resources to cover all monthly expenses." +
                        " There will be no further warnings when strikes are lost. If you lose all your strikes, the game will end." +
                        " You can view your cash flow, including incomes and expenses, in the Financials tab." +
                        "\nTake immediate action to secure your position.",
                "Properties/gameOverFirstWarning.jpg",
                "Understood"
        );

        PopUpMessageTracker.sendMessage(message);
    }

    public static void triggerGameOverWarning() {
        PopUpMessageTracker.PopUpMessage gameOverMessage = new PopUpMessageTracker.PopUpMessage(
                "Warning",
                "Only 1 strike left\n\nYour journey will end soon..\n\n",
                "Properties/gameOverWarning.jpg",
                "No room for errors..."
        );
        PopUpMessageTracker.sendMessage(gameOverMessage);
    }

    public static boolean sentinelsInfoSent = false;
    public static void triggerSentinelsInfo() {
        if(sentinelsInfoSent){
            return;
        }
        sentinelsInfoSent = true;
        PopUpMessageTracker.PopUpMessage gameOverMessage = new PopUpMessageTracker.PopUpMessage(
                "Who are the Sentinels?",
                "Sentinels are elite citizens dedicated to protecting the highest authorities. " +
                        "They will rally to their Master's defense whenever their authority is challenged. " +
                        "To weaken these guardians, duel them individually and remove them from the Authority battle before challenging Governor or King." +
                        "\n\nGovernors are protected by their Mercenaries, fierce attackers who relentlessly harass any potential threats to their master." +
                        " Kings are defended by Nobles, who wield strategic power against rivaling Nations, and the formidable Vanguards," +
                        " whose devastating strength makes dethroning the King a nearly impossible task.",
                "Properties/sentinels.jpg",
                "Understood"
        );
        PopUpMessageTracker.sendMessage(gameOverMessage);

    }

    public static void triggerEarlyGameInfo() {
        PopUpMessageTracker.PopUpMessage gameOverMessage = new PopUpMessageTracker.PopUpMessage(
                "Getting Started",
                "At the top of the screen is your main wallet, which is currently empty. " +
                        "Resources you generate with the clicker will be added to your work wallet. " +
                        "Every 27th day, your work wallet will be taxed by your local Captain, your direct authority. " +
                        "After taxation, the remaining resources are automatically transferred to your main wallet, where they can be freely spent." +
                        "You have the opportunity to overthrow the Captain, gaining the power to collect tax payments from other Peasants in your area. " +
                        "Ensure you have sufficient resources in your main wallet to cover basic food and maintenance costs." +
                        "\n\nPlan wisely and build your wealth to rise in power!",
                "Properties/earlyGameInfo.jpg",
                "Glory Awaits"
        );
        PopUpMessageTracker.sendMessage(gameOverMessage);
    }

    private static boolean exploreMapInfoSent = false;
    public static void triggerExploreMapInfo() {
        if(exploreMapInfoSent){
            return;
        }
        exploreMapInfoSent = true;
        PopUpMessageTracker.PopUpMessage gameOverMessage = new PopUpMessageTracker.PopUpMessage(
                "Explore the Map",
                "The world of Territorial Clickers is vast and layered. It spans from the overarching World, down through Continents," +
                        " Nations led by Kings, Provinces governed by Governors, Cities overseen by Mayors, and finally, Districts commanded by Captains." +
                        "\n\nThe land is teeming with others just like you, all striving to expand their territorial power. You have the freedom to explore the map and uncover vital information about each area." +
                        "Discover who resides there, assess their wealth and military strength, and use this knowledge to your advantage.",
                "Properties/exploreMapInfo.jpg",
                "Let's Explore"
        );
        PopUpMessageTracker.sendMessage(gameOverMessage);
    }

    public static boolean vaultInfoSent = false;
    public static void triggerVaultInfo() {
        if (vaultInfoSent) {
            return;
        }
        vaultInfoSent = true;
        PopUpMessageTracker.PopUpMessage gameOverMessage = new PopUpMessageTracker.PopUpMessage(
                "Vault",
                "The Vault is a place to invest your resources, locking them until the end of the current year. " +
                        "After this period, your stored wealth will start earning a 15% interest annually. " +
                        "However, beware that your vault can be a target for robbers. " +
                        "Upgrade the defense of your property to safeguard your accumulated wealth. " +
                        "You can withdraw available resources from your vault to your main wallet at any time. " +
                        "Some mandatory payments might automatically be paid from your vault if your main wallet balance is too low." +
                        "In case your main wallet is completely emptied, vault will automatically send required resources to prevent loss of strikes.",
                "Properties/vaultInfo.jpg",
                "Easy money…"
        );
        PopUpMessageTracker.sendMessage(gameOverMessage);
    }

    public static void triggerMarketInfo() {
        PopUpMessageTracker.PopUpMessage gameOverMessage = new PopUpMessageTracker.PopUpMessage(
                "Exchange",
                "You can buy and sell resources at the exchange. Be mindful of the high fees associated with every trade. " +
                        "You are trading in the same market as everyone else in your Nation, so take advantage of fluctuating prices. " +
                        "You can also automate the buying and selling of resources. This feature will sell all selected resources you have, leaving enough to cover your mandatory expenses.",
                "Properties/market.jpg",
                "Understood"
        );
        PopUpMessageTracker.sendMessage(gameOverMessage);
    }

    public static void triggerPeasantInfo() {
        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Who are the Peasants?",
                "Peasants are the backbone of the economy, performing essential tasks and producing vital resources." +
                        " Farmers generate monthly food supplies," +
                        " Miners extract valuable alloys," +
                        " and Merchants accumulate gold. " +
                        "These hardworking citizens are under the authority of their local Captain, who collects taxes from them." +
                        " Despite their crucial role, Peasants often feel dissatisfied with their lowly status and " +
                        "aspire to rise in rank to lighten their burdens and improve their lives.",
                "Properties/peasants.jpg",
                "Understood"
        );
        PopUpMessageTracker.sendMessage(message);
    }
    public static void triggerAuthorityInfo() {
        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Who are the Authorities?",
                "Authorities are the leaders who govern different levels of the realm. " +
                        "Captains oversee the Districts, Mayors govern the Cities, Governors rule the Provinces, " +
                        "and the King commands the entire Nation. Each level of authority has its own responsibilities " +
                        "and powers, from collecting taxes to enforcing laws and protecting their territories. " +
                        "These positions are often contested, as everyone aspires to climb the hierarchy and wield greater power.",
                "Properties/authorities.jpg",
                "Understood"
        );
        PopUpMessageTracker.sendMessage(message);
    }

    public static void triggerPropertyInfo() {
        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Home Sweet Home",
                "Property functions as a home for a citizen. There are monthly maintenance costs that must be paid." +
                        "Property comes with utility slots, that can be used to generate resources automatically on monthly basis." +
                        "You can construct larger property to open new utility slots but maintenance expeneses will also be increased." +
                        "Having a Military property which allows you to start training your own army." +
                        "Increasing property defence makes your property and vault stronger.",
                "Properties/manorPop.jpg",
                "Understood"
        );
        PopUpMessageTracker.sendMessage(message);
    }

    public static void triggerGrandFoundryInfo() {
        PopUpMessageTracker.PopUpMessage message = new PopUpMessageTracker.PopUpMessage(
                "Grand Foundry",
                "The Grand Foundry is a magnificent construction designed to generate vast amounts of resources, easing the burden of army expenses. " +
                        "Over time, it will automatically upgrade itself, significantly boosting its resource production capabilities. " +
                        "\nFurthermore, if you triumph over another military in battle, you will gain control of their Grand Foundry's production for a certain period.",
                "Properties/grandFoundry.png",
                "Exciting"
        );
        PopUpMessageTracker.sendMessage(message);
    }

}
