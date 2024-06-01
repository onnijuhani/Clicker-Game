package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.Person;
import model.characters.Status;
import model.resourceManagement.TransferPackage;
import model.stateSystem.EventTracker;
import model.time.Time;

public class GoldMine extends UtilityBuilding {

    public GoldMine(int basePrice, Person owner) {
        super(basePrice, owner);
        this.value = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.GoldMine;

        if (owner.getPerson().getRole().getStatus() == Status.Miner) {
            addBonus("Miner bonus", 0.25);
        }
    }
    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                        value + " Gold"
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
        return new TransferPackage(0,0,value);
    }

    @Override
    public void updatePaymentCalendar(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.INCOME, Payment.GOLD_MINE_INCOME, getGenerateAmount(), Time.utilitySlots);
    }

}