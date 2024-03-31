package model.characters.ai.actions;

import customExceptions.InsufficientResourcesException;
import model.buildings.Construct;
import model.buildings.MilitaryBuilding;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ManagementActions {
    private final Person person;
    private final Wallet wallet;
    private final Exchange exchange;
    private final UtilityShop utilityShop;
    private final List<WeightedObject> allActions = new LinkedList<>();

    private final Map<Trait, Integer> profile;

    private int gold_need_threshold = 20;
    private int alloy_need_threshold = 100;
    private int food_need_threshold = 200;

    public ManagementActions(Person person, final Map<Trait, Integer> profile) {
        this.person = person;
        this.wallet = person.getWallet();
        this.exchange = person.getRole().getNation().getShop().getExchange();
        this.utilityShop = person.getRole().getNation().getShop().getUtilityShop();
        this.profile = profile;
        createAllActions();
    }
    private void createAllActions() {
        EvaluateNeeds evaluateNeeds = new EvaluateNeeds(10, profile);
        TakeActionOnNeeds takeActionOnNeeds = new TakeActionOnNeeds(10, profile);
        BalanceResources balanceResources = new BalanceResources(10,profile);


        allActions.add(evaluateNeeds);
        allActions.add(takeActionOnNeeds);
        allActions.add(balanceResources);
    }

    /**
     * This is the main class to balance out the wallet, only called max once per month.
     */
    class BalanceResources extends WeightedObject{
        private int foodBoughtCounter = 0;
        private int alloyBoughtCounter = 0;
        private int goldBoughtCounter = 0;
        private int lastCheck;
        public BalanceResources(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
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
            if (person.getProperty() instanceof MilitaryBuilding) {
                desiredRatio = new int[]{(int) (0.40 * total), (int) (0.40 * total), (int) (0.20 * total)};
            } else {
                desiredRatio = new int[]{(int) (0.33 * total), (int) (0.33 * total), (int) (0.33 * total)};
            }

            int food = wallet.getFood();
            int alloy = wallet.getAlloy();


            if(food > ( desiredRatio[0] + food/3) ){
                exchange.sellResource((food - desiredRatio[0]) / 2, Resource.Food, person.getCharacter());
                
                foodBoughtCounter--;
                goldBoughtCounter++;
                if(goldBoughtCounter > 4){
                    person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                }
                
            }else{
                exchange.forceBuy((desiredRatio[0]-food) / 2, Resource.Food, person);
                
                foodBoughtCounter++;
                goldBoughtCounter--;
                if(foodBoughtCounter > 4){
                    person.addAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                }
            }
            checkFood result = new checkFood(desiredRatio, alloy);


            if(result.alloy() > ( result.desiredRatio()[1] + result.alloy() /3) ){
                exchange.sellResource((result.alloy() - result.desiredRatio()[1]) / 2,Resource.Alloy, person.getCharacter());

                alloyBoughtCounter--;
                goldBoughtCounter++;
                if(goldBoughtCounter > 4){
                    person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                }
                
            }else {
                exchange.forceBuy((result.desiredRatio()[1] - result.alloy()) / 2, Resource.Alloy, person);

                alloyBoughtCounter++;
                goldBoughtCounter--;
                if (alloyBoughtCounter > 4) {
                    person.addAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                }
            }

        }
        private record checkFood(int[] desiredRatio, int alloy) {
        }
    }


    /**
     *  Only class to evaluate Role specific needs, should execute or add aspirations
     */

    class EvaluateRoleSpecificNeeds extends WeightedObject {

        public EvaluateRoleSpecificNeeds(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
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
                    person.addAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);

                case Miner:
                    person.addAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                    person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);

                case Merchant:

                    person.addAspiration(Aspiration.DEPOSIT_TO_VAULT);

                case Captain:

                    if(!evaluateExtremeTaxPolicy()){
                        if(!evaluateLowTaxPolicy()){
                            person.addAspiration(Aspiration.SET_STANDARD_TAX);
                        }
                    }


                case Mayor:

                    if(!evaluateExtremeTaxPolicy()){
                        if(!evaluateLowTaxPolicy()){
                            person.addAspiration(Aspiration.SET_STANDARD_TAX);
                        }
                    }


                case Governor:

                    if(!evaluateExtremeTaxPolicy()){
                        if(!evaluateLowTaxPolicy()){
                            person.addAspiration(Aspiration.SET_STANDARD_TAX);
                        }
                    }

                case Mercenary:



                case King:

                    if(!evaluateExtremeTaxPolicy()){
                        if(!evaluateLowTaxPolicy()){
                            person.addAspiration(Aspiration.SET_STANDARD_TAX);
                        }
                    }

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
                    return true;
                }
                if (loyal > 50 || unambitious > 70 || liberal > 40) {
                    person.addAspiration(Aspiration.SET_MEDIUM_TAXES);
                    return true;
                }
            }
            return false;
        }

    }

    /**
     * This class takes action on different management Aspirations, should not do any combat management here AT ALL. Only add combat aspirations if needed
     */
    class TakeActionOnNeeds extends WeightedObject{
        int counter = 0;
        public TakeActionOnNeeds(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
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
                            person.getEventTracker().addEvent(EventTracker.Message("Major" , "Tried construction property"));
                            Construct.constructProperty(person);
                            person.removeAspiration(Aspiration.UPGRADE_PROPERTY);
                            person.removeAspiration(Aspiration.SAVE_RESOURCES);
                            person.getEventTracker().addEvent(EventTracker.Message("Major" , "success construction property"));
                        } catch (InsufficientResourcesException e) {
                            person.getProperty().getVault().withdrawal(wallet, e.getCost());

                        }


                        break;

                    case GET_GOLD_INSTANTLY:

                        exchange.forceBuy(gold_need_threshold * 2, Resource.Gold, person);
                        // gold is so important that is need is removed only by the Evaluate Needs method
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
                        utilityShop.upgradeBuilding(UtilityBuildings.GoldMine, person);
                        break;

                    case INVEST_IN_ALLOY_PRODUCTION:
                        if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;
                        utilityShop.upgradeBuilding(UtilityBuildings.AlloyMine, person);
                        break;

                    case INVEST_IN_FOOD_PRODUCTION:
                        if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) return;
                        utilityShop.upgradeBuilding(UtilityBuildings.MeadowLands, person);
                        break;

                    default:// if there are no aspirations, make a deposit to vault and upgrade defence if they are passive or unambitious. Others do nothing

                        if (person.getAspirations().contains(Aspiration.SAVE_RESOURCES)) break;
                        if (counter < 1000) {counter++;break;}
                        if(profile.containsKey(Trait.Passive) || profile.containsKey(Trait.Unambitious)) {
                            TransferPackage vaultDeposit = new TransferPackage(food_need_threshold / 2, alloy_need_threshold / 2, gold_need_threshold / 2);
                            property.getVault().deposit(wallet, vaultDeposit);
                            person.getEventTracker().addEvent(EventTracker.Message("Major","Adding resources to vault " + vaultDeposit));
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

        public EvaluateNeeds(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
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

            if(!(person.getProperty() instanceof Fortress) && counter < 2) {  // fortress cannot be upgraded
                evaluatePropertyNeed();
            }

            evaluateMinimumResourceNeeds();


            counter++; // property shouldn't be checked every time, every 10th time is just fine to prevent AI advancing too greedily
            if(counter > 10){
                counter = 0;
            }

        }



        /**
         * property need isn't important enough to get own Actions
         */
        private void evaluatePropertyNeed() {
            Property property = person.getProperty();
            int usedSlotAmount = property.getUtilitySlot().usedSlotAmount();

            //evaluate need for new property
            if(property.getUtilitySlot().getSlotAmount() == 5){
                //TODO add something here eventually to upgrade into military properties

            }else if(usedSlotAmount == property.getUtilitySlot().getSlotAmount()) {
                if (property.getUtilitySlot().getTotalUpgradeLevels() > 3 * usedSlotAmount){ // idea here is to upgrade some of the utility buildings first before the property
                    person.addAspiration(Aspiration.UPGRADE_PROPERTY); // remember to remove this need after new property is created.
                    person.addAspiration(Aspiration.SAVE_RESOURCES); // remove this one too
                }
            }
        }

        private void evaluateMinimumResourceNeeds() {

            int food = wallet.getFood();

            if( food < food_need_threshold ) {
                person.addAspiration(Aspiration.GET_FOOD_INSTANTLY);
                if (food < food_need_threshold / 2){
                    person.addAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                } else{
                    person.removeAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                }
            } else {
                person.removeAspiration(Aspiration.INVEST_IN_FOOD_PRODUCTION);
                person.removeAspiration(Aspiration.GET_FOOD_INSTANTLY);
            }

            int alloy = wallet.getAlloy();

            if( alloy < alloy_need_threshold ) {
                person.addAspiration(Aspiration.GET_ALLOYS_INSTANTLY);
                if (alloy < alloy_need_threshold / 2){
                    person.addAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
                } else{
                    person.removeAspiration(Aspiration.GET_ALLOYS_INSTANTLY);
                }
            } else {
                person.removeAspiration(Aspiration.GET_ALLOYS_INSTANTLY);
                person.removeAspiration(Aspiration.INVEST_IN_ALLOY_PRODUCTION);
            }

            int gold = wallet.getGold();

            if( gold < gold_need_threshold ) {
                person.addAspiration(Aspiration.GET_GOLD_INSTANTLY);
                if (gold < food_need_threshold / 2){
                    person.addAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                } else{
                    person.removeAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
                }
            } else {
                person.removeAspiration(Aspiration.GET_GOLD_INSTANTLY);
                person.removeAspiration(Aspiration.INVEST_IN_GOLD_PRODUCTION);
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
}
