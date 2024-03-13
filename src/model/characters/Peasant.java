package model.characters;

import model.NameCreation;
import model.buildings.PropertyCreation;
import model.characters.authority.Authority;
import model.stateSystem.EventTracker;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.time.GenerateManager;
import model.time.GenerateObserver;

public class Peasant extends Character implements GenerateObserver {

    @Override
    public void generateUpdate() {
        getEmployment().generatePayment();
    }

    protected Authority quarterAuthority;
    protected double generateRate = 0;
    protected Employment employment;

    protected WorkWallet workWallet;

    public Peasant() {
        this.wallet = new Wallet();
        this.name = NameCreation.generateCharacterName();
        this.eventTracker = new EventTracker();
        this.workWallet = new WorkWallet(wallet);
        this.property = PropertyCreation.createPeasantProperty(this);
        GenerateManager.subscribe(this);
    }
    @Override
    protected boolean shouldSubscribeToTaxEvent() {
        return false;
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



}

