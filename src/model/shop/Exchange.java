package model.shop;

import model.characters.Character;
import model.characters.player.EventTracker;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;

public class Exchange extends ShopComponents {
    private ExchangeRates rates;
    private int defaultFoodAlloys = 100;
    private int defaultGold = 10;
    private double marketFee = 0.35;

    public Exchange(Wallet wallet){
        super(wallet);
        this.rates = new ExchangeRates();
    }

    public void exchangeResources(int amountToBuy, Resource buyType, Resource sellType, Character character) {
        double rate = rates.getRate(sellType, buyType);
        double costWithoutFee = amountToBuy / rate;
        int totalCost = (int) (costWithoutFee * (1 + marketFee));

        if (!wallet.hasEnoughResource(buyType, amountToBuy)) {
            String errorMessage = EventTracker.Message("Error", "Market has insufficient resources for the exchange.");
            character.getEventTracker().addEvent(errorMessage);
            return;
        }

        if (!character.getWallet().hasEnoughResource(sellType, totalCost)) {
            String errorMessage = EventTracker.Message( "Error","Insufficient resources for the exchange.");
            character.getEventTracker().addEvent(errorMessage);
            return;
        }

        TransferPackage costPackage = TransferPackage.fromEnum(sellType, totalCost);
        TransferPackage purchasePackage = TransferPackage.fromEnum(buyType, amountToBuy);

        this.wallet.deposit(character.getWallet(), costPackage);
        character.getWallet().deposit(this.wallet, purchasePackage);

        String message = EventTracker.Message("Shop","Exchanged " + sellType + " for " + buyType);
        character.getEventTracker().addEvent(message);
    }
    public int getDefaultFoodAlloys() {
        return defaultFoodAlloys;
    }

    public int getDefaultGold() {
        return defaultGold;
    }

    public double getMarketFee() {
        return marketFee;
    }

    public int calculateExchangeCost(int amountToBuy, Resource buyType, Resource sellType) {
        double rate = rates.getRate(sellType, buyType);
        double costWithoutFee = amountToBuy / rate;
        return (int) (costWithoutFee * (1 + marketFee));
    }
    public String[] getExchangeCost(int amountToBuy, Resource buyType, Resource sellType) {
        double cost = calculateExchangeCost(amountToBuy, buyType, sellType);
        long roundedCost = Math.round(cost);
        long roundedAmountToBuy = Math.round(amountToBuy);
        return new String[]{roundedCost + " " + sellType, roundedAmountToBuy + " " + buyType};
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
        private double alloyToGold = 0.2; // 1 alloy buys 0.2 gold
        private double goldToFood = 10; //1 gold buys 10 food
        private double goldToAlloy = 5; //1 gold buys 5 alloys

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


