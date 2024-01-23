package model.buildings;

import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Vault;
import model.resourceManagement.wallets.Wallet;

public class Maintenance {
    private double food;
    private double alloy;
    private double gold;

    public Maintenance(PropertyConfig.PropertyValues propertyValues) {
        this.food = propertyValues.food;
        this.alloy = propertyValues.alloy;
        this.gold = propertyValues.gold;
    }

    public void payMaintenance(Property property) {
        TransferPackage maintenanceCost = maintenanceCost();
        Vault propertyVault = property.getVault();
        Wallet ownerWallet = property.getOwner().getWallet();
        String message = "Maintenance paid. " + maintenanceCost.toString();

        if (canPay(maintenanceCost, propertyVault)) {
            propertyVault.subtractResources(maintenanceCost);
            property.getOwner().getEventTracker().addEvent(EventTracker.Message("Minor",message));
        } else if (canPay(maintenanceCost, ownerWallet)) {
            ownerWallet.subtractResources(maintenanceCost);
            property.getOwner().getEventTracker().addEvent(EventTracker.Message("Minor",message));
        } else {
            String errorMessage = "Maintenance not paid" + maintenanceCost.toString();
            property.getOwner().getEventTracker().addEvent(EventTracker.Message("Error",errorMessage));
        }
    }

    private boolean canPay(TransferPackage cost, Wallet wallet) {
        double[] walletResources = wallet.getWalletValues();
        double[] costResources = cost.getAll();
        return walletResources[0] >= costResources[0] &&
                walletResources[1] >= costResources[1] &&
                walletResources[2] >= costResources[2];
    }

    public TransferPackage maintenanceCost() {
        return new TransferPackage(food, alloy, gold);
    }

    @Override
    public String toString() {
        return "Maintenance cost: " + food + " - " + alloy + " - " + gold;
    }
}