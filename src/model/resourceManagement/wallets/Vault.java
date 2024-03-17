package model.resourceManagement.wallets;

import model.GameManager;
import model.characters.Character;
import model.resourceManagement.TransferPackage;
import model.shop.Ownable;
import model.stateSystem.EventTracker;

public class Vault extends Wallet implements Ownable {

    private final Wallet lockedResources;

    public Vault(final Ownable owner) {
        super(owner);
        addToGameManager();
        this.lockedResources = new Wallet(this);
    }

    private void addToGameManager(){
        GameManager.getAllVaultsInGame().add(this);
    }

    @Override
    public void deposit(Wallet depositFromWallet, TransferPackage transfer){
        lockedResources.addResources(transfer);
        depositFromWallet.subtractResources(transfer);
        TransferPackage lockedAmount = TransferPackage.fromArray(lockedResources.getWalletValues());
        getOwner().getEventTracker().addEvent(EventTracker.Message("Minor", "Deposited resources "+lockedAmount.toShortString()+" are locked and will be available after the year ends. Interest will not be applied."));

    }

    public void releaseLockedResources(){
        TransferPackage lockedAmount = TransferPackage.fromArray(lockedResources.getWalletValues());
        this.depositAll(lockedResources);
        getOwner().getEventTracker().addEvent(EventTracker.Message("Minor", "Locked resources "+lockedAmount.toShortString()+" are now available for immediate withdrawal and interest payments at the year end."));

    }


    public void applyInterest(double interestRate) {
        int[] walletValues = getWalletValues();
        int[] interestValues = new int[walletValues.length];

        for (int i = 0; i < walletValues.length; i++) {
            interestValues[i] = (int) (walletValues[i] * interestRate);
        }

        TransferPackage interestTransfer = TransferPackage.fromArray(interestValues);
        addResources(interestTransfer);

        getOwner().getEventTracker().addEvent(EventTracker.Message("Utility","Interest gained "+ interestTransfer));

    }


    public void robbery(Character attacker, Character defender) {
        Wallet toWallet = attacker.getWallet();

        int[] halfValues = getHalfValues();

        TransferPackage transfer = TransferPackage.fromArray(halfValues);
        this.subtractResources(transfer);
        toWallet.addResources(transfer);


        attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Robbery successful. Gained: " + transfer + " from vault."));
        defender.getEventTracker().addEvent(EventTracker.Message("Major", "Vault robbed. Lost: " + transfer + "."));

    }

    public int[] getHalfValues() {
        int[] values = this.getWalletValues();
        int[] halfValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            halfValues[i] = values[i] / 2;
        }
        return halfValues;
    }

    @Override
    public EventTracker getEventTracker() {
        return null;
    }
}

