package model.war;

import model.Settings;
import model.worldCreation.Nation;

public class WarService {


    /**
     * @return returns Null if there is an error starting the War. Otherwise, returns the War object.
     */
    public static War startWar(Nation attacker, Nation defender) {

        if(attacker == defender){
            throw new RuntimeException(attacker+"Tried to enter war against itself");
        }
        if(attacker.isVassal()){
            if(defender != attacker.getOverlord()) {
                System.out.println(attacker + " is a Vassal. Tried to enter a war against: " + defender + "\nVassals can only attack their overlords");
                return null;
            }
        }
        if(defender.isVassal()){
            System.out.println(defender + " is a Vassal. Attempted attack done by: " + attacker);
            return null;
        }

        String warName = getWarName(attacker, defender);

//        TransferPackage cost = attacker.calculateWarStartingCost(defender);
//        Person king = Model.getPlayerAsPerson();
//        if(attacker.getWallet().subtractResources(cost)){
//            king.getMessageTracker().addMessage(MessageTracker.Message("Major", "War cost paid: " + cost));
//            king.getMessageTracker().addMessage(MessageTracker.Message("Major", warName + " started."));
//        }else{
//            king.getMessageTracker().addMessage(MessageTracker.Message(
//                    "Major",
//                    "Not enough resources to start a war against " + defender + "\n" +
//                     "Required: " + cost.toShortString() + "\nAvailable in National Wallet: " + attacker.getWallet().getBalance().toShortString()
//            ));
//            return null;
//        }




        War war = new War(attacker, defender, warName);

        return war;

    }

    private static String getWarName(Nation attacker, Nation defender) {
        String warName;

        if(attacker.isVassal()){
            if(attacker.getOverlord() == defender){
                warName = Settings.removeUiNameAddition(attacker.getName()) + "'s War for Independence";
            }else{
                throw new RuntimeException("Vassal attempted to enter a war against someone else than their overlord." +
                        " Overlord: " + attacker.getOverlord() + ". Target: " + defender);
            }
        }else if(attacker.getVassals().size() >= 2){
            warName = Settings.removeUiNameAddition(attacker.getName()) + "'s War for Supremacy";
        }else{
            warName = Settings.removeUiNameAddition(attacker.getName()) + "'s War for Conquest";
        }
        return warName;
    }


}
