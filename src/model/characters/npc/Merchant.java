package model.characters.npc;

import model.characters.Peasant;
import model.characters.Authority;

public class Merchant extends Peasant {
    public static int totalAmount;

    public Merchant(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        super.employment = new Employment(0, 0, 10, workWallet);
        this.totalAmount += 1;
    }
}
