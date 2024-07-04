package model.buildings.utilityBuilding;

import model.characters.payments.Payment;
import model.characters.payments.PaymentManager;
import model.characters.Person;
import model.characters.Trait;
import model.resourceManagement.TransferPackage;
import model.stateSystem.MessageTracker;
import model.time.Time;

import java.util.HashSet;
import java.util.Set;

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
        owner.getEventTracker().addEvent(MessageTracker.Message("Utility", this.getClass().getSimpleName() + " generated " + transfer));
    }
    @Override
    public void payConsequence() {
        synchronized (this) {
            Set<Person> alliesCopy = new HashSet<>(owner.getRelationsManager().getAllies());
            for (Person ally : alliesCopy) {

                if (!ally.getAiEngine().getProfile().containsKey(Trait.Liberal)) {
                    owner.getRelationsManager().removeAlly(ally);
                    ally.getRelationsManager().removeAlly(owner);

                    if(ally.getAiEngine().getProfile().containsKey(Trait.Slaver)){
                        owner.getRelationsManager().addEnemy(ally);
                        ally.getRelationsManager().addEnemy(owner);
                    }

                    if(owner.isPlayer()) {
                        owner.getEventTracker().addEvent(MessageTracker.Message("Minor", "Relationship with " + ally.getCharacter() + "\n\t\t\t\tcooled due to Work Center construction."));
                    }
                }
            }
        }

        Set<Person> enemiesCopy = new HashSet<>(owner.getRelationsManager().getEnemies());
        for (Person enemy : enemiesCopy) {
            if (enemy.getAiEngine().getProfile().containsKey(Trait.Liberal)) {
                owner.getRelationsManager().removeEnemy(enemy);
                enemy.getRelationsManager().removeEnemy(owner);
                if(owner.isPlayer()) {
                    owner.getEventTracker().addEvent(MessageTracker.Message("Minor", "Common interests in Work Center\n\t\t\t\thave improved your standing with " + enemy.getCharacter() + "."));
                }
            }
        }
    }

    private TransferPackage getGenerateAmount() {
        return new TransferPackage(production[0],production[1],production[2]);
    }

    @Override
    public void updatePaymentCalendar(PaymentManager calendar) {
        calendar.addPayment(PaymentManager.PaymentType.INCOME, Payment.WORKER_CENTER_INCOME, getGenerateAmount(), Time.utilitySlots);
    }


}
