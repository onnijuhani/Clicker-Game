package model.resourceManagement.payments;

import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.WorkWallet;
import model.stateSystem.EventTracker;

import java.util.EnumMap;

public class Tax {
    private final EnumMap<Resource, TaxInfo> taxInfoByResource;
    private boolean isTaxRateChanged = true;
    private double taxRate;

    public TaxRate getCurrentTaxRate() {
        return currentTaxRate;
    }

    private TaxRate currentTaxRate;

    public enum TaxRate {
        LOW, MEDIUM, STANDARD, EXTREME
    }

    public Tax() {
        taxInfoByResource = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            taxInfoByResource.put(resource, new TaxInfo(60));
        }
        currentTaxRate = TaxRate.STANDARD;
    }

    public void setTaxInfo(Resource resource, int taxPercentage) {
        taxInfoByResource.get(resource).setTaxPercentage(taxPercentage);
        isTaxRateChanged = true;
    }

    public void setExtremeTaxRate() {
        setTaxRate(80);
        currentTaxRate = TaxRate.EXTREME;
    }

    public void setStandardTaxRate() {
        setTaxRate(60);
        currentTaxRate = TaxRate.STANDARD;
    }

    public void setMediumTaxRate() {
        setTaxRate(40);
        currentTaxRate = TaxRate.MEDIUM;
    }

    public void setLowTaxRate() {
        setTaxRate(20);
        currentTaxRate = TaxRate.LOW;
    }

    private void setTaxRate(int rate) {
        taxInfoByResource.get(Resource.Food).setTaxPercentage(rate);
        taxInfoByResource.get(Resource.Alloy).setTaxPercentage(rate);
        taxInfoByResource.get(Resource.Gold).setTaxPercentage(rate);
        isTaxRateChanged = true;
    }

    public void setAnyTaxRate(Resource type, int intPercentage) {
        if (intPercentage < 20 || intPercentage > 80) {
            return;
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

    public void collectTax(WorkWallet fromWallet, EventTracker taxPayerTracker, WorkWallet toWallet, EventTracker taxManTracker, int day) {
        int foodTax = (int) taxInfoByResource.get(Resource.Food).calculateTax(fromWallet.getFood());
        int alloyTax = (int) taxInfoByResource.get(Resource.Alloy).calculateTax(fromWallet.getAlloy());
        int goldTax = (int) taxInfoByResource.get(Resource.Gold).calculateTax(fromWallet.getGold());

        TransferPackage transfer = new TransferPackage(foodTax, alloyTax, goldTax);
        toWallet.deposit(fromWallet, transfer);

        fromWallet.setTaxedOrNot(true);
        fromWallet.cashOutSalary(day);

        String taxPaid = EventTracker.Message("Minor", "Tax paid " + transfer);
        String taxCollected = EventTracker.Message("Minor", "Tax Collected " + transfer);
        taxPayerTracker.addEvent(taxPaid);
        taxManTracker.addEvent(taxCollected);
    }

    public String toStringTaxRates() {
        StringBuilder taxRatesBuilder = new StringBuilder();
        for (Resource resource : Resource.values()) {
            TaxInfo info = taxInfoByResource.get(resource);
            taxRatesBuilder.append(resource.name())
                    .append(": Tax ")
                    .append(Math.round(info.getTaxPercentage()))
                    .append("%\n");
        }
        return taxRatesBuilder.toString().trim();
    }

    public void increaseTaxRate() {
        switch (currentTaxRate) {
            case LOW:
                setMediumTaxRate();
                break;
            case MEDIUM:
                setStandardTaxRate();
                break;
            case STANDARD:
                setExtremeTaxRate();
                break;
            case EXTREME:
                break;
        }
    }

    public void decreaseTaxRate() {
        switch (currentTaxRate) {
            case LOW:
                break;
            case MEDIUM:
                setLowTaxRate();
                break;
            case STANDARD:
                setMediumTaxRate();
                break;
            case EXTREME:
                setStandardTaxRate();
                break;
        }
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
