package model.characters.npc;

import model.characters.Authority;
import model.characters.Peasant;

public class Farmer extends Peasant {
    public static int totalAmount;

    public Farmer(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        super.employment = new Employment(20, 0, 0, workWallet);
        this.totalAmount += 1;
    }
}
