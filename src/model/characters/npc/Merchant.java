package model.characters.npc;

import model.Settings;
import model.characters.Employment;
import model.characters.Peasant;
import model.characters.Status;
import model.characters.authority.Authority;

public class Merchant extends Peasant {
    public static int totalAmount;

    public Merchant(Authority quarterAuthority) {
        role.setAuthority(quarterAuthority);
        super.employment = new Employment(0, 0, Settings.getInt("merchantGenerate"), person.getWorkWallet());
        totalAmount += 1;
        role.setStatus(Status.Merchant);
    }
}
