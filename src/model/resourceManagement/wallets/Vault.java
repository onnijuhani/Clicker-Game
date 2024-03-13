package model.resourceManagement.wallets;

import model.characters.Character;
import model.stateSystem.EventTracker;
import model.resourceManagement.TransferPackage;

public class Vault extends Wallet {

    public Vault() {
    }

    public void robbery(Character attacker, Character defender) {
        Wallet toWallet = attacker.getWallet();
        int[] values = this.getWalletValues();


        int[] halfValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            halfValues[i] = values[i] / 2;
        }

        TransferPackage transfer = TransferPackage.fromArray(halfValues);
        this.subtractResources(transfer);
        toWallet.addResources(transfer);


        attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Added " + transfer + " to wallet"));
        defender.getEventTracker().addEvent(EventTracker.Message("Major", "lost " + transfer + " from Vault"));
    }
}

