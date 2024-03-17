package model.characters.npc;

import model.Settings;
import model.characters.Employment;
import model.characters.Peasant;
import model.characters.Status;
import model.characters.authority.Authority;

public class Farmer extends Peasant {
    public static int totalAmount;

    public Farmer(Authority quarterAuthority) {
        role.setAuthority(quarterAuthority);
        employment = new Employment(Settings.getInt("farmerGenerate"), 0, 0, person.getWorkWallet());
        totalAmount += 1;
        role.setStatus(Status.Farmer);
    }
}
