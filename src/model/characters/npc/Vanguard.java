package model.characters.npc;

import model.characters.Status;
import model.characters.Support;
import model.characters.authority.Authority;
import model.resourceManagement.payments.Salary;

public class Vanguard extends Support {
    public static int totalAmount;

    public Vanguard(Authority authority) {
        super(authority);
        this.salary = new Salary(200, 100, 30);
        this.totalAmount += 1;
        this.status = Status.Vanguard;
    }
}
