package model.characters.npc;

import model.characters.Character;
import model.characters.Peasant;

public class Slave extends Peasant {
    public static int totalAmount;
    Character owner;

    public Slave(Character owner) {
        this.owner = owner;
        super.employment = new Employment(5, 5, 0.5, workWallet);
        this.totalAmount += 1;
    }
}
