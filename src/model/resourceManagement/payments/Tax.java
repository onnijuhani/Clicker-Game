package model.resourceManagement.payments;

import model.stateSystem.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;

import java.util.EnumMap;

public class Tax {
    private final EnumMap<Resource, TaxInfo> taxInfoByResource;
    private boolean isTaxRateChanged = true;
    private double taxRate;

    public Tax() {
        taxInfoByResource = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            taxInfoByResource.put(resource, new TaxInfo(60)); // Default tax info
        }
    }

    public void setTaxInfo(Resource resource, int taxPercentage) {
        taxInfoByResource.get(resource).setTaxPercentage(taxPercentage);

    }

    public void setExtremeTaxRate(){
        taxInfoByResource.get(Resource.Food).setTaxPercentage(80);
        taxInfoByResource.get(Resource.Alloy).setTaxPercentage(80);
        taxInfoByResource.get(Resource.Gold).setTaxPercentage(80);
        isTaxRateChanged = true;
    }

    public void setStandardTaxRate(){taxInfoByResource.get(Resource.Food).setTaxPercentage(60);
       taxInfoByResource.get(Resource.Alloy).setTaxPercentage(60);
       taxInfoByResource.get(Resource.Food).setTaxPercentage(60);
       taxInfoByResource.get(Resource.Gold).setTaxPercentage(60);
       isTaxRateChanged = true;
    }

    public void setMediumTaxRate(){
        taxInfoByResource.get(Resource.Food).setTaxPercentage(40);
        taxInfoByResource.get(Resource.Alloy).setTaxPercentage(40);
        taxInfoByResource.get(Resource.Gold).setTaxPercentage(40);
        isTaxRateChanged = true;
    }

    public void setLowTaxRate(){
        taxInfoByResource.get(Resource.Food).setTaxPercentage(20);
        taxInfoByResource.get(Resource.Alloy).setTaxPercentage(20);
        taxInfoByResource.get(Resource.Gold).setTaxPercentage(20);
        isTaxRateChanged = true;
    }


    /**
     * Set custom tax rate between 20-80 to any resource
     * @param type the resource type that taxRate is being changed
     * @param intPercentage new tax rate, insert as integer
     */
    public void setAnyTaxRate(Resource type, int intPercentage){

        if(intPercentage < 20 || intPercentage > 80){
            return; // these are the limits
        }

        taxInfoByResource.get(type).setTaxPercentage(intPercentage);

        isTaxRateChanged = true;

    }



    public TaxInfo getTaxInfo(Resource resource) {
        return taxInfoByResource.get(resource);
    }

    public double getTaxRate() {
        if (isTaxRateChanged) {
            taxRate = (getTaxInfo(Resource.Food).taxPercentage
                    + getTaxInfo(Resource.Alloy).taxPercentage
                    + getTaxInfo(Resource.Gold).taxPercentage) / 3;
            isTaxRateChanged = false;
            return taxRate;
        }
        return taxRate;
    }


    public void collectTax(WorkWallet fromWallet, EventTracker taxPayerTracker, WorkWallet toWallet, EventTracker taxManTracker) {
        int foodTax = (int) taxInfoByResource.get(Resource.Food).calculateTax(fromWallet.getFood());
        int alloyTax = (int) taxInfoByResource.get(Resource.Alloy).calculateTax(fromWallet.getAlloy());
        int goldTax = (int) taxInfoByResource.get(Resource.Gold).calculateTax(fromWallet.getGold());

        TransferPackage transfer = new TransferPackage(foodTax,alloyTax,goldTax);
        toWallet.deposit(fromWallet, transfer);

        fromWallet.setTaxedOrNot(true);
        fromWallet.cashOutSalary(); //after taxation salary is added to the main wallet of the owner. This wallet cannot be taxed.

        String taxPaid = EventTracker.Message("Minor","Tax paid "+transfer);
        String taxCollected = EventTracker.Message("Minor","Tax Collected"+transfer);
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

