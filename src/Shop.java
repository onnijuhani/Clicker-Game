public class Shop {
    private Exchange exchange;



    private ClickerShop clickerShop;
    public Shop(){
        this.exchange = new Exchange();
        this.clickerShop = new ClickerShop();

    }
    public Exchange getExchange() {
        return exchange;
    }
    public ClickerShop getClickerShop() {
        return clickerShop;
    }
}

class PropertyShop{
    private int fortressPrice = 1000;
    private int citadelPrice = 800;
    private int castlePrice = 600;
    private int manorPrice = 400;
    private int mansionPrice = 200;
    private  int villaPrice = 100;
    private int cottagePrice = 50;
    private int shackPrice = 25;
    public PropertyShop(){
    }

    public void buyProperty(Properties type, Property property){
        switch (type) {
            case Properties.Fortress:

        }
    }


}

class Exchange {

    private ExchangeRates rates;

    private double defaultFoodAlloys = 100;
    private double defaultGold = 10;

    private double marketFee = 0.25;

    public Exchange(){
        this.rates = new ExchangeRates();
    }

    public void exchangeResources(double amountToBuy, Resource buyType, Resource sellType, Wallet wallet) {
        double rate = rates.getRate(sellType, buyType);
        double costWithoutFee = amountToBuy / rate;
        double totalCost = costWithoutFee * (1 + marketFee);

        if (!wallet.hasEnoughResource(sellType, totalCost)) {
            throw new IllegalArgumentException("Insufficient resources for the exchange.");
        }

        TransferPackage costPackage = TransferPackage.fromEnum(sellType, totalCost);
        TransferPackage purchasePackage = TransferPackage.fromEnum(buyType, amountToBuy);

        wallet.subtractResources(costPackage);
        wallet.addResources(purchasePackage);
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

class ClickerShop {
    private int alloyClickerPrice = 100;
    private int goldClickerPrice = 1000;

    public ClickerShop() {
    }
    public void buyClicker(Resource type, Clicker clicker, Wallet wallet) {
        int price = getPrice(type);
        if (wallet.hasEnoughResource(Resource.Gold, price)) {
            wallet.subtractGold(price);
            ClickerTools newTool = createClickerTool(type);
            clicker.addClickerTool(type, newTool);
        } else {
            throw new IllegalArgumentException("Insufficient gold to buy clicker.");
        }
    }

    public int getPrice(Resource type) {
        switch (type) {
            case Alloy:
                return alloyClickerPrice;
            case Gold:
                return goldClickerPrice;
            default:
                throw new IllegalArgumentException("Clicker type not available for purchase.");
        }
    }

    private ClickerTools createClickerTool(Resource type) {
        switch (type) {
            case Alloy:
                return new AlloyClicker();
            case Gold:
                return new GoldClicker();
            default:
                throw new IllegalArgumentException("Invalid clicker type.");
        }
    }

    public void setAlloyClickerPrice(int price) {
        this.alloyClickerPrice = price;
    }
    public void setGoldClickerPrice(int price) {
        this.goldClickerPrice = price;
    }
}
