package model.characters.npc;

import model.Settings;
import model.characters.Status;
import model.characters.Support;
import model.characters.authority.Authority;
import model.resourceManagement.payments.Salary;

public class Noble extends Support {
    public static int totalAmount;
    private final int nobleBonusDays;
    private boolean nobleBonusUsed = false;

    public Noble(Authority authority) {
        super(authority);
        this.salary = new Salary(200, 75, 50);
        totalAmount += 1;
        role.setStatus(Status.Noble);

        this.nobleBonusDays = Settings.getRandom().nextInt(280) + 60;
    }

    public int getNobleBonusDays() {
        if(nobleBonusUsed) return 0;
        nobleBonusUsed = true;
        return nobleBonusDays;
    }
    public boolean isNobleBonusUsed() {
        return nobleBonusUsed;
    }
}
