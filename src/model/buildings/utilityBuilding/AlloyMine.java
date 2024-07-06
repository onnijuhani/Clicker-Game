package model.buildings.utilityBuilding;

import model.Settings;
import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.Person;
import model.characters.Status;
import model.resourceManagement.TransferPackage;
import model.stateSystem.MessageTracker;
import model.time.Time;

public class AlloyMine extends UtilityBuilding {

    public AlloyMine(int basePrice, Person owner) {
        super(basePrice, owner);
        this.value = Settings.getInt("mineProduction");
        this.name = UtilityBuildings.AlloyMine;

        if (owner.getPerson().getRole().getStatus() == Status.Miner) {
            addBonus("Miner bonus", 0.25);
        }
    }

    @Override
    public String getInfo(){
        return (
                "Level " + getUpgradeLevel() + "\n"+
                        value*calculateBonus() + " Alloys"
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
        return new TransferPackage(0,value,0);
    }

    @Override
    public void updatePaymentManager(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.INCOME, Payment.ALLOY_MINE_INCOME, getGenerateAmount(), Time.utilitySlots);
    }



}
