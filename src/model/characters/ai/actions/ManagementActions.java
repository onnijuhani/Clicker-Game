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
        TakeActionOnNeeds takeActionOnNeeds = new TakeActionOnNeeds(15, profile);
        BalanceResources balanceResources = new BalanceResources(10,profile);


        allActions.add(evaluateNeeds);
        allActions.add(takeActionOnNeeds);
        allActions.add(balanceResources);
    }


    class BalanceResources extends WeightedObject{
        public BalanceResources(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }
        @Override
        public void execute(){
            defaultAction();
        }
        @Override
        public void defaultAction() {

            double[]currentRatio = person.getWallet().getBalanceRatio();
            double total = currentRatio[0] + currentRatio[1] + currentRatio[2];

            int[] desiredRatio;
            if (person.getProperty() instanceof MilitaryBuilding) {
                desiredRatio = new int[]{(int) (0.40 * total), (int) (0.40 * total), (int) (0.20 * total)};
            } else {
                desiredRatio = new int[]{(int) (0.33 * total), (int) (0.33 * total), (int) (0.33 * total)};
            }

            int food = wallet.getFood();
            int alloy = wallet.getAlloy();
            // balancing out food and alloy amounts should also balance gold.


            if(food > ( desiredRatio[0] + food/3) ){
                exchange.sellResource(food -desiredRatio[0], Resource.Food, person.getCharacter());
            }else{
                exchange.forceBuy(desiredRatio[0]-food, Resource.Food, person);
            }

            if(alloy > ( desiredRatio[1] + alloy/3) ){
                exchange.sellResource(alloy-desiredRatio[1],Resource.Alloy, person.getCharacter());
            }else{
                exchange.forceBuy(desiredRatio[1]-alloy, Resource.Alloy, person);
            }

        }
    }

    class TakeActionOnNeeds extends WeightedObject{
        int counter = 0;
        public TakeActionOnNeeds(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }

        @Override
        public void execute(){
            defaultAction();
        }

        @Override
        public void defaultAction(){

            if(Time.getYear() == 0 && Time.getMonth() < 4){
                return; // quick return in early game to allow some generate ramp up
            }
            System.out.println("take action 1");

            if  (wallet.isLowBalance()){
                return; // if wallet is empty or very low, return immediately. Nothing can be done here.
            }
            System.out.println("take action 2");
            if(person.hasAspiration(Aspiration.GET_FOOD_INSTANTLY) && person.hasAspiration(Aspiration.GET_ALLOYS_INSTANTLY) && person.hasAspiration(Aspiration.GET_GOLD_INSTANTLY)){
                return; // quick return if there is need for everything, don't waste time and let production ramp up
            }
            System.out.println("take action 3");
            Property property = person.getProperty();
            System.out.println("take action 4");
            System.out.println(person.getAspirations());
            for (Aspiration aspiration : person.getAspirations()) {
                System.out.println("take action 5");
                System.out.println(aspiration);
                switch (aspiration) {

                    case UPGRADE_PROPERTY:
                        System.out.println("UPROPERTY 1");
                        try {
                            System.out.println("UPROPERTY 2");
                            Construct.constructProperty(person);
                            person.removeAspiration(Aspiration.UPGRADE_PROPERTY);
                            person.removeAspiration(Aspiration.SAVE_RESOURCES);
                            System.out.println("UPROPERTY 4");
                        } catch (InsufficientResourcesException e) {
                            person.getProperty().getVault().withdrawal(wallet, e.getCost());
                            System.out.println("UPROPERTY 5");
                        }
                        System.out.println("UPROPERTY 6");
                        break;

                    case GET_GOLD_INSTANTLY:
                        System.out.println("GOLD 1");
                        exchange.forceBuy(gold_need_threshold * 2, Resource.Gold, person);
                        // gold is so important that is need is removed only by the Evaluate Needs method
                        break;

                    case GET_FOOD_INSTANTLY:
                        System.out.println("FOOD 1");
                        exchange.forceBuy(food_need_threshold * 2, Resource.Food, person);
                        person.removeAspiration(Aspiration.GET_FOOD_INSTANTLY);
                        break;

                    case GET_ALLOYS_INSTANTLY:
                        System.out.println("ALLOYS 1");
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
     * THIS METHOD SHOULD ONLY EVALUATE CURRENT NEEDS BUT !!!NOT!!! DO ANYTHING ABOUT THEM. JUST ADD IT INTO ASPIRATIONS
     * Having separate class for actually taking required action should improve performance
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

            if(!(person.getProperty() instanceof Fortress) && counter == 0) {  // fortress cannot be upgraded
                evaluatePropertyNeed();
            }else{
                person.addAspiration(Aspiration.INCREASE_PROPERTY_DEFENCE); // fortress defence should be updated instead
            }

            evaluateResourceNeed();

            counter++; // property shouldn't be checked every time, every 20th time is just fine to prevent AI advancing too greedily
            if(counter > 20){
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

        private void evaluateResourceNeed() {

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

    public List<WeightedObject> getAllActions() {
        return allActions;
    }
}
