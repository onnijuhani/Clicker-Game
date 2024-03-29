package model.characters;

import model.buildings.PropertyCreation;
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
        person.setProperty(PropertyCreation.createPeasantProperty(person));
        GenerateManager.subscribe(this);
    }



    public Employment getEmployment() {
        return employment;
    }

    public void createEmployment(int food, int alloy, int gold, WorkWallet workwallet) {
        this.employment = new Employment(food, alloy, gold, workwallet);
    }









}

