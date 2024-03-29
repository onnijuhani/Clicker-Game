package model.characters.npc;

import model.characters.Status;
import model.characters.Support;
import model.characters.authority.Authority;
import model.resourceManagement.payments.Salary;

public class Mercenary extends Support {
    public static int totalAmount;

    public Mercenary(Authority authority) {
        super(authority);
        this.salary = new Salary(150, 75, 25);
        totalAmount += 1;
        role.setStatus(Status.Mercenary);
    }
}
