package model.characters.npc;

import model.characters.Character;
import model.characters.Peasant;

public class Slave extends Peasant {
    public static int totalAmount;
    Character owner;

    public Slave(Character owner) {
        this.owner = owner;
        super.employment = new Employment(10, 10, 1, workWallet);
        this.totalAmount += 1;
    }
}
