package model.shop;

import model.characters.Character;
import model.characters.Person;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.EventTracker;

import java.util.Arrays;
import java.util.OptionalInt;

/**
 * This class is a complete mess now
 * TODO simplify and refactor
 */
public class Exchange extends ShopComponents {

    private final ExchangeRates rates;
    private int defaultFood = 100;  //these defaults exist mostly for the exchange view. They reflect the desired ratio 10-5-1
    private int defaultAlloys = 50;
    private int defaultGold = 10;
    private final double marketFee = 0.35;

    private final boolean DB = true; //TODO remove this debugger


    public Exchange(Wallet wallet){
        super(wallet);
        this.rates = new ExchangeRates();
    }

    public boolean forceBuy(int amount, Resource buyType, Person person) {
        boolean bought;

        if (buyType == Resource.Gold){
            int sellAmountFood = amount * 10;
            bought = sellResource(sellAmountFood, Resource.Food, person.getCharacter() );
            if (!bought){
                int sellAmountAlloy = amount * 5;
                bought = sellResource(sellAmountAlloy, Resource.Alloy, person.getCharacter());
                if (bought){
                    return true;
                }
            }
        }

        if (buyType == Resource.Food || buyType == Resource.Alloy){

            Resource forceType;
            if(buyType == Resource.Food){
                forceType = Resource.Alloy;
            }else{
                forceType = Resource.Food;
            }

            bought = exchangeResources(amount, buyType, Resource.Gold, person.getCharacter());
            if(!bought){
                int forceAmount = calculateAmountToSellForGold(amount, buyType, forceType);
                boolean forceBought = exchangeResources(forceAmount, forceType, Resource.Gold, person.getCharacter());
                if(forceBought){
                    bought = exchangeResources(amount, buyType, Resource.Gold, person.getCharacter());
                    return bought;
                }
            }

        }
        return false;

    }




    private int calculateAmountToSellForGold(int amountToBuy, Resource buyType, Resource sellType) {

        if(buyType == Resource.Gold || sellType == Resource.Gold){
            System.out.println("Exchange calculateAmountToSellForGold went wrong");
            throw new IllegalArgumentException("Unsupported resource exchange combination.");
        }

        double buyRate = rates.getRate(Resource.Gold, buyType);
        double sellRate = rates.getRate(sellType, Resource.Gold);
        double goldNeeded = amountToBuy * buyRate;
        return (int) Math.ceil(goldNeeded / sellRate);
    }


    /**
     * This method is used to see either food or alloys for Gold.
     * @param amountToSell the amount you want to sell, either food or alloys
     * @param sellType either food or alloys
     * @param character character wishing to sell the resource
     */
    public Boolean sellResource(int amountToSell, Resource sellType, Character character) {

        if(sellType == Resource.Gold){ // make sure gold is not being sold.
            System.out.println("Exchange sellResource method went wrong");
            return false;
        }

        updateExchangeRates();

        double amountAfterFee = amountToSell - amountToSell * marketFee;

        double rate = rates.getRate(Resource.Gold, sellType);
        int amountGold = (int) (amountAfterFee / rate);

        TransferPackage costPackage = TransferPackage.fromEnum(sellType, (int) amountAfterFee);

        if(!(character.getPerson().getWallet().hasEnoughResources(costPackage))){
            return false; // not enough resources to sell
        }

        TransferPackage purchasePackage = TransferPackage.fromEnum(Resource.Gold, amountGold);

        this.wallet.deposit(character.getPerson().getWallet(), costPackage);
        character.getPerson().getWallet().addResources(purchasePackage);

        if(character.getPerson().isPlayer()) {
            String message = EventTracker.Message("Shop", "Exchanged " + sellType + " for " + amountGold);
            character.getEventTracker().addEvent(message);
        }
        return true;
    }




    public boolean exchangeResources(int amountToBuy, Resource buyType, Resource sellType, Character character) {
        if(DB) {System.out.println("exchange 0");}
        updateExchangeRates(); // Update rates based on current wallet status
        if(DB) {System.out.println("exchange 1" + buyType + sellType);}
        double rate = rates.getRate(sellType, buyType);
        if(DB) {System.out.println("exchange 2");}
        double costWithoutFee = amountToBuy / rate;
        if(DB) {System.out.println("exchange 3");}
        int totalCost = (int) (costWithoutFee * (1 + marketFee));
        if(DB) {System.out.println("exchange 4");}

        if (!character.getPerson().getWallet().hasEnoughResource(sellType, totalCost)) {
            if(character.getPerson().isPlayer()) {
                String errorMessage = EventTracker.Message("Error", "Insufficient resources for the exchange.");
                character.getEventTracker().addEvent(errorMessage);
                if(DB) {System.out.println("exchange 5");}
            }
            if(DB) {System.out.println("exchange 6");}
            return false;

        }
        if(DB) {System.out.println("exchange 7");}
        TransferPackage costPackage = TransferPackage.fromEnum(sellType, totalCost);
        TransferPackage purchasePackage = TransferPackage.fromEnum(buyType, amountToBuy);
        if(DB) {System.out.println("exchange 8");}
        this.wallet.deposit(character.getPerson().getWallet(), costPackage);
        character.getPerson().getWallet().addResources(purchasePackage);
        if(DB) {System.out.println("exchange 9");}
        if(character.getPerson().isPlayer()) {
            String message = EventTracker.Message("Shop", "Exchanged " + sellType + " for " + buyType);
            character.getEventTracker().addEvent(message);
        }

        // to prevent wallet growing too big it is cut in half
        OptionalInt max = Arrays.stream(getWallet().getWalletValues()).max();

        if (max.getAsInt() > 100_000_000) {
            wallet.cutBalanceInHalf();
        }
        return true;


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
                System.out.println("Virhe Exchange getRate() metodissa");
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


