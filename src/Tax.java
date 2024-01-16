import java.util.EnumMap;

public class Tax {
    private final EnumMap<Resource, TaxInfo> taxInfoByResource;

    public Tax() {
        taxInfoByResource = new EnumMap<>(Resource.class);
        for (Resource resource : Resource.values()) {
            taxInfoByResource.put(resource, new TaxInfo(0.1, 10)); // Default tax info
        }
    }

    public void setTaxInfo(Resource resource, double taxPercentage, double minimumTaxableAmount) {
        taxInfoByResource.get(resource).setTaxPercentage(taxPercentage);
        taxInfoByResource.get(resource).setMinimumTaxableAmount(minimumTaxableAmount);
    }

    public TaxInfo getTaxInfo(Resource resource) {
        return taxInfoByResource.get(resource);
    }

    // Define how tax is calculated and collected based on the rules set for each resource
    public TransferPackage collectTax(Wallet fromWallet, Wallet toWallet) {
        double foodTax = taxInfoByResource.get(Resource.Food).calculateTax(fromWallet.getFood().getAmount());
        double alloyTax = taxInfoByResource.get(Resource.Alloy).calculateTax(fromWallet.getAlloy().getAmount());
        double goldTax = taxInfoByResource.get(Resource.Gold).calculateTax(fromWallet.getGold().getAmount());

        TransferPackage transfer = new TransferPackage(foodTax,alloyTax,goldTax);
        toWallet.deposit(fromWallet, transfer);

        return transfer;
    }

    private double getResourceAmount(Wallet wallet, Resource resource) {
        switch (resource) {
            case Food:
                return wallet.getFood().getAmount();
            case Alloy:
                return wallet.getAlloy().getAmount();
            case Gold:
                return wallet.getGold().getAmount();
            default:
                return 0;
        }
    }

    public static class TaxInfo {
        private double taxPercentage;
        private double minimumTaxableAmount;

        public TaxInfo(double taxPercentage, double minimumTaxableAmount) {
            this.taxPercentage = taxPercentage;
            this.minimumTaxableAmount = minimumTaxableAmount;
        }

        public void setTaxPercentage(double taxPercentage) {
            this.taxPercentage = taxPercentage;
        }

        public void setMinimumTaxableAmount(double minimumTaxableAmount) {
            this.minimumTaxableAmount = minimumTaxableAmount;
        }

        public double calculateTax(double amount) {
            if (amount >= minimumTaxableAmount) {
                return amount * (taxPercentage / 100.0);
            }
            return 0;
        }
    }
}

