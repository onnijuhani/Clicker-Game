package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.Person;
import model.characters.Status;
import model.resourceManagement.TransferPackage;
import model.stateSystem.MessageTracker;
import model.time.Time;

public class Meadowlands extends UtilityBuilding {

    public Meadowlands(int basePrice, Person owner) {
        super(basePrice, owner);
        this.value = Settings.getInt("meadowLandsProduction");
        this.name = UtilityBuildings.MeadowLands;

        if (owner.getPerson().getRole().getStatus() == Status.Farmer) {
            addBonus("Farmer bonus", 0.50);
        }
    }
    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                        value*calculateBonus() + " Food"
                );
    }
    @Override
    protected void generateAction() {
        TransferPackage transfer = getGenerateAmount();
        owner.getWallet().addResources(transfer);
        if (owner.isPlayer()) {
            owner.getMessageTracker().addMessage(MessageTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
        }
    }

    private TransferPackage getGenerateAmount() {
        return new TransferPackage(value,0,0);
    }

    @Override
    public void updatePaymentManager(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.INCOME, Payment.MEADOWLANDS_INCOME, getGenerateAmount(), Time.utilitySlots);
    }



}
