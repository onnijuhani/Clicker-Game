package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.Person;
import model.characters.Status;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;
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
                        value + " Food"
                );
    }
    protected void generateAction() {
        TransferPackage transfer = getGenerateAmount();
        owner.getWallet().addResources(transfer);
        if (owner.isPlayer()) {
            owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName() + " generated" + transfer));
        }
    }

    private TransferPackage getGenerateAmount() {
        return new TransferPackage(value,0,0);
    }

    @Override
    public void updatePaymentCalendar(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.INCOME, Payment.GOLD_MINE_INCOME, getGenerateAmount(), Time.utilitySlots);
    }



}
