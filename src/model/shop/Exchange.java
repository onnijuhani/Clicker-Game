package model.shop;

import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.resources.Resource;
import model.resourceManagement.wallets.Wallet;

public class Exchange {

    private ExchangeRates rates;

    private double defaultFoodAlloys = 100;
    private double defaultGold = 10;

    private double marketFee = 0.25;



    public Exchange(){
        this.rates = new ExchangeRates();
    }

    public String exchangeResources(double amountToBuy, Resource buyType, Resource sellType, Wallet wallet) {
        double rate = rates.getRate(sellType, buyType);
        double costWithoutFee = amountToBuy / rate;
        double totalCost = costWithoutFee * (1 + marketFee);

        if (!wallet.hasEnoughResource(sellType, totalCost)) {
            String errorMessage = EventTracker.Message( "Error","Insufficient resources for the exchange.");
            return errorMessage ;
        }

        TransferPackage costPackage = TransferPackage.fromEnum(sellType, totalCost);
        TransferPackage purchasePackage = TransferPackage.fromEnum(buyType, amountToBuy);

        wallet.subtractResources(costPackage);
        wallet.addResources(purchasePackage);

        String message = EventTracker.Message("Resource","Exchanged " + sellType + " for " + buyType);
        return message;
    }
    public double getDefaultFoodAlloys() {
        return defaultFoodAlloys;
    }

    public double getDefaultGold() {
        return defaultGold;
    }

    public double getMarketFee() {
        return marketFee;
    }

    public double calculateExchangeCost(double amountToBuy, Resource buyType, Resource sellType) {
        double rate = rates.getRate(sellType, buyType);
        double costWithoutFee = amountToBuy / rate;
        return costWithoutFee * (1 + marketFee);
    }
    public String getExchangeCostString(double amountToBuy, Resource buyType, Resource sellType) {
        double cost = calculateExchangeCost(amountToBuy, buyType, sellType);
        long roundedCost = Math.round(cost);
        long roundedAmountToBuy = Math.round(amountToBuy);
        return roundedAmountToBuy + " " + buyType + "\n" + roundedCost + ": " + sellType;
    }
    public void increaseDefaultPrices() {
        defaultFoodAlloys *= 2;
        defaultGold *= 2;
    }
    public void decreaseDefaultPrices() {
        defaultFoodAlloys /= 2;
        defaultGold /= 2;
    }





    class ExchangeRates {

        private double foodToGold = 0.1; //1 food buys 0.1 gold
        private double alloyToGold = 0.2;
        private double goldToFood = 10; //1 gold buys 10 food
        private double goldToAlloy = 5;

        public ExchangeRates() {
        }
        public double getRate(Resource sell, Resource buy) {
            if (sell.equals(Resource.Food) && buy.equals(Resource.Gold)) {
                return getFoodToGold();
            } else if (sell.equals(Resource.Alloy) && buy.equals(Resource.Gold)) {
                return getAlloyToGold();
            } else if (sell.equals(Resource.Gold) && buy.equals(Resource.Food)) {
                return getGoldToFood();
            } else if (sell.equals(Resource.Gold) && buy.equals(Resource.Alloy)) {
                return getGoldToAlloy();
            } else {
                throw new IllegalArgumentException("Unsupported resource exchange combination.");
            }
        }
        public double getFoodToGold() {
            return foodToGold;
        }
        public void setFoodToGold(double foodToGold) {
            this.foodToGold = foodToGold;
        }
        public double getAlloyToGold() {
            return alloyToGold;
        }
        public void setAlloyToGold(double alloyToGold) {
            this.alloyToGold = alloyToGold;
        }
        public double getGoldToFood() {
            return goldToFood;
        }
        public void setGoldToFood(double goldToFood) {
            this.goldToFood = goldToFood;
        }
        public double getGoldToAlloy() {
            return goldToAlloy;
        }
        public void setGoldToAlloy(double goldToAlloy) {
            this.goldToAlloy = goldToAlloy;
        }
    }
}


