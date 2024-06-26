package model.characters;

import model.buildings.PropertyFactory;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.WorkWallet;
import model.time.GenerateManager;
import model.time.GenerateObserver;

public class Peasant extends Character implements GenerateObserver {

    @Override
    public void generateUpdate() {
        getEmployment().generatePayment();

    }

    protected Employment employment;

    // Default constructor for NPC
    public Peasant() {
        super();
        initializePeasant();
    }
    // Constructor for player or NPC based on the passed flag
    public Peasant(boolean isPlayer) {
        super(isPlayer);
        initializePeasant();
    }

    private void initializePeasant() {
        person.setProperty(PropertyFactory.createPeasantProperty(person));
        GenerateManager.subscribe(this);
    }

    private TransferPackage countSalary(){
        double taxRate = getRole().getAuthority().getTaxForm().getTaxRate()  / 100;
        return employment.getFullSalary().multiply(taxRate);
    }



    public Employment getEmployment() {
        return employment;
    }

    public void createEmployment(int food, int alloy, int gold, WorkWallet workwallet) {
        if (this.employment != null) { // to make sure the old object is gone
            employment.clearResources();
        }
        this.employment = new Employment(food, alloy, gold, workwallet);
    }









}

