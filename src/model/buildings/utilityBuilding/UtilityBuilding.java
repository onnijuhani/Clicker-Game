package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.payments.PaymentManager;
import model.characters.Person;
import model.characters.payments.PaymentTracker;
import model.resourceManagement.TransferPackage;
import model.shop.UpgradeSystem;
import model.stateSystem.MessageTracker;
import model.time.UtilityManager;
import model.time.UtilityObserver;

public class UtilityBuilding extends UpgradeSystem implements UtilityObserver, PaymentTracker {

    protected UtilityBuildings name;

    protected Person owner;
    protected int MAX_LEVEL = Settings.getInt("utilityMaxLevel");
    protected int increaseDivider = 2;


    @Override
    public void utilityUpdate() {
        generateAction();
    }
    public String getInfo(){
        return "empty";
    }


    public UtilityBuilding(int basePrice, Person owner) {
        super(basePrice);
        this.owner = owner;
        UtilityManager.subscribe(this);

    }

    protected void generateAction() {
        TransferPackage transfer = new TransferPackage(0,0,0);
        owner.getWallet().addResources(transfer);
        if (owner.isPlayer()) {
            owner.getMessageTracker().addMessage(MessageTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
        }
    }

    @Override
    public void updatePaymentManager(PaymentManager calendar) {

    }
}
