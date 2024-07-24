package model.characters.ai;

import customExceptions.InsufficientResourcesException;
import model.Settings;
import model.buildings.Construct;
import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.AuthorityCharacter;
import model.characters.Person;
import model.characters.combat.CombatStats;
import model.resourceManagement.Resource;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.shop.Exchange;
import model.shop.UtilityShop;

import static model.characters.ai.actions.CombatActions.achieveHigherPosition;
import static model.characters.ai.actions.ManagementActions.tradeMarket;

public enum Aspiration {
    INCREASE_PERSONAL_OFFENCE {
        @Override
        public void handle(Person person) {
            CombatStats combatStats = person.getCombatStats();
            if(combatStats.upgradeOffenseWithGold()) {
                person.removeAspiration(INCREASE_PERSONAL_OFFENCE);
                person.logAction(this,"Default increase personal offence to level " + combatStats.getOffenseLevel());
            }

        }
    },
    INCREASE_PERSONAL_DEFENCE {
        @Override
        public void handle(Person person) {
            CombatStats combatStats = person.getCombatStats();
            if(combatStats.upgradeDefenceWithGold()){
                person.removeAspiration(INCREASE_PERSONAL_DEFENCE);
                person.logAction(this,"Increased personal defence to level " + combatStats.getDefenseLevel());
            }
        }
    },
    INCREASE_PROPERTY_DEFENCE {
        @Override
        public void handle(Person person) {
            Property property = person.getProperty();
            if (property.upgradeDefenceWithAlloys()) {
                person.removeAspiration(INCREASE_PROPERTY_DEFENCE);
                person.logAction(this,"Increased personal defence to level " + property.getDefenceStats().getUpgradeLevel());
            }
        }
    },
    ACHIEVE_HIGHER_POSITION {
        @Override
        public void handle(Person person) {
            achieveHigherPosition(person); // aspiration removed in CombatSystem
        }

    },
    SAVE_RESOURCES {
        @Override
        public void handle(Person person) {
        }
    },

    UPGRADE_PROPERTY{
        @Override
        public void handle(Person person) { // aspiration removed in Construct
            try {
                if (Construct.constructProperty(person)) {
                    person.logAction(this,String.format("New Property Construction started, current one is %s", person.getProperty().getClass().getSimpleName()));
                }
            } catch (InsufficientResourcesException e) {
                person.getProperty().getVault().withdrawal(person.getWallet(), e.getCost());
            }
        }
    },
    GET_GOLD_INSTANTLY {
        @Override
        public void handle(Person person) {
            person.removeAspiration(GET_GOLD_INSTANTLY);
            TransferPackage expenses = person.getPaymentManager().getFullExpenses();
            int gold_need_threshold = expenses.gold();
            Exchange exchange = person.getRole().getNation().getShop().getExchange();

            if (exchange.forceBuy(gold_need_threshold * 2, Resource.Gold, person)) {
                person.logAction(this,String.format("GET_GOLD_INSTANTLY removed and bought %d gold", gold_need_threshold * 2));
            }
        }
    },
    GET_FOOD_INSTANTLY {

        @Override
        public void handle(Person person) {
            person.removeAspiration(GET_FOOD_INSTANTLY);
            TransferPackage expenses = person.getPaymentManager().getFullExpenses();
            int food_need_threshold = expenses.food();
            Exchange exchange = person.getRole().getNation().getShop().getExchange();

            if (exchange.forceBuy(food_need_threshold * 2, Resource.Food, person)) {
                person.logAction(this,String.format("GET_FOOD_INSTANTLY removed and bought %d food", food_need_threshold * 2));
            }
        }
    },
    GET_ALLOYS_INSTANTLY {
        @Override
        public void handle(Person person) {
            person.removeAspiration(GET_ALLOYS_INSTANTLY);
            TransferPackage expenses = person.getPaymentManager().getFullExpenses();
            int alloy_need_threshold = expenses.alloy();
            Exchange exchange = person.getRole().getNation().getShop().getExchange();

            if (exchange.forceBuy(alloy_need_threshold * 2, Resource.Alloy, person)) {
                person.logAction(this, String.format("GET_ALLOYS_INSTANTLY removed and bought %d alloys", alloy_need_threshold * 2));
            }
        }
    },
    INVEST_IN_GOLD_PRODUCTION {
        @Override
        public void handle(Person person) {
            UtilitySlot utilitySlot = person.getProperty().getUtilitySlot();
            if (UtilityShop.upgradeBuilding(UtilityBuildings.GoldMine, person)) {
                person.logAction(this,"Because of need to invest in gold, Gold Mine has been upgraded to level " + utilitySlot.getAnyLevel(UtilityBuildings.GoldMine));
                person.removeAspiration(this);
            }
        }
    },
    INVEST_IN_ALLOY_PRODUCTION {
        @Override
        public void handle(Person person) {
            UtilitySlot utilitySlot = person.getProperty().getUtilitySlot();
            if (UtilityShop.upgradeBuilding(UtilityBuildings.AlloyMine, person)) {
                person.logAction(this,"Because of need to invest in Alloys, Alloy Mine has been upgraded to level " + utilitySlot.getAnyLevel(UtilityBuildings.AlloyMine));
                person.removeAspiration(this);
            }
        }
    },

    INVEST_IN_FOOD_PRODUCTION {
        @Override
        public void handle(Person person) {
            UtilitySlot utilitySlot = person.getProperty().getUtilitySlot();
            if (UtilityShop.upgradeBuilding(UtilityBuildings.MeadowLands, person)) {
                person.logAction(this,"Because of need to invest in food, Meadowlands has been upgraded to level " + utilitySlot.getAnyLevel(UtilityBuildings.MeadowLands));
                person.removeAspiration(this);
            }
        }
    },
    DEPOSIT_TO_VAULT {
        @Override
        public void handle(Person person) {
            Property property = person.getProperty();
            Wallet wallet = person.getWallet();

            TransferPackage vd = person.getPaymentManager().getNetCash().divide(Settings.getRandom().nextInt(15, 50));
            if (vd.isPositive()) {
                if (property.getVault().deposit(wallet, vd)) {
                    person.logAction(this, String.format("%s deposited to the Vault", vd));
                    person.addAspiration(Aspiration.INCREASE_PROPERTY_DEFENCE);
                    person.removeAspiration(this);
                }
            }
        }
    },
    SET_EXTREME_TAXES {
        @Override
        public void handle(Person person) {
            if(person.getCharacter() instanceof AuthorityCharacter ac){
                if(person.getAspirations().contains(Aspiration.SET_EXTREME_TAXES)) {
                    ac.getAuthorityPosition().getTaxForm().setExtremeTaxRate();
                    person.removeAspiration(this);
                }
            }
        }
    },




    SET_LOW_TAXES {
        @Override
        public void handle(Person person) {
            if(person.getCharacter() instanceof AuthorityCharacter ac){
                if(person.getAspirations().contains(Aspiration.SET_LOW_TAXES)) {
                    ac.getAuthorityPosition().getTaxForm().setLowTaxRate();
                    person.removeAspiration(this);
                }
            }
        }
    },

    SET_MEDIUM_TAXES {
        @Override
        public void handle(Person person) {
            if(person.getCharacter() instanceof AuthorityCharacter ac){
                if(person.getAspirations().contains(Aspiration.SET_MEDIUM_TAXES)) {
                    ac.getAuthorityPosition().getTaxForm().setMediumTaxRate();
                    person.removeAspiration(this);
                }
            }
        }
    },
    SET_STANDARD_TAX {
        @Override
        public void handle(Person person) {
            if(person.getCharacter() instanceof AuthorityCharacter ac){
                if(person.getAspirations().contains(Aspiration.SET_STANDARD_TAX)) {
                    ac.getAuthorityPosition().getTaxForm().setStandardTaxRate();
                    person.removeAspiration(this);
                }
            }
        }
    },
    TRADE_MARKET {
        @Override
        public void handle(Person person) {
            tradeMarket(person);
            person.removeAspiration(this);
        }
    };

    // Abstract method to be implemented by each aspiration
    public abstract void handle(Person person);





    public boolean spendsResources() {
        return this == INCREASE_PERSONAL_OFFENCE ||
                this == INCREASE_PERSONAL_DEFENCE ||
                this == INCREASE_PROPERTY_DEFENCE ||
                this == DEPOSIT_TO_VAULT ||
                this == INVEST_IN_ALLOY_PRODUCTION ||
                this == INVEST_IN_GOLD_PRODUCTION ||
                this == INVEST_IN_FOOD_PRODUCTION ||
                this == TRADE_MARKET;
    }




}
