package model.characters.ai.actions;

import customExceptions.InsufficientResourcesException;
import model.buildings.Construct;
import model.buildings.Property;
import model.buildings.properties.Fortress;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.Person;
import model.characters.Role;
import model.characters.Status;
import model.characters.Trait;
import model.characters.ai.Aspiration;
import model.characters.ai.actionCircle.WeightedObject;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.shop.Exchange;
import model.shop.UtilityShop;
import model.stateSystem.EventTracker;
import model.time.Time;
import model.war.Military;

import java.util.List;
import java.util.Map;

public class ManagementActions extends BaseActions {
    private int gold_need_threshold = 20;
    private int alloy_need_threshold = 100;
    private int food_need_threshold = 200;

    private final Wallet wallet = person.getWallet();
    private final Exchange exchange = person.getRole().getNation().getShop().getExchange();

    public ManagementActions(Person person, NPCActionLogger npcActionLogger, Map<Trait, Integer> profile) {
        super(person, npcActionLogger, profile);
    }


    @Override
    protected void createAllActions() {
        EvaluateNeeds evaluateNeeds = new EvaluateNeeds(person, npcActionLogger,1,profile);
        TakeActionOnNeeds takeActionOnNeeds = new TakeActionOnNeeds(person, npcActionLogger,5,profile);
        BalanceWallet balanceWallet = new BalanceWallet(person, npcActionLogger,5,profile);
        TradeMarket tradeMarket = new TradeMarket(person, npcActionLogger,5,profile);
        EvaluateRoleSpecificNeeds evaluateRoleSpecificNeeds = new EvaluateRoleSpecificNeeds(person, npcActionLogger,5,profile);


        allActions.add(evaluateNeeds);
        allActions.add(takeActionOnNeeds);
        allActions.add(balanceWallet);
        allActions.add(tradeMarket);
        allActions.add(evaluateRoleSpecificNeeds);
    }


    /**
     * Main class that attempts to balance the market.
     */
    class TradeMarket extends WeightedObject {
        public TradeMarket(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }
        @Override
        public void execute(){
            defaultAction();
        }
        @Override
        public void defaultAction() {
            if(wallet.isLowBalance()){
                return;
            }

            if(!person.hasAspiration(Aspiration.TRADE_MARKET)) return;


            int gold = wallet.getGold();
            double foodRatioInMarket = exchange.getWallet().getBalanceRatio()[0];
            double alloyRatioInMarket = exchange.getWallet().getBalanceRatio()[1];
            double goldRatioInMarket = exchange.getWallet().getBalanceRatio()[2];


            if (!(gold > gold_need_threshold * 4)){
                return;
            }

            if (!(goldRatioInMarket < 15)){
                return;
            }

            double fRatio = foodRatioInMarket / (foodRatioInMarket + alloyRatioInMarket);
            double aRatio = alloyRatioInMarket / (foodRatioInMarket + alloyRatioInMarket);

            int amountGoldToSpend = gold - (gold / 4);

            if (fRatio > 0.35 && amountGoldToSpend > 0){
                if(exchange.exchangeResources(amountGoldToSpend*10, Resource.Food, Resource.Gold, person.getCharacter())) {
                    logAction(String.format("Bought %d food to balance the market", amountGoldToSpend*10));
                }
            }

            if (aRatio > 0.35 && amountGoldToSpend > 0){
                if(exchange.exchangeResources(amountGoldToSpend*5, Resource.Alloy, Resource.Gold, person.getCharacter())) {
                    logAction(String.format("Bought %d alloys to balance the market", amountGoldToSpend*5));
                }
            }
        }
    }

    /**
     * This is the main class to balance out the wallet, only called max once per month.
     */
    class BalanceWallet extends WeightedObject{
        private int foodBoughtCounter = 0;
        private int alloyBoughtCounter = 0;
        private int goldBoughtCounter = 0;
        private int lastCheck;

        public BalanceWallet(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        @Override
        public void execute(){
            defaultAction();
        }
        @Override
        public void defaultAction() {
            if(Time.year == 0 && Time.month == 0){
                return;
            }
            // do this only once per month
            int currentMonth = Time.month;
            if(currentMonth == lastCheck){
                return;
            }else{
                lastCheck = currentMonth;
            }
            double[] currentRatio = person.getWallet().getBalanceRatio();
            double total = currentRatio[0] + currentRatio[1] + currentRatio[2];

            int[] desiredRatio;
            if (person.getProperty() instanceof Military) {
                desiredRatio = new int[]{(int) (0.40 * total), (int) (0.40 * total), (int) (0.20 * total)};
            } else {
                desiredRatio = new int[]{(int) (0.33 * total), (int) (0.33 * total), (int) (0.33 * total)};
            }

            int food = wallet.getFood();
            int alloy = wallet.getAlloy();

            if(food > ( desiredRatio[0] + food/3) ){
                int amountBefore = wallet.getGold();
                int amountToSell = (food - desiredRatio[0]) / 2;
                exchange.sellResource(amountToSell, Resource.Food, person.getCharacter());
                
                foodBoughtCounter--;
                goldBoughtCounter++;
                if(goldBoughtCounter > 4){
                    person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                }

                int amountBought = amountBefore - wallet.getGold();
                logAction(String.format("Sold %d food for %d gold", amountToSell, amountBought));
                
            }else{
                exchange.forceBuy((desiredRatio[0]-food) / 2, Resource.Food, person);
                
                foodBoughtCounter++;
                goldBoughtCounter--;
                if(foodBoughtCounter > 4){
                    person.addAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                }

                logAction(String.format("Force bought %d food", (desiredRatio[0]-food) / 2));

            }
            CheckBalanceNow result = new CheckBalanceNow(desiredRatio, alloy);

            if(result.alloy() > ( result.desiredRatio()[1] + result.alloy() /3) ){
                int amountBefore = wallet.getGold();
                int amountToSell = (result.alloy() - result.desiredRatio()[1]) / 2;
                exchange.sellResource(amountToSell,Resource.Alloy, person.getCharacter());

                alloyBoughtCounter--;
                goldBoughtCounter++;
                if(goldBoughtCounter > 4){
                    person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                }

                int amountBought = amountBefore - wallet.getGold();
                logAction(String.format("Sold %d alloys for %d gold", amountToSell, amountBought));
                
            }else {
                exchange.forceBuy((result.desiredRatio()[1] - result.alloy()) / 2, Resource.Alloy, person);

                alloyBoughtCounter++;
                goldBoughtCounter--;
                if (alloyBoughtCounter > 4) {
                    person.addAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                }
                logAction(String.format("Force bought %d alloys", (result.desiredRatio()[1] - result.alloy()) / 2));
            }

        }
        private record CheckBalanceNow(int[] desiredRatio, int alloy) {
        }
    }


    /**
     *  Only class to evaluate Role specific needs, should execute or add aspirations
     */

    class EvaluateRoleSpecificNeeds extends WeightedObject {


        public EvaluateRoleSpecificNeeds(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }

        @Override
        public void execute(){
            defaultAction();
        }

        /**
         * default is the only method here
         */
        @Override
        public void defaultAction() {

            Role role = person.getRole();
            Status status = role.getStatus();

            switch (status) {
                case Farmer:

                case Miner:

                case Merchant:
                    person.addAspiration(Aspiration.DEPOSIT_TO_VAULT);

                case Captain:

                    if(!evaluateExtremeTaxPolicy()){
                        if(evaluateLowTaxPolicy()){
                            person.addAspiration(Aspiration.SET_STANDARD_TAX);
                        }
                    }

                case Mayor:

                    if(!evaluateExtremeTaxPolicy()){
                        if(evaluateLowTaxPolicy()){
                            person.addAspiration(Aspiration.SET_STANDARD_TAX);
                        }
                    }

                case Governor:

                    if(!evaluateExtremeTaxPolicy()){
                        if(evaluateLowTaxPolicy()){
                            person.addAspiration(Aspiration.SET_STANDARD_TAX);
                        }
                    }

                case Mercenary:


                case King:

                    if(!evaluateExtremeTaxPolicy()){
                        if(evaluateLowTaxPolicy()){
                            person.addAspiration(Aspiration.SET_STANDARD_TAX);
                        }
                    }
                    person.removeAspiration(Aspiration.ACHIEVE_HIGHER_POSITION);


                case Noble:



                case Vanguard:



                case Peasant:

                }
            }

        private boolean evaluateExtremeTaxPolicy() {
            Integer aggressive = person.getAiEngine().getProfile().get(Trait.Aggressive);
            Integer disloyal = person.getAiEngine().getProfile().get(Trait.Disloyal);


            if (aggressive != null && disloyal != null) {
                if (aggressive > 25 && disloyal > 25) {
                    person.addAspiration(Aspiration.SET_EXTREME_TAXES);
                    return true;
                }
                if (aggressive > 75 || disloyal > 75) {
                    person.addAspiration(Aspiration.SET_EXTREME_TAXES);
                    return true;
                }
            }

            Integer slaver = person.getAiEngine().getProfile().get(Trait.Slaver);
            if( slaver != null && slaver > 20) {
                person.addAspiration(Aspiration.SET_EXTREME_TAXES);
                return true;
            }

            return false;

        }

        private boolean evaluateLowTaxPolicy() {
            Integer loyal = person.getAiEngine().getProfile().get(Trait.Loyal);
            Integer unambitious = person.getAiEngine().getProfile().get(Trait.Unambitious);
            Integer liberal = person.getAiEngine().getProfile().get(Trait.Liberal);

            if (loyal != null && unambitious != null && liberal != null) {
                if (loyal > 10 && unambitious > 10 && liberal > 10) {
                    person.addAspiration(Aspiration.SET_LOW_TAXES);
                    return false;
                }
                if (loyal > 50 || unambitious > 70 || liberal > 40) {
                    person.addAspiration(Aspiration.SET_MEDIUM_TAXES);
                    return false;
                }
            }
            return true;
        }


    }

    /**
     * This class takes action on different management Aspirations, should not do any combat management here AT ALL. Only add combat aspirations if needed
     */
    class TakeActionOnNeeds extends WeightedObject{
        int counter = 0;

        public TakeActionOnNeeds(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }


        @Override
        public void execute(){
            defaultAction();
            balanceSkewedResources();
        }

        /**
         * If there is significant overweight in certain resource, try to balance it out.
         * This should be secondary balance mechanism to Balance Resources class
         */
        private void balanceSkewedResources() {

            double fRatio = wallet.getBalanceRatio()[0];
            double aRatio = wallet.getBalanceRatio()[1];
            double gRatio = wallet.getBalanceRatio()[2];

            if (fRatio > 0.70){
                exchange.sellResource(wallet.getFood() / 2, Resource.Food, person.getCharacter());
            }else if (aRatio > 0.70){
                exchange.sellResource(wallet.getAlloy() / 2, Resource.Alloy, person.getCharacter());
            }else if (gRatio > 0.75){
                exchange.exchangeResources(wallet.getGold() / 4 * 10, Resource.Food, Resource.Gold, person.getCharacter());
                exchange.exchangeResources(wallet.getGold() / 4 * 5, Resource.Alloy, Resource.Gold, person.getCharacter());
            }

        }

        @Override
        public void defaultAction(){

            if(Time.getYear() == 0 && Time.getMonth() < 4){
                return; // quick return in early game to allow some generate ramp up
            }


            if  (wallet.isLowBalance()){
                return; // if wallet is empty or very low, return immediately. Nothing can be done here.
            }

            if(person.hasAspiration(Aspiration.GET_FOOD_INSTANTLY) && person.hasAspiration(Aspiration.GET_ALLOYS_INSTANTLY) && person.hasAspiration(Aspiration.GET_GOLD_INSTANTLY)){
                return; // quick return if there is need for everything, don't waste time and let production ramp up
            }

            Property property = person.getProperty();

            for (Aspiration aspiration : person.getAspirations()) {


                if(person.getAspirations().contains(Aspiration.SAVE_RESOURCES) && person.getAspirations().contains(Aspiration.UPGRADE_PROPERTY)){
                    if(!aspiration.equals(Aspiration.UPGRADE_PROPERTY)){
                        continue; // make sure upgrade is prioritized so resources can be saved
                    }
                }

                switch (aspiration) {


                    case UPGRADE_PROPERTY:

                        try {
                            Construct.constructProperty(person);
                            person.removeAspiration(Aspiration.UPGRADE_PROPERTY);
                            person.removeAspiration(Aspiration.SAVE_RESOURCES);
                        } catch (InsufficientResourcesException e) {
                            person.getProperty().getVault().withdrawal(wallet, e.getCost());

                        }


                        break;

                    case GET_GOLD_INSTANTLY:

                        exchange.forceBuy(gold_need_threshold * 2, Resource.Gold, person);
                        person.removeAspiration(Aspiration.GET_GOLD_INSTANTLY);
                        break;

                    case GET_FOOD_INSTANTLY:

                        exchange.forceBuy(food_need_threshold * 2, Resource.Food, person);
                        person.removeAspiration(Aspiration.GET_FOOD_INSTANTLY);
                        break;

                    case GET_ALLOYS_INSTANTLY:

                        exchange.forceBuy(alloy_need_threshold * 2, Resource.Alloy, person);
                        person.removeAspiration(Aspiration.GET_ALLOYS_INSTANTLY);
                        break;

                    case INVEST_IN_GOLD_PRODUCTION:
                        if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;
                        UtilityShop.upgradeBuilding(UtilityBuildings.GoldMine, person);
                        break;

                    case INVEST_IN_ALLOY_PRODUCTION:
                        if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;
                        UtilityShop.upgradeBuilding(UtilityBuildings.AlloyMine, person);
                        break;

                    case INVEST_IN_FOOD_PRODUCTION:
                        if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;
                        UtilityShop.upgradeBuilding(UtilityBuildings.MeadowLands, person);
                        break;

                    default:// if there are no aspirations, make a deposit to vault and upgrade defence if they are passive or unambitious. Others do nothing

                        if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) break;
                        if (counter < 500) {counter++;break;}
                        if(profile.containsKey(Trait.Passive) || profile.containsKey(Trait.Unambitious)) {
                            TransferPackage netCash = person.getPaymentManager().getNetCash();
                            TransferPackage vaultDeposit = new TransferPackage(netCash.food(), netCash.alloy(), netCash.gold());
                            property.getVault().deposit(wallet, vaultDeposit);
                            person.getEventTracker().addEvent(EventTracker.Message("Major","Adding resources to vault " + vaultDeposit));
                            person.addAspiration(Aspiration.INCREASE_PROPERTY_DEFENCE);
                        }
                        break;
                }
            }
        }
    }

    /**
     *  THIS CLASS SHOULD ONLY EVALUATE CURRENT MANAGEMENT NEEDS BUT NOT DO ANYTHING ABOUT THEM. JUST ADD IT INTO ASPIRATIONS
     */
    class EvaluateNeeds extends WeightedObject {
        int counter = 0;

        public EvaluateNeeds(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }


        @Override
        public void execute(){
            defaultAction();
        }

        /**
         * default is the only method here
         */
        @Override
        public void defaultAction() {

            if(Time.getYear() == 0 && Time.getMonth() < 3){
                return; // quick return in early game to allow some generate ramp up
            }

            if(!(person.getProperty() instanceof Fortress) && counter < 1) {  // fortress cannot be upgraded
                evaluatePropertyNeed();
            }
            evaluateMinimumResourceNeeds();

            counter++; // property shouldn't be checked every time, every 10th time is just fine to prevent AI advancing too greedily
            if(counter > 10){
                counter = 0;
            }

            evaluateOptimalResourceNeeds();

        }



        /**
         * property need isn't important enough to get own Actions
         */
        private void evaluatePropertyNeed() {
            Property property = person.getProperty();
            int usedSlotAmount = property.getUtilitySlot().usedSlotAmount();

            //evaluate need for new property
            if(property.getUtilitySlot().getSlotAmount() == 5){
                // make sure they have net cash high enough to sustain the default army before attempting to update into military buildings
                TransferPackage netCash = person.getPaymentManager().getNetBalance();
                if(netCash.food() > 1100 && netCash.alloy() > 300 && netCash.gold() > 100){
                    person.addAspiration(Aspiration.UPGRADE_PROPERTY);
                    person.addAspiration(Aspiration.SAVE_RESOURCES);
                    logAction(String.format("Evaluated a need to upgrade current home %s and a need to save resources for that", person.getProperty()));
                }

            }else if(usedSlotAmount == property.getUtilitySlot().getSlotAmount()) {
                if (property.getUtilitySlot().getTotalUpgradeLevels() > 3 * usedSlotAmount){ // idea here is to upgrade some of the utility buildings first before the property
                    person.addAspiration(Aspiration.UPGRADE_PROPERTY); // remember to remove this need after new property is created.
                    person.addAspiration(Aspiration.SAVE_RESOURCES); // remove this one too
                    logAction(String.format("Evaluated a need to upgrade current home %s and a need to save resources for that", person.getProperty()));
                }
            }
        }

        private void evaluateMinimumResourceNeeds() {
            try {
                int food = wallet.getFood();
                TransferPackage expenses = person.getPaymentManager().getFullExpense();

                food_need_threshold = expenses.food();
                alloy_need_threshold = expenses.alloy();
                gold_need_threshold = expenses.gold();

                if( food < food_need_threshold ) {
                    logAction("MinimumResourceNeeds", String.format("Evaluated a lack of food as current balance is below threshold of %d", food_need_threshold));
                    person.addAspiration(Aspiration.GET_FOOD_INSTANTLY);
                    if (food < food_need_threshold / 2){
                        person.addAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                    } else{
                        person.removeAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                    }
                } else {
                    person.removeAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                    person.removeAspiration(Aspiration.GET_FOOD_INSTANTLY);
                    logAction("MinimumResourceNeeds", String.format("Evaluated excess of food as current balance is above threshold of %d", food_need_threshold));
                }

                int alloy = wallet.getAlloy();

                if( alloy < alloy_need_threshold ) {
                    logAction("MinimumResourceNeeds", String.format("Evaluated a lack of alloys as current balance is below threshold of %d", alloy_need_threshold));
                    person.addAspiration(Aspiration.GET_ALLOYS_INSTANTLY);
                    if (alloy < alloy_need_threshold / 2){
                        person.addAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                    } else{
                        person.removeAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                    }
                } else {
                    person.removeAspiration(Aspiration.GET_ALLOYS_INSTANTLY);
                    person.removeAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                    logAction("MinimumResourceNeeds", String.format("Evaluated excess of alloys as current balance is above threshold of %d", alloy_need_threshold));
                }

                int gold = wallet.getGold();

                if( gold < gold_need_threshold ) {
                    logAction("MinimumResourceNeeds", String.format("Evaluated a lack of gold as current balance is below threshold of %d", gold_need_threshold));
                    person.addAspiration(Aspiration.GET_GOLD_INSTANTLY);
                    if (gold < food_need_threshold / 2){
                        person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                    } else{
                        person.removeAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                    }
                } else {
                    person.removeAspiration(Aspiration.GET_GOLD_INSTANTLY);
                    person.removeAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                    logAction("MinimumResourceNeeds", String.format("Evaluated excess of gold as current balance is above threshold of %d", gold_need_threshold));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void evaluateOptimalResourceNeeds() {

            try {
                TransferPackage netBalance = person.getPaymentManager().getNetBalance();

                int food_need_optimal = netBalance.food();
                int alloy_need_optimal = netBalance.alloy();
                int gold_need_optimal = netBalance.gold();

                if( food_need_optimal < 1000 ) {
                    logAction("OptimalResourceNeeds", String.format("Evaluated need to invest in the production of food as current balance is negative %d", food_need_optimal));
                    person.addAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                } else {
                    person.removeAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                    logAction("OptimalResourceNeeds", String.format("No need to invest in food production as current balance is positive %d", food_need_optimal));
                }


                if( alloy_need_optimal < 100 ) {
                    logAction("OptimalResourceNeeds", String.format("Evaluated need to invest in the production of alloys as current balance is negative %d", alloy_need_optimal));
                    person.addAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                }else{
                    person.removeAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                    logAction("OptimalResourceNeeds", String.format("No need to invest in alloys production as current balance is positive %d", alloy_need_optimal));
                }

                if( gold_need_optimal < 20 ) {
                    logAction("OptimalResourceNeeds", String.format("Evaluated need to invest in the production of gold as current balance is negative %d", gold_need_optimal));
                    person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                }else{
                    person.removeAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                    logAction("OptimalResourceNeeds", String.format("No need to invest in gold production as current balance is positive %d", gold_need_optimal));
                }

                if (hasImmediateResourceAspirations()) {
                    person.addAspiration(Aspiration.TRADE_MARKET);
                    logAction("OptimalResourceNeeds", "Good resource balance achieved. Ready to trade the market");
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * get all action classes that are about overall management
     * @return
     */
    public List<WeightedObject> getAllActions() {
        return allActions;
    }

    public boolean hasImmediateResourceAspirations() {
        return person.getAspirations().contains(Aspiration.GET_GOLD_INSTANTLY) ||
                person.getAspirations().contains(Aspiration.GET_FOOD_INSTANTLY) ||
                person.getAspirations().contains(Aspiration.GET_ALLOYS_INSTANTLY);
    }
}
