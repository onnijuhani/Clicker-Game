package model.characters.ai.actions;

import customExceptions.InsufficientResourcesException;
import model.buildings.Construct;
import model.buildings.Property;
import model.buildings.properties.Fortress;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.Person;
import model.characters.Trait;
import model.characters.ai.Aspiration;
import model.characters.ai.actionCircle.WeightedObject;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.shop.Exchange;
import model.shop.UtilityShop;
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
        EvaluateNeeds evaluateNeeds = new EvaluateNeeds(1, profile);
        TakeActionOnNeeds takeActionOnNeeds = new TakeActionOnNeeds(1, profile);


        allActions.add(evaluateNeeds);
        allActions.add(takeActionOnNeeds);
    }


    class TakeActionOnNeeds extends WeightedObject{


        public TakeActionOnNeeds(int weight, Map<Trait, Integer> profile) {
            super(weight, profile);
        }

        @Override
        public void defaultAction(){
            if(Time.getYear() == 0 && Time.getMonth() < 4){
                return; // quick return in early game to allow some generate ramp up
            }

            if (wallet.isEmpty() || wallet.isLowBalance()){
                return; // if wallet is empty or very low, return immediately. Nothing can be done here.
            }

            if(person.hasAspiration(Aspiration.GET_FOOD_INSTANTLY) && person.hasAspiration(Aspiration.GET_ALLOYS_INSTANTLY) && person.hasAspiration(Aspiration.GET_GOLD_INSTANTLY)){
                return; // quick return if there is need for everything, don't waste time and let production ramp up
            }


            Property property = person.getProperty();


            for (Aspiration aspiration : person.getAspirations()) {
                switch (aspiration) {
                    case UPGRADE_PROPERTY:

                        try {
                            Construct.constructProperty(person);
                            person.removeAspiration(Aspiration.UPGRADE_PROPERTY);
                        } catch (InsufficientResourcesException e) {
                            person.getProperty().getVault().withdrawal(wallet, e.getCost());
                        }

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

                        utilityShop.upgradeBuilding(UtilityBuildings.GoldMine, person);
                        break;

                    case INVEST_IN_ALLOY_PRODUCTION:

                        utilityShop.upgradeBuilding(UtilityBuildings.AlloyMine, person);
                        break;

                    case INVEST_IN_FOOD_PRODUCTION:

                        utilityShop.upgradeBuilding(UtilityBuildings.MeadowLands, person);
                        break;

                    default:// if there are no aspirations, make a deposit to vault if they are passive or unambitious. Others do nothing

                        if(profile.containsKey(Trait.Passive) || profile.containsKey(Trait.Unambitious)) {

                            if(profile.containsKey(Trait.Defender)){
                                property.upgradeDefence();
                            }

                            TransferPackage vaultDeposit = new TransferPackage(food_need_threshold, alloy_need_threshold, gold_need_threshold);
                            property.getVault().deposit(wallet, vaultDeposit);
                        }
                        break;
                }
            }
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

        /**
         * default is the only method here
         */
        @Override
        public void defaultAction() {

            if(Time.getYear() == 0 && Time.getMonth() < 4){
                return; // quick return in early game to allow some generate ramp up
            }

            if(!(person.getProperty() instanceof Fortress) && counter == 0) {  // fortress cannot be upgraded
                evaluatePropertyNeed();
            }

            evaluateResourceNeed();

            counter++; // property shouldn't be checked every time, every 100th time is just fine to prevent AI advancing too greedily
            if(counter == 100){
                counter = 0;
            }

        }

        /**
         * property need isn't important enough to get own Actions
         */
        private void evaluatePropertyNeed() {
            Property property = person.getProperty();

            //evaluate need for new property
            if(property.getUtilitySlot().getSlotAmount() == 5){
                //add something here eventually to upgrade into military properties

            }else if(property.getUtilitySlot().usedSlotAmount() == property.getUtilitySlot().getSlotAmount()){
                person.addAspiration(Aspiration.UPGRADE_PROPERTY); // remember to remove this need after new property is created.
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

            int gold = wallet.getAlloy();

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
