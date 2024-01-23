package model.characters;

import model.NameCreation;
import model.buildings.PropertyCreation;
import model.characters.authority.Authority;
import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import time.GenerateManager;
import time.GenerateObserver;

import java.util.LinkedList;

public class Peasant extends Character implements GenerateObserver {

    @Override
    public void generateUpdate() {
        getEmployment().generatePayment();
        if (getWorkWallet().isTaxed()) {
            cashOutSalary();
        }
    }


    protected Authority quarterAuthority;
    protected double generateRate = 0;
    protected Employment employment;

    protected WorkWallet workWallet;

    public Peasant() {
        this.wallet = new Wallet();
        this.slaves = new LinkedList<>();
        this.allies = new LinkedList<>();
        this.enemies = new LinkedList<>();
        this.name = NameCreation.generateCharacterName();
        this.eventTracker = new EventTracker();
        this.workWallet = new WorkWallet();
        this.property = PropertyCreation.createPeasantProperty(this);
        GenerateManager.subscribe(this);
    }
    @Override
    protected boolean shouldSubscribeToTimeEvent() {
        return false;
    }

    public void cashOutSalary() {
        wallet.depositAll(workWallet);
        workWallet.setTaxedOrNot(false);
    }

    public Employment getEmployment() {
        return employment;
    }


    public Authority getQuarterAuthority() {
        return quarterAuthority;
    }

    public void setQuarterAuthority(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
    }

    public double getGenerateRate() {
        return generateRate;
    }

    public void setGenerateRate(double generateRate) {
        this.generateRate = generateRate;
    }
    public void setEmployment(Employment employment) {
        this.employment = employment;
    }

    public WorkWallet getWorkWallet() {
        return workWallet;
    }

    public void setWorkWallet(WorkWallet workWallet) {
        this.workWallet = workWallet;
    }

    public class Employment {
        private double food;
        private double alloy;
        private double gold;
        private double bonusFood = 1;
        private double bonusAlloy = 1;
        private double bonusGold = 1;
        private WorkWallet workWallet;

        public Employment(double foodBaseRate, double alloyBaseRate, double goldBaseRate, WorkWallet workWallet) {
            this.food = foodBaseRate;
            this.alloy = alloyBaseRate;
            this.gold = goldBaseRate;
            this.workWallet = workWallet;
        }
        public void generatePayment() {
            TransferPackage transfer = new TransferPackage(food * bonusFood, alloy * bonusAlloy, gold * bonusGold);
            workWallet.addResources(transfer);
        }
        public void setBonusFood(double bonusFood) {
            this.bonusFood = bonusFood;
        }
        public void setBonusAlloy(double bonusAlloy) {
            this.bonusAlloy = bonusAlloy;
        }
        public void setBonusGold(double bonusGold) {
            this.bonusGold = bonusGold;
        }
        public void setFood(double food) {
            this.food = food;
        }
        public void setAlloy(double alloy) {
            this.alloy = alloy;
        }
        public void setGold(double gold) {
            this.gold = gold;
        }
    }
}

