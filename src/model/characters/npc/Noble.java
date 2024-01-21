package model.characters.npc;

import model.characters.Support;
import model.characters.authority.Authority;
import model.resourceManagement.payments.Salary;

public class Noble extends Support {
    public static int totalAmount;

    public Noble(Authority authority) {
        super(authority);
        this.salary = new Salary(15, 10, 5);
        this.totalAmount += 1;
    }
}
