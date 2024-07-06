package model.shop;

import model.characters.Character;
import model.characters.Person;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.MessageTracker;

import java.util.Arrays;
import java.util.OptionalInt;

import static model.Settings.formatShortNumber;

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

    public MarketState getMarketState() {
        return marketState;
    }

    private MarketState marketState = MarketState.GOOD_BALANCE;

    private final boolean DB = false; //TODO remove this debugger


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
     * This method is used to sell either food or alloys for Gold.
     * @param amountToSell the amount you want to sell, either food or alloys, the actual amount will be way smaller because the fee 35% is NOT added
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
        int amountGold = Math.max( (int)(amountAfterFee / rate), 1);

        TransferPackage costPackage = TransferPackage.fromEnum(sellType, (int) amountAfterFee);

        if(!(character.getPerson().getWallet().hasEnoughResources(costPackage))){
            return false; // not enough resources to sell
        }

        TransferPackage purchasePackage = TransferPackage.fromEnum(Resource.Gold, amountGold);

        this.wallet.deposit(character.getPerson().getWallet(), costPackage);
        character.getPerson().getWallet().addResources(purchasePackage);

        if(character.getPerson().isPlayer()) {
            String message = MessageTracker.Message("Shop", "Exchanged " + sellType + " for " + amountGold + " gold");
            character.getMessageTracker().addMessage(message);
        }
        return true;
    }


    /**
     * @param amountToBuy Amount you wish to buy
     * @param buyType Resource you want to buy
     * @param sellType Resourse you want to sell
     * @param character Character doing the trade
     * @return is trade was successful
     */
    public boolean exchangeResources(int amountToBuy, Resource buyType, Resource sellType, Character character) {

        updateExchangeRates(); // Update rates based on current wallet status

        double rate = rates.getRate(sellType, buyType);

        double costWithoutFee = Math.max( amountToBuy / rate, 1);

        int totalCost = Math.max( (int) (costWithoutFee * (1 + marketFee)), 1);

        if(!character.getPerson().isPlayer()){
            TransferPackage netCash = character.getPerson().getPaymentManager().getNetCash();
        }

        if (!character.getPerson().getWallet().hasEnoughResource(sellType, totalCost)) {
            if(character.getPerson().isPlayer()) {
                String errorMessage = MessageTracker.Message("Error", "Insufficient resources for the exchange.");
                character.getMessageTracker().addMessage(errorMessage);
            }
            return false;
        }

        TransferPackage costPackage = TransferPackage.fromEnum(sellType, totalCost);
        TransferPackage purchasePackage = TransferPackage.fromEnum(buyType, amountToBuy);


        // transaction happens here if deposit return true
        if(this.wallet.deposit(character.getPerson().getWallet(), costPackage)) {
            character.getPerson().getWallet().addResources(purchasePackage);


            String message = MessageTracker.Message("Shop", "Exchanged " +totalCost +"-"+ sellType + " for " + amountToBuy +"-"+ buyType);
            character.getMessageTracker().addMessage(message);


            // to prevent wallet growing too big it is cut in half
            OptionalInt max = Arrays.stream(getWallet().getWalletValues()).max();

            if (max.getAsInt() > 100_000_000) {
                wallet.cutBalanceInHalf();
            }

            checkMarketBalance();

            return true;
        }

        return false;
    }

    private void checkMarketBalance() {
        if (wallet.getBalanceRatio()[0] < 30){
            marketState = MarketState.LACK_OF_FOOD;
        } else if (wallet.getBalanceRatio()[1] < 20){
            marketState = MarketState.LACK_OF_ALLOYS;
        } else if (wallet.getBalanceRatio()[2] < 10){
            marketState = MarketState.LACK_OF_GOLD;
        }else{
            marketState = MarketState.GOOD_BALANCE;
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
        long roundedCost = Math.max(Math.round(cost), 1);
        long roundedAmountToBuy = Math.round(amountToBuy);
        return new String[]{formatShortNumber(roundedCost) + " " + sellType, formatShortNumber(roundedAmountToBuy) + " " + buyType};
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

    public void forceAcquire(TransferPackage cost, Person person, boolean isMandatory) {

        person.getMessageTracker().addMessage(MessageTracker.Message("Minor" , "Tried force Acquire"));

        if(person.isPlayer() && !isMandatory){
            return;
        }
        person.getMessageTracker().addMessage(MessageTracker.Message("Minor" , "Tried force Acquire 1"));
        if (person.getWallet().hasEnoughResources(cost)) {
            return; // Sufficient resources available, no action needed
        }
        person.getMessageTracker().addMessage(MessageTracker.Message("Minor" , "Tried force Acquire 2"));

        int fCost = cost.food();
        int aCost = cost.alloy();
        int gCost = cost.gold();

        int fOwned = person.getWallet().getFood();
        int aOwned = person.getWallet().getAlloy();
        int gOwned = person.getWallet().getGold();

        int fNeeded = fCost - fOwned;
        int aNeeded = aCost - aOwned;
        int gNeeded = gCost - gOwned;

        person.getMessageTracker().addMessage(MessageTracker.Message("Minor" , "Tried force Acquire\n" + fNeeded +" "+ aNeeded +" "+ gNeeded));

        switch (getAction(fNeeded, aNeeded, gNeeded)) {
            case BUY_FOOD_AND_ALLOY:
                forceBuy(fNeeded, Resource.Food, person);
                forceBuy(aNeeded, Resource.Alloy, person);
                break;
            case BUY_FOOD_AND_SELL_ALLOY:
                forceBuy(fNeeded, Resource.Food, person);
                sellResource(aNeeded * -1, Resource.Alloy, person.getCharacter());
                break;
            case BUY_ALLOY_AND_SELL_FOOD:
                forceBuy(aNeeded, Resource.Alloy, person);
                sellResource(fNeeded * -1, Resource.Food, person.getCharacter());
                break;
            case BUY_FOOD:
                forceBuy(fNeeded, Resource.Food, person);
                break;
            case BUY_ALLOY:
                forceBuy(aNeeded, Resource.Alloy, person);
                break;
            case SELL_FOOD_AND_ALLOY:
                sellResource(fNeeded * -1, Resource.Food, person.getCharacter());
                sellResource(aNeeded * -1, Resource.Alloy, person.getCharacter());
                break;
        }
    }

    private ActionType getAction(int fNeeded, int aNeeded, int gNeeded) {
        if (fNeeded > 0 && aNeeded > 0 && gNeeded > 0) {
            return ActionType.BUY_FOOD_AND_ALLOY;
        } else if (fNeeded > 0 && aNeeded > 0) {
            return ActionType.BUY_FOOD_AND_ALLOY;
        } else if (fNeeded > 0 && gNeeded > 0) {
            return ActionType.BUY_FOOD_AND_SELL_ALLOY;
        } else if (aNeeded > 0 && gNeeded > 0) {
            return ActionType.BUY_ALLOY_AND_SELL_FOOD;
        } else if (fNeeded > 0) {
            return ActionType.BUY_FOOD;
        } else if (aNeeded > 0) {
            return ActionType.BUY_ALLOY;
        } else {
            return ActionType.SELL_FOOD_AND_ALLOY;
        }
    }

    private enum ActionType {
        BUY_FOOD_AND_ALLOY,
        BUY_FOOD_AND_SELL_ALLOY,
        BUY_ALLOY_AND_SELL_FOOD,
        BUY_FOOD,
        BUY_ALLOY,
        SELL_FOOD_AND_ALLOY
    }

    private enum MarketState {
        LACK_OF_GOLD,
        LACK_OF_ALLOYS,
        LACK_OF_FOOD,
        GOOD_BALANCE
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
            if (sell.equals(model.resourceManagement.Resource.Food) && buy.equals(model.resourceManagement.Resource.Gold)) {
                return getFoodToGold();
            } else if (sell.equals(model.resourceManagement.Resource.Alloy) && buy.equals(model.resourceManagement.Resource.Gold)) {
                return getAlloyToGold();
            } else if (sell.equals(model.resourceManagement.Resource.Gold) && buy.equals(Resource.Food)) {
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


