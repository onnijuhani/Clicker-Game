package model.buildings;

import model.GameManager;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.payments.PaymentTracker;
import model.stateSystem.MessageTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Vault;
import model.resourceManagement.wallets.Wallet;

public class Maintenance implements PaymentTracker {
    private final int food;
    private final int alloy;
    private final int gold;

    public Maintenance(PropertyConfig.PropertyValues propertyValues) {
        this.food = propertyValues.food;
        this.alloy = propertyValues.alloy;
        this.gold = propertyValues.gold;
    }

    public void payMaintenance(Property property) {
        TransferPackage maintenanceCost = getMaintenanceCost();
        Vault propertyVault = property.getVault();
        Wallet ownerWallet = property.getOwner().getWallet();
        String message = "Maintenance paid. " + maintenanceCost.toString();

        boolean isPlayer = property.getOwner().isPlayer();

        if (canPay(maintenanceCost, ownerWallet)) {
            ownerWallet.subtractResources(maintenanceCost);
            if(isPlayer) {
                property.getOwner().getEventTracker().addEvent(MessageTracker.Message("Minor", message));
            }
        } else if (canPay(maintenanceCost, propertyVault)) {
            propertyVault.subtractResources(maintenanceCost);
            if(isPlayer) {
                property.getOwner().getEventTracker().addEvent(MessageTracker.Message("Minor", message));
            }
        } else {
            String errorMessage = "Maintenance not paid " + maintenanceCost;
            if(isPlayer) {
                property.getOwner().getEventTracker().addEvent(MessageTracker.Message("Error", errorMessage));
            }
            property.getOwner().loseStrike();
        }
    }

    private boolean canPay(TransferPackage cost, Wallet wallet) {
        int[] walletResources = wallet.getWalletValues();
        int[] costResources = cost.getAll();
        return walletResources[0] >= costResources[0] &&
                walletResources[1] >= costResources[1] &&
                walletResources[2] >= costResources[2];
    }

    public TransferPackage getMaintenanceCost() {
        return new TransferPackage(food, alloy, gold);
    }




    @Override
    public String toString() {
        return food + " Food " + "\n" +
                alloy + " Alloys " + "\n" +
                 gold + " Gold";
    }

    @Override
    public void updatePaymentManager(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.EXPENSE, Payment.MAINTENANCE_EXPENSE, getMaintenanceCost(), GameManager.getMaintenanceDay());
    }
}
