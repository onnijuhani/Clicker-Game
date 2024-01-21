package model.characters.npc;

import model.characters.Peasant;
import model.characters.authority.Authority;

public class Miner extends Peasant {
    public static int totalAmount;

    public Miner(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        super.employment = new Employment(0, 20, 0, workWallet);
        this.totalAmount += 1;
    }
}
