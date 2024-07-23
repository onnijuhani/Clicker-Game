package model.characters.ai.actions;

import customExceptions.InsufficientResourcesException;
import model.buildings.Construct;
import model.buildings.Property;
import model.buildings.properties.Castle;
import model.buildings.properties.Citadel;
import model.buildings.properties.Fortress;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.buildings.utilityBuilding.UtilitySlot;
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
import model.time.Time;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings("CallToPrintStackTrace")
public class ManagementActions extends BaseActions {
    private int gold_need_threshold = 20;
    private int alloy_need_threshold = 100;
    private int food_need_threshold = 200;
    private final Wallet wallet = person.getWallet();
    private final Exchange exchange = person.getRole().getNation().getShop().getExchange();


    private static final Predicate<Person> hasInvestInFood = person -> person.hasAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
    private static final Predicate<Person> hasInvestInAlloys = person -> person.hasAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
    private static final Predicate<Person> hasInvestInGold = person -> person.hasAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
    private static final Predicate<Person> hasGetFoodInstantly = person -> person.hasAspiration(Aspiration.GET_FOOD_INSTANTLY);
    private static final Predicate<Person> hasGetAlloysInstantly = person -> person.hasAspiration(Aspiration.GET_ALLOYS_INSTANTLY);
    private static final Predicate<Person> hasGetGoldInstantly = person -> person.hasAspiration(Aspiration.GET_GOLD_INSTANTLY);
    private static final Predicate<Person> hasInvestInGuilds = person ->   person.hasAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION)
                                                                        || person.hasAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION)
                                                                        && person.hasAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);

    private static final Predicate<Person> hasInvestInMines = person ->   person.hasAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION)
                                                                          && person.hasAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);

    private static final Predicate<Person> hasSetExtremeTax = person -> person.hasAspiration(Aspiration.SET_EXTREME_TAXES);
    private static final Predicate<Person> hasSetLowTax = person -> person.hasAspiration(Aspiration.SET_LOW_TAXES);
    private static final Predicate<Person> hasSetStandardTax = person -> person.hasAspiration(Aspiration.SET_STANDARD_TAX);
    private static final Predicate<Person> hasSetMediumTax = person -> person.hasAspiration(Aspiration.SET_MEDIUM_TAXES);

    public ManagementActions(Person person, NPCActionLogger npcActionLogger, Map<Trait, Integer> profile) {
        super(person, npcActionLogger, profile);
    }


    @Override
    protected void createAllActions() {
        EvaluateNeeds evaluateNeeds = new EvaluateNeeds(person, npcActionLogger,1,profile);
        TakeActionOnNeeds takeActionOnNeeds = new TakeActionOnNeeds(person, npcActionLogger,10,profile);
        TradeMarket tradeMarket = new TradeMarket(person, npcActionLogger,5,profile);
        EvaluateRoleSpecificNeeds evaluateRoleSpecificNeeds = new EvaluateRoleSpecificNeeds(person, npcActionLogger,5,profile);


        allActions.add(evaluateNeeds);
        allActions.add(takeActionOnNeeds);
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

            if(person.hasAspiration(Aspiration.SAVE_RESOURCES)) return;

            int gold = wallet.getGold();
            double fMarketRatio = exchange.getWallet().getBalanceRatio()[0];
            double aMarketRatio = exchange.getWallet().getBalanceRatio()[1];

            int amountGoldToSpend = gold - (gold / 4);

            double foodRatio = 0.625;
            double alloyRatio = 0.313;

            if (fMarketRatio < foodRatio && amountGoldToSpend > 0){
                int amountBefore = wallet.getFood();
                if(exchange.exchangeResources(amountGoldToSpend*2, Resource.Food, Resource.Gold, person.getCharacter())) {
                    int amountAfter = wallet.getFood() -amountBefore;
                    logAction(String.format("Bought %d food to balance the market since market has %1f ratio of food. Spent %d Gold", amountAfter , fMarketRatio, amountGoldToSpend*2));
                    person.removeAspiration(Aspiration.TRADE_MARKET);
                }
            }

            if (aMarketRatio < alloyRatio && amountGoldToSpend > 0){
                int amountBefore = wallet.getAlloy();
                if(exchange.exchangeResources(amountGoldToSpend, Resource.Alloy, Resource.Gold, person.getCharacter())) {
                    int amountAfter = wallet.getAlloy() -amountBefore;
                    logAction(String.format("Bought %d alloys to balance the market since market has %2f ratio of alloys. Spent %d Gold", amountAfter, aMarketRatio, amountGoldToSpend));
                    person.removeAspiration(Aspiration.TRADE_MARKET);
                }
            }

//            logAction(amountGoldToSpend+ " " +fMarketRatio+ " " +aMarketRatio+ " " +foodRatio+ " " +alloyRatio);
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

            try {
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
                            if(evaluateLowTaxPolicy() && !hasSetLowTax.test(person)){
                                person.addAspiration(Aspiration.SET_STANDARD_TAX);
                            }
                        }
                        person.removeAspiration(Aspiration.ACHIEVE_HIGHER_POSITION);
    
    
                    case Noble:
    
    
    
                    case Vanguard:
    
    
    
                    case Peasant:
    
                    }
            } catch (Exception e) {
                e.printStackTrace();throw new RuntimeException(e);
            }
        }
            

        private boolean evaluateExtremeTaxPolicy() {
            Integer aggressive = person.getAiEngine().getProfile().get(Trait.Aggressive);
            Integer disloyal = person.getAiEngine().getProfile().get(Trait.Disloyal);


            if (aggressive != null && disloyal != null && !hasSetExtremeTax.test(person)) {
                if (aggressive > 25 && disloyal > 25 && doesntHaveTaxAspirationYet()) {
                    person.addAspiration(Aspiration.SET_EXTREME_TAXES);
                    logAction("aggressive and disloyal", "Added aspiration to set extreme tax policy for having aggressive and disloyal personality");
                    return true;
                }
                if (aggressive > 75 || disloyal > 75 && doesntHaveTaxAspirationYet()) {
                    person.addAspiration(Aspiration.SET_EXTREME_TAXES);
                    logAction("aggressive or disloyal", "Added aspiration to set extreme tax policy for having very high aggressive or disloyal personality");
                    return true;
                }
            }

            Integer slaver = person.getAiEngine().getProfile().get(Trait.Slaver);
            if( slaver != null && slaver > 20 && !hasSetExtremeTax.test(person) && doesntHaveTaxAspirationYet()) {
                person.addAspiration(Aspiration.SET_EXTREME_TAXES);
                logAction("slaver", "Added aspiration to set extreme tax policy for being a slaver personality");
                return true;
            }

            return false;

        }

        private boolean evaluateLowTaxPolicy() {
            try {
                Integer loyal = person.getAiEngine().getProfile().get(Trait.Loyal);
                Integer unambitious = person.getAiEngine().getProfile().get(Trait.Unambitious);
                Integer liberal = person.getAiEngine().getProfile().get(Trait.Liberal);

                if (loyal != null && unambitious != null && liberal != null && !hasSetLowTax.test(person)) {
                    if (loyal > 10 && unambitious > 10 && liberal > 10 && doesntHaveTaxAspirationYet()) {
                        person.addAspiration(Aspiration.SET_LOW_TAXES);
                        logAction("loyal and unambitious and liberal", "Added aspiration to set low tax policy for having loyal and unambitious and liberal personality");
                        return false;
                    }
                    if (loyal > 50 || unambitious > 70 || liberal > 40 && !hasSetLowTax.test(person) && doesntHaveTaxAspirationYet()) {
                        person.addAspiration(Aspiration.SET_MEDIUM_TAXES);
                        logAction("loyal or unambitious or liberal", "Added aspiration to set low tax policy for having high loyal, unambitious or liberal personality");
                        return false;
                    }
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();throw new RuntimeException(e);
            }
        }
    }

    private boolean doesntHaveTaxAspirationYet(){
        return person.hasAspiration(Aspiration.SET_MEDIUM_TAXES)
                || person.hasAspiration(Aspiration.SET_LOW_TAXES)
                || person.hasAspiration(Aspiration.SET_EXTREME_TAXES)
                || person.hasAspiration(Aspiration.SET_STANDARD_TAX);
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
         * This should be secondary balance mechanism to BalanceWallet class
         */
        private void balanceSkewedResources() {

            double fRatio = wallet.getBalanceRatio()[0];
            double aRatio = wallet.getBalanceRatio()[1];
            double gRatio = wallet.getBalanceRatio()[2];

            if (fRatio > 0.50){
                int amountToSell = wallet.getFood() / 2;
                if(exchange.sellResource(amountToSell, Resource.Food, person.getCharacter())){
                logAction(String.format("Sold %d food to balance out skewed wallet. Ratio was %f", amountToSell, fRatio));
                }
            }else if (aRatio > 0.60){
                int amountToSell = wallet.getAlloy() / 2;
                exchange.sellResource(amountToSell, Resource.Alloy, person.getCharacter());
                logAction(String.format("Sold %d alloys to balance out skewed wallet. Ratio was %f", amountToSell, aRatio));
            }else if (gRatio > 0.70){
                int amountBefore = person.getWallet().getGold();
                exchange.exchangeResources(wallet.getGold() / 4 * 10, Resource.Food, Resource.Gold, person.getCharacter());
                exchange.exchangeResources(wallet.getGold() / 4 * 5, Resource.Alloy, Resource.Gold, person.getCharacter());
                int amountAfter = person.getWallet().getGold() - amountBefore;
                logAction(String.format("Sold %d gold to balance out skewed wallet Ratio was %f", amountAfter, gRatio));

            }

        }

        @Override
        public void defaultAction(){

            try {
                if(Time.getYear() == 0 && Time.getMonth() < 2){
                    return; // quick return in early game to allow some generate ramp up
                }


                if  (wallet.isLowBalance()){
                    return; // if wallet is empty or very low, return immediately.
                }

                if (immediateNeedForEverything()) return; // quick return if there is need for everything,  let production ramp up

                Property property = person.getProperty();
                UtilitySlot utilitySlot = property.getUtilitySlot();

                for (Aspiration aspiration : person.getAspirations()) {
    
    
                    if(person.getAspirations().contains(Aspiration.SAVE_RESOURCES) && person.getAspirations().contains(Aspiration.UPGRADE_PROPERTY)){
                        if(!aspiration.equals(Aspiration.UPGRADE_PROPERTY)){
                            continue; // make sure upgrade is prioritized so resources can be saved.
                        }
                    }
    
                    switch (aspiration) {
    
                        case UPGRADE_PROPERTY:
    
                            try {
                                Construct.constructProperty(person);
                                person.removeAspiration(Aspiration.UPGRADE_PROPERTY);
                                person.removeAspiration(Aspiration.SAVE_RESOURCES);
                                logAction(String.format("New Property Construction started, current one is %s", person.getProperty().getClass().getSimpleName()));
                            } catch (InsufficientResourcesException e) {
                                person.getProperty().getVault().withdrawal(wallet, e.getCost());
    
                            }
                            break;
    
                        case GET_GOLD_INSTANTLY:
                            if (exchange.forceBuy(gold_need_threshold * 2, Resource.Gold, person)) {
                                person.removeAspiration(Aspiration.GET_GOLD_INSTANTLY);
                                logAction(String.format("GET_GOLD_INSTANTLY removed and bought %d gold", gold_need_threshold * 2));
                            }
                            break;
    
                        case GET_FOOD_INSTANTLY:
                            if (exchange.forceBuy(food_need_threshold * 2, Resource.Food, person)) {
                                person.removeAspiration(Aspiration.GET_FOOD_INSTANTLY);
                                logAction(String.format("GET_FOOD_INSTANTLY removed and bought %d food", food_need_threshold * 2));
                            }
                            break;
    
                        case GET_ALLOYS_INSTANTLY:
                            if (exchange.forceBuy(alloy_need_threshold * 2, Resource.Alloy, person)) {
                                person.removeAspiration(Aspiration.GET_ALLOYS_INSTANTLY);
                                logAction(String.format("GET_ALLOYS_INSTANTLY removed and bought %d alloys", alloy_need_threshold * 2));
                            }
    
                        case INVEST_IN_GOLD_PRODUCTION:

                            if(hasInvestInGuilds.test(person)){
                                if(UtilityShop.upgradeBuilding(UtilityBuildings.SlaveFacility, person)){
                                logAction("Because of need to invest in food, gold and alloys, SlaveFacility has been upgraded to level " + utilitySlot.getAnyLevel(UtilityBuildings.SlaveFacility));
                                };
                                if(UtilityShop.upgradeBuilding(UtilityBuildings.WorkerCenter, person)){
                                    logAction("Because of need to invest in food, gold and alloys, SlaveFacility has been upgraded to level " + utilitySlot.getAnyLevel(UtilityBuildings.WorkerCenter));
                                };
                            }
                            else if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;
                            if(UtilityShop.upgradeBuilding(UtilityBuildings.GoldMine, person)){
                                logAction("Because of need to invest in gold, Gold Mine has been upgraded to level " + utilitySlot.getAnyLevel(UtilityBuildings.GoldMine));
                            };
    
                            break;
    
                        case INVEST_IN_ALLOY_PRODUCTION:
                            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;
                            if(UtilityShop.upgradeBuilding(UtilityBuildings.AlloyMine, person)){
                                logAction("Because of need to invest in alloys, Alloy Mine has been upgraded to level " + utilitySlot.getAnyLevel(UtilityBuildings.AlloyMine));
                            }
                            break;
    
                        case INVEST_IN_FOOD_PRODUCTION:
                            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;
                            if(UtilityShop.upgradeBuilding(UtilityBuildings.MeadowLands, person)){
                                logAction("Because of need to invest in food, Meadowlands has been upgraded to level " + utilitySlot.getAnyLevel(UtilityBuildings.MeadowLands));
                            };
                            break;
    
                        default:// if there are no aspirations, make a deposit to vault and upgrade defence if they are passive or unambitious. Others do nothing
    
                            if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) break;
    
                            if (counter < 5) {counter++;break;}
    
                            if(profile.containsKey(Trait.Passive) || profile.containsKey(Trait.Unambitious)) {
                                TransferPackage netCash = person.getPaymentManager().getNetCash();
                                TransferPackage vaultDeposit = new TransferPackage(netCash.food(), netCash.alloy(), netCash.gold());

                                if(vaultDeposit.isPositive()) {
                                    if (property.getVault().deposit(wallet, vaultDeposit)) {
                                        logAction("passive and or unambitious", String.format("%s deposited to the Vault", vaultDeposit));
                                        person.addAspiration(Aspiration.INCREASE_PROPERTY_DEFENCE);
                                    }
                                }
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();throw new RuntimeException(e);
            }
        }
    }



    /**
     *  THIS CLASS SHOULD ONLY EVALUATE CURRENT MANAGEMENT NEEDS BUT NOT DO ANYTHING ABOUT THEM. JUST ADD IT INTO ASPIRATIONS
     */
    class EvaluateNeeds extends WeightedObject {
        public EvaluateNeeds(Person person, NPCActionLogger npcActionLogger, int weight, Map<Trait, Integer> profile) {
            super(person, npcActionLogger, weight, profile);
        }
        @Override
        public void execute(){
            defaultAction();
        }
        @Override
        public void defaultAction() {

            if(Time.getYear() == 0 && Time.getMonth() < 1){
                return; // quick return in early game to allow some generate ramp up
            }

            if(!(person.getProperty() instanceof Fortress)) {  // fortress cannot be upgraded
                if(person.isPlayer()){
                    System.out.println("lol");
                }
                evaluatePropertyNeed();
            }

            evaluateMinimumResourceNeeds();
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
                TransferPackage netBalance = person.getPaymentManager().getNetBalance();
                if(netBalance.food() > 2000 && netBalance.alloy() > 1000 && netBalance.gold() > 100){
                    person.addAspiration(Aspiration.UPGRADE_PROPERTY);
                    person.addAspiration(Aspiration.SAVE_RESOURCES);
                    logAction(String.format("Evaluated a need to upgrade current home %s and a need to save resources for that", person.getProperty().getClass().getSimpleName()));
                }
                if(property instanceof Castle){
                    if(person.getRole().getStatus().isCitadelWorthy()){
                        person.addAspiration(Aspiration.UPGRADE_PROPERTY);
                        person.addAspiration(Aspiration.SAVE_RESOURCES);
                        logAction(String.format("Evaluated a need to upgrade current home %s and a need to save resources for that", person.getProperty().getClass().getSimpleName()));
                    }
                }
                if(property instanceof Citadel){
                    if(person.getRole().getStatus().isFortressWorthy()){
                        person.addAspiration(Aspiration.UPGRADE_PROPERTY);
                        person.addAspiration(Aspiration.SAVE_RESOURCES);
                        logAction(String.format("Evaluated a need to upgrade current home %s and a need to save resources for that", person.getProperty().getClass().getSimpleName()));
                    }
                }

            }else if(usedSlotAmount == property.getUtilitySlot().getSlotAmount()) {
                if (property.getUtilitySlot().getTotalUpgradeLevels() > 2 * usedSlotAmount){ // idea here is to upgrade some of the utility buildings first before the property
                    person.addAspiration(Aspiration.UPGRADE_PROPERTY); // remember to remove this need after new property is created.
                    person.addAspiration(Aspiration.SAVE_RESOURCES); // remove this one too
                    logAction(String.format("Evaluated a need to upgrade current home %s and a need to save resources for that", person.getProperty().getClass().getSimpleName()));
                }
            }
        }

        private void evaluateMinimumResourceNeeds() {
            try {
                int food = wallet.getFood();
                TransferPackage expenses = person.getPaymentManager().getFullExpenses();

                food_need_threshold = expenses.food();
                alloy_need_threshold = expenses.alloy();
                gold_need_threshold = expenses.gold();

                if( food < food_need_threshold ) {
                    if (!hasGetFoodInstantly.test(person)) {
                        logAction("MinimumResourceNeeds", String.format("Evaluated a lack of food as current balance is below threshold of %d", food_need_threshold));
                        person.addAspiration(Aspiration.GET_FOOD_INSTANTLY);
                    }
                    if (food < food_need_threshold / 2 && !hasInvestInFood.test(person)){
                        person.addAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                    }
                }

                int alloy = wallet.getAlloy();

                if( alloy < alloy_need_threshold ) {
                    if (!hasGetAlloysInstantly.test(person)) {
                        logAction("MinimumResourceNeeds", String.format("Evaluated a lack of alloys as current balance is below threshold of %d", alloy_need_threshold));
                        person.addAspiration(Aspiration.GET_ALLOYS_INSTANTLY);
                    }
                    if (alloy < alloy_need_threshold / 2 && !hasInvestInAlloys.test(person)){
                        person.addAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                    }
                }

                int gold = wallet.getGold();

                if( gold < gold_need_threshold ) {
                    if (hasGetGoldInstantly.test(person)) {
                        logAction("MinimumResourceNeeds", String.format("Evaluated a lack of gold as current balance is below threshold of %d", gold_need_threshold));
                        person.addAspiration(Aspiration.GET_GOLD_INSTANTLY);
                    }
                    if (gold < food_need_threshold / 2 && !hasInvestInGold.test(person)){
                        person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();throw new RuntimeException(e);
            }
        }

        private void evaluateOptimalResourceNeeds() {
            try {
                TransferPackage netBalance = person.getPaymentManager().getNetBalance();

                logAction(String.format("Current free cash flow: %s", netBalance.toShortString()));

                int food_need_optimal = netBalance.food();
                int alloy_need_optimal = netBalance.alloy();
                int gold_need_optimal = netBalance.gold();

                if( food_need_optimal < 150 ) {
                    if (!hasInvestInFood.test(person)) {
                        logAction("OptimalResourceNeeds", String.format("Evaluated need to invest in the production of food as current balance is negative %d", food_need_optimal));
                        person.addAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                    }
                } else {
                    person.removeAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                }


                if( alloy_need_optimal < 250 ) {
                    if (!hasInvestInAlloys.test(person)) {
                        logAction("OptimalResourceNeeds", String.format("Evaluated need to invest in the production of alloys as current balance is negative %d", alloy_need_optimal));
                        person.addAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                    }
                }else{
                    person.removeAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                }

                if( gold_need_optimal < 500 ) {
                    if(!hasInvestInGold.test(person)) {
                        logAction("OptimalResourceNeeds", String.format("Evaluated need to invest in the production of gold as current balance is negative %d", gold_need_optimal));
                        person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                    }
                }else{
                    person.removeAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                }

                if (!hasImmediateResourceAspirations()) {
                    if(person.hasAspiration(Aspiration.TRADE_MARKET)){
                        return;
                    }
                    person.addAspiration(Aspiration.TRADE_MARKET);
                    logAction("OptimalResourceNeeds", "Good resource balance achieved. Ready to trade the market");
                }

            } catch (Exception e) {
                e.printStackTrace();throw new RuntimeException(e);
            }
        }
    }


    /**
     * get all action classes that are about overall management
     * @return list of all actions
     */
    public List<WeightedObject> getAllActions() {
        return allActions;
    }

    public boolean hasImmediateResourceAspirations() {
        return person.getAspirations().contains(Aspiration.GET_GOLD_INSTANTLY) ||
                person.getAspirations().contains(Aspiration.GET_FOOD_INSTANTLY) ||
                person.getAspirations().contains(Aspiration.GET_ALLOYS_INSTANTLY);
    }
    private boolean immediateNeedForEverything() {
        return person.hasAspiration(Aspiration.GET_FOOD_INSTANTLY) && person.hasAspiration(Aspiration.GET_ALLOYS_INSTANTLY) && person.hasAspiration(Aspiration.GET_GOLD_INSTANTLY);
    }
}
