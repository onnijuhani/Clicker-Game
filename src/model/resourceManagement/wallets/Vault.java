package model.resourceManagement.wallets;

import model.GameManager;
import model.characters.Person;
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
    public void deleteFromGameManager() {
        GameManager.getAllVaultsInGame().remove(this);
    }

    @Override
    public void deposit(Wallet depositFromWallet, TransferPackage transfer){
        if(!depositFromWallet.hasEnoughResources(transfer)){
            return; // quick return if there isn't enough resources
        }
        lockedResources.addResources(transfer);
        depositFromWallet.subtractResources(transfer);
        TransferPackage lockedAmount = TransferPackage.fromArray(lockedResources.getWalletValues());
        getOwner().getEventTracker().addEvent(EventTracker.Message("Minor", "Deposited resources "+lockedAmount.toShortString()+"\nare locked and will be available after the year ends.\nInterest will not be applied during this time."));

    }

    public void releaseLockedResources(){
        TransferPackage lockedAmount = TransferPackage.fromArray(lockedResources.getWalletValues());
        this.depositAll(lockedResources);
        if( !(lockedAmount.getAll()[0] == 0) && !(lockedAmount.getAll()[1] == 0) && !(lockedAmount.getAll()[2] == 0))  {
            getOwner().getEventTracker().addEvent(EventTracker.Message("Minor", "Locked resources " + lockedAmount.toShortString() + "\nare now available for immediate withdrawal and interest payments at the year end."));
        }
    }


    public void applyInterest(double interestRate) {
        int[] walletValues = getWalletValues();
        int[] interestValues = new int[walletValues.length];

        for (int i = 0; i < walletValues.length; i++) {
            interestValues[i] = (int) (walletValues[i] * interestRate);
        }

        TransferPackage interestTransfer = TransferPackage.fromArray(interestValues);
        addResources(interestTransfer);

        getOwner().getEventTracker().addEvent(EventTracker.Message("Minor","Interest gained "+ interestTransfer + this));

    }


    public void robbery(Person attacker, Person defender) {
        Wallet toWallet = attacker.getWallet();

        int[] halfValues = getHalfValues();

        TransferPackage transfer = TransferPackage.fromArray(halfValues);
        this.subtractResources(transfer);
        toWallet.addResources(transfer);


        attacker.getEventTracker().addEvent(EventTracker.Message("Major", "Robbery successful.\n Gained: " + transfer + " from vault."));
        defender.getEventTracker().addEvent(EventTracker.Message("Major", "Vault robbed.\n Lost: " + transfer + "."));

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

