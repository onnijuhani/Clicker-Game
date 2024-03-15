package model.characters.npc;

import model.Settings;
import model.characters.Employment;
import model.characters.Peasant;
import model.characters.Status;
import model.characters.authority.Authority;

public class Merchant extends Peasant {
    public static int totalAmount;

    public Merchant(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        super.employment = new Employment(0, 0, Settings.get("merchantGenerate"), workWallet);
        this.totalAmount += 1;
        setStatus(Status.Merchant);
    }
}
