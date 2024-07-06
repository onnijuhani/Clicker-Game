package model.resourceManagement.wallets;

import model.GameManager;
import model.characters.Person;
import model.characters.payments.PaymentManager;
import model.resourceManagement.TransferPackage;
import model.shop.Ownable;
import model.stateSystem.MessageTracker;

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
    public boolean deposit(Wallet depositFromWallet, TransferPackage transfer){
        if(!depositFromWallet.hasEnoughResources(transfer)){
            return false; // quick return if there isn't enough resources
        }
        lockedResources.addResources(transfer);
        depositFromWallet.subtractResources(transfer);
        TransferPackage lockedAmount = TransferPackage.fromArray(lockedResources.getWalletValues());
        getOwner().getMessageTracker().addMessage(MessageTracker.Message("Minor", "Deposited resources "+lockedAmount.toShortString()+"\nare locked and will be available after the year ends.\nInterest will not be applied during this time."));
        return true;
    }

    public void releaseLockedResources(){
        TransferPackage lockedAmount = TransferPackage.fromArray(lockedResources.getWalletValues());
        this.depositAll(lockedResources);
        if( !(lockedAmount.getAll()[0] == 0) && !(lockedAmount.getAll()[1] == 0) && !(lockedAmount.getAll()[2] == 0))  {
            getOwner().getMessageTracker().addMessage(MessageTracker.Message("Minor", "Locked resources " + lockedAmount.toShortString() + "\nare now available for immediate withdrawal and interest payments at the year end."));
        }
    }


    public void applyInterest(double interestRate) {
        int[] walletValues = getWalletValues();
        int[] interestValues = new int[walletValues.length];

        for (int i = 0; i < walletValues.length; i++) {

            if (walletValues[i] > 100_000_000){
                return;
            }

            interestValues[i] = Math.min(  (int) (walletValues[i] * interestRate), 1_000_000);
        }

        TransferPackage interestTransfer = TransferPackage.fromArray(interestValues);
        addResources(interestTransfer);

        getOwner().getMessageTracker().addMessage(MessageTracker.Message("Minor","Vault interest gained "+ interestTransfer));

    }


    public void robbery(Person attacker, Person defender) {
        Wallet toWallet = attacker.getWallet();

        int[] halfValues = getHalfValues();

        TransferPackage transfer = TransferPackage.fromArray(halfValues);
        this.subtractResources(transfer);
        toWallet.addResources(transfer);


        attacker.getMessageTracker().addMessage(MessageTracker.Message("Major", "Robbery successful.\n Gained: " + transfer + " from vault."));
        defender.getMessageTracker().addMessage(MessageTracker.Message("Major", "Vault robbed.\n Lost: " + transfer + "."));

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
    public MessageTracker getMessageTracker() {
        return null;
    }

    @Override
    public PaymentManager getPaymentManager() {
        return null;
    }


}

