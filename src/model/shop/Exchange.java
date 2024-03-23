package model.shop;

import model.characters.Character;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.EventTracker;

import java.util.Arrays;
import java.util.OptionalInt;

public class Exchange extends ShopComponents {

    private final ExchangeRates rates;
    private int defaultFood = 100;  //these defaults exist mostly for the exchange view. They reflect the desired ratio 10-5-1
    private int defaultAlloys = 50;
    private int defaultGold = 10;
    private double marketFee = 0.35;


    public Exchange(Wallet wallet){
        super(wallet);
        this.rates = new ExchangeRates();
    }

    public void sellResource(int amountToSell, Resource sellType, Character character) {
        updateExchangeRates();

        double amountAfterFee = amountToSell - amountToSell * marketFee;

        double rate = rates.getRate(Resource.Gold, sellType);
        int amountGold = (int) (amountAfterFee / rate);


        TransferPackage costPackage = TransferPackage.fromEnum(sellType, (int) amountAfterFee);
        TransferPackage purchasePackage = TransferPackage.fromEnum(Resource.Gold, amountGold);

        this.wallet.deposit(character.getPerson().getWallet(), costPackage);
        character.getPerson().getWallet().addResources(purchasePackage);


        if(character.getPerson().isPlayer()) {
            String message = EventTracker.Message("Shop", "Exchanged " + sellType + " for " + amountGold);
            character.getEventTracker().addEvent(message);
        }


    }

    public void exchangeResources(int amountToBuy, Resource buyType, Resource sellType, Character character) {

        updateExchangeRates(); // Update rates based on current wallet status

        double rate = rates.getRate(sellType, buyType);
        double costWithoutFee = amountToBuy / rate;
        int totalCost = (int) (costWithoutFee * (1 + marketFee));


        if (!character.getPerson().getWallet().hasEnoughResource(sellType, totalCost)) {
            if(character.getPerson().isPlayer()) {
                String errorMessage = EventTracker.Message("Error", "Insufficient resources for the exchange.");
                character.getEventTracker().addEvent(errorMessage);
            }
            return;
        }

        TransferPackage costPackage = TransferPackage.fromEnum(sellType, totalCost);
        TransferPackage purchasePackage = TransferPackage.fromEnum(buyType, amountToBuy);

        this.wallet.deposit(character.getPerson().getWallet(), costPackage);
        character.getPerson().getWallet().addResources(purchasePackage);

        if(character.getPerson().isPlayer()) {
            String message = EventTracker.Message("Shop", "Exchanged " + sellType + " for " + buyType);
            character.getEventTracker().addEvent(message);
        }

        // to prevent wallet growing too big it is cut in half
        OptionalInt max = Arrays.stream(getWallet().getWalletValues()).max();

        if (max.getAsInt() > 100_000_000) {
            wallet.cutBalanceInHalf();
        }


    }



    private void updateExchangeRates() {
        int foodAmount = wallet.getFood();
        int alloyAmount = wallet.getAlloy();
        int goldAmount = wallet.getGold();

        rates.updateRates(foodAmount, alloyAmount, goldAmount);
    }

    public double[] getRatioBalance() {
        int[] amounts = wallet.getWalletValues();
        double foodRatio = amounts[2] != 0 ? (double) amounts[0] / amounts[2] : 0;
        double alloyRatio = amounts[2] != 0 ? (double) amounts[1] / amounts[2] : 0;
        return new double[]{foodRatio, alloyRatio};
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
        // Doubles the default amounts while maintaining the ratio 100:50:10
        defaultFood = Math.max(4, defaultFood * 2);
        defaultAlloys = Math.max(4, defaultAlloys * 2);
        defaultGold = Math.max(4, defaultGold * 2);
    }

    public void decreaseDefaultPrices() {
        // Halves the default amounts with protection to not go below 1, maintaining the ratio 100:50:10
        if (defaultFood > 1 || defaultAlloys > 1 || defaultGold > 1) {
            // Calculate the new values with a check to ensure they don't fall below 4 and break the fee system
            int newDefaultFood = defaultFood / 2;
            int newDefaultAlloys = defaultAlloys / 2;
            int newDefaultGold = defaultGold / 2;

            // Apply the new values if they are above 4, otherwise set to 1
            defaultFood = newDefaultFood >= 4 ? newDefaultFood : 1;
            defaultAlloys = newDefaultAlloys >= 4 ? newDefaultAlloys : 1;
            defaultGold = newDefaultGold >= 4 ? newDefaultGold : 1;
        }
    }

    public ExchangeRates getRates() {
        return rates;
    }
    public int getDefaultFood() {
        return defaultFood;
    }

    public void setDefaultFood(int defaultFood) {
        this.defaultFood = defaultFood;
    }

    public int getDefaultAlloys() {
        return defaultAlloys;
    }

    public void setDefaultAlloys(int defaultAlloys) {
        this.defaultAlloys = defaultAlloys;
    }

    public void setDefaultGold(int defaultGold) {
        this.defaultGold = defaultGold;
    }

    public void setMarketFee(double marketFee) {
        this.marketFee = marketFee;
    }




    /**
     * Exchange Rates try to reach ratio of 10-5-1. Rates are adjusted so that this ideal reflects to the prices.
     */
    static class ExchangeRates {

        //default ratios
        private double foodToGold = 0.1; // 1 food buys 0.1 gold
        private double alloyToGold = 0.2; // 1 alloy buys 0.2 gold
        private double goldToFood = 10; // 1 gold buys 10 food
        private double goldToAlloy = 5; // 1 gold buys 5 alloys

        public ExchangeRates() {}


        public void updateRates(int foodAmount, int alloyAmount, int goldAmount) {


            // max and min to limit the market
            // Desired ratios
            double desiredFoodRatio = 10.0;
            double minFoodToGoldRatio = desiredFoodRatio * 0.45;
            double maxFoodToGoldRatio = desiredFoodRatio * 10;

            double desiredAlloy = 5.0;
            double minAlloyToGoldRatio = desiredAlloy * 0.45;
            double maxAlloyToGoldRatio = desiredAlloy * 10;

            // current ratios
            double currentFoodToGoldRatio = (double)foodAmount / goldAmount;
            double currentAlloyToGoldRatio = (double)alloyAmount / goldAmount;

            // food gold ratio
            if (currentFoodToGoldRatio < minFoodToGoldRatio) {
                this.foodToGold = 1.0 / minFoodToGoldRatio;
            } else if (currentFoodToGoldRatio > maxFoodToGoldRatio) {
                this.foodToGold = 1.0 / maxFoodToGoldRatio;
            } else {
                this.foodToGold = 1.0 / currentFoodToGoldRatio;
            }

            // alloys gold ratio
            if (currentAlloyToGoldRatio < minAlloyToGoldRatio) {
                this.alloyToGold = 1.0 / minAlloyToGoldRatio;
            } else if (currentAlloyToGoldRatio > maxAlloyToGoldRatio) {
                this.alloyToGold = 1.0 / maxAlloyToGoldRatio;
            } else {
                this.alloyToGold = 1.0 / currentAlloyToGoldRatio;
            }


            this.goldToFood = 1 / this.foodToGold;
            this.goldToAlloy = 1 / this.alloyToGold;
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
        @Override
        public String toString(){
            return ""+foodToGold+" "+alloyToGold+" "+goldToFood+" "+goldToAlloy;
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


