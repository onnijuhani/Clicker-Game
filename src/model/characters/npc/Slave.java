package model.characters.npc;

import model.characters.Character;
import model.characters.Employment;
import model.characters.Peasant;
import model.characters.Status;

public class Slave extends Peasant {
    public static int totalAmount;
    Character owner;

    public Slave(Character owner) {
        this.owner = owner;
        super.employment = new Employment(10, 5, 1, workWallet);
        this.totalAmount += 1;
        this.status = Status.Slave;
    }
}
