package model.characters.npc;

import model.characters.Support;
import model.characters.Authority;
import model.resourceManagement.payments.Salary;

public class Vanguard extends Support {
    public static int totalAmount;

    public Vanguard(Authority authority) {
        super(authority);
        this.salary = new Salary(40, 30, 20);
        this.totalAmount += 1;
    }
}
