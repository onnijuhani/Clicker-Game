package model.characters;

import model.TimeObserver;
import model.buildings.PropertyCreation;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.WorkWallet;

public class Peasant extends Character implements TimeObserver {
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

    @Override
    public void timeUpdate(int currentDay, int currentWeek, int currentMonth, int currentYear) {
        if (currentDay == 1) {
            getEmployment().generatePayment();
            if (getWorkWallet().isTaxed()) {
                cashOutSalary();
                getWorkWallet().setTaxedOrNot(false);
            }
            System.out.println(getWorkWallet() + "   " + getWallet());
        }
    }

    protected WorkWallet workWallet;
    protected Authority quarterAuthority;
    protected double generateRate = 0;
    protected Employment employment;

    public Peasant() {
        this.workWallet = new WorkWallet();
        super.property = PropertyCreation.createPeasantProperty(this);
    }

    public void cashOutSalary() {
        wallet.depositAll(workWallet);
    }

    public Employment getEmployment() {
        return employment;
    }

    public WorkWallet getWorkWallet() {
        return workWallet;
    }

    public void setWorkWallet(WorkWallet workWallet) {
        this.workWallet = workWallet;
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
}
