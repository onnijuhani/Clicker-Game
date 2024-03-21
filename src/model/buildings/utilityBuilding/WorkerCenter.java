package model.buildings.utilityBuilding;

import model.characters.Person;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;

public class WorkerCenter extends SlaveFacility {


    public WorkerCenter(int basePrice, Person owner) {
        super(basePrice, owner);
        super.production[0] = 20;
        super.production[1] = 10;
        super.production[2] = 4;
    }

    @Override
    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(production[0]*calculateBonus(),production[1]*calculateBonus(), production[2]*calculateBonus());
        owner.getPerson().getWallet().addResources(transfer);
        owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
    }


}
