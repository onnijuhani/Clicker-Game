package model.characters;

import model.buildings.PropertyCreation;
import model.time.GenerateManager;
import model.time.GenerateObserver;

public class Peasant extends Character implements GenerateObserver {

    @Override
    public void generateUpdate() {
        getEmployment().generatePayment();
    }

    protected Employment employment;

    public Peasant() {
        person.setProperty(PropertyCreation.createPeasantProperty(this));
        GenerateManager.subscribe(this);
    }
    @Override
    protected boolean shouldSubscribeToTaxEvent() {
        return false;
    }

    public Employment getEmployment() {
        return employment;
    }








}

