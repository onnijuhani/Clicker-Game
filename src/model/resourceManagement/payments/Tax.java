package model.resourceManagement.payments;

import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.resources.Resource;
import model.resourceManagement.wallets.Wallet;

import java.util.EnumMap;

public class Tax {
    private final EnumMap<Resource, TaxInfo> taxInfoByResource;

    public Tax() {
        taxInfoByResource = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            taxInfoByResource.put(resource, new TaxInfo(60, 0)); // Default tax info
        }
    }

    public void setTaxInfo(Resource resource, int taxPercentage, int minimumTaxableAmount) {
        taxInfoByResource.get(resource).setTaxPercentage(taxPercentage);
        taxInfoByResource.get(resource).setMinimumTaxableAmount(minimumTaxableAmount);
    }

    public TaxInfo getTaxInfo(Resource resource) {
        return taxInfoByResource.get(resource);
    }


    public void collectTax(Wallet fromWallet, EventTracker taxPayer, Wallet toWallet, EventTracker taxMan) {
        double foodTax = taxInfoByResource.get(Resource.Food).calculateTax(fromWallet.getFood().getAmount());
        double alloyTax = taxInfoByResource.get(Resource.Alloy).calculateTax(fromWallet.getAlloy().getAmount());
        double goldTax = taxInfoByResource.get(Resource.Gold).calculateTax(fromWallet.getGold().getAmount());

        TransferPackage transfer = new TransferPackage(foodTax,alloyTax,goldTax);
        toWallet.deposit(fromWallet, transfer);

        taxPayer.addEvent(EventTracker.Message("Major","Tax paid "+transfer.toString()));
        taxMan.addEvent(EventTracker.Message("Major","Tax Collected"+transfer.toString()));
    }

    private double getResourceAmount(Wallet wallet, Resource resource) {
        return switch (resource) {
            case Food -> wallet.getFood().getAmount();
            case Alloy -> wallet.getAlloy().getAmount();
            case Gold -> wallet.getGold().getAmount();
            default -> 0;
        };
    }
    public String toStringTaxRates() {
        StringBuilder taxRatesBuilder = new StringBuilder();
        for (Resource resource : Resource.values()) {
            TaxInfo info = taxInfoByResource.get(resource);
            taxRatesBuilder.append(resource.name()) // First letter of resource name
                    .append(": Tax ")
                    .append(Math.round(info.getTaxPercentage())) // Rounded percentage
                    .append("%\n");
        }
        return taxRatesBuilder.toString().trim(); // Trim to remove the last newline
    }

    public static class TaxInfo {
        private double taxPercentage;
        private int minimumTaxableAmount;

        public TaxInfo(double taxPercentage, int minimumTaxableAmount) {
            this.taxPercentage = taxPercentage;
            this.minimumTaxableAmount = minimumTaxableAmount;
        }

        public void setTaxPercentage(int taxPercentage) {
            this.taxPercentage = taxPercentage;
        }

        public void setMinimumTaxableAmount(int minimumTaxableAmount) {
            this.minimumTaxableAmount = minimumTaxableAmount;
        }

        public double calculateTax(double amount) {
            return amount * (taxPercentage / 100.0);
        }

        public double getTaxPercentage() {
            return taxPercentage;
        }

        public double getMinimumTaxableAmount() {
            return minimumTaxableAmount;
        }
    }
}

