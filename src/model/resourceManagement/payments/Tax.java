package model.resourceManagement.payments;

import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;

import java.util.EnumMap;

public class Tax {
    private final EnumMap<Resource, TaxInfo> taxInfoByResource;

    public Tax() {
        taxInfoByResource = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            taxInfoByResource.put(resource, new TaxInfo(60)); // Default tax info
        }
    }

    public void setTaxInfo(Resource resource, int taxPercentage) {
        taxInfoByResource.get(resource).setTaxPercentage(taxPercentage);

    }

    public TaxInfo getTaxInfo(Resource resource) {
        return taxInfoByResource.get(resource);
    }


    public void collectTax(WorkWallet fromWallet, EventTracker taxPayerTracker, WorkWallet toWallet, EventTracker taxManTracker) {
        int foodTax = (int) taxInfoByResource.get(Resource.Food).calculateTax(fromWallet.getFood());
        int alloyTax = (int) taxInfoByResource.get(Resource.Alloy).calculateTax(fromWallet.getAlloy());
        int goldTax = (int) taxInfoByResource.get(Resource.Gold).calculateTax(fromWallet.getGold());

        TransferPackage transfer = new TransferPackage(foodTax,alloyTax,goldTax);
        toWallet.deposit(fromWallet, transfer);

        fromWallet.setTaxedOrNot(true);
        fromWallet.cashOutSalary(); //after taxation salary is added to the main wallet of the owner. This wallet cannot be taxed.

        String taxPaid = EventTracker.Message("Major","Tax paid "+transfer);
        String taxCollected = EventTracker.Message("Major","Tax Collected"+transfer);
        taxPayerTracker.addEvent(taxPaid);
        taxManTracker.addEvent(taxCollected);


    }

    private int getResourceAmount(Wallet wallet, Resource resource) {
        return switch (resource) {
            case Food -> wallet.getFood();
            case Alloy -> wallet.getAlloy();
            case Gold -> wallet.getGold();
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


        public TaxInfo(double taxPercentage) {
            this.taxPercentage = taxPercentage;

        }

        public void setTaxPercentage(int taxPercentage) {
            this.taxPercentage = taxPercentage;
        }



        public double calculateTax(double amount) {
            return amount * (taxPercentage / 100.0);
        }

        public double getTaxPercentage() {
            return taxPercentage;
        }


    }
}

