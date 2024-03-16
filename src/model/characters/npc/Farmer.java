package model.characters.npc;

import model.Settings;
import model.characters.Employment;
import model.characters.Peasant;
import model.characters.Status;
import model.characters.authority.Authority;

public class Farmer extends Peasant {
    public static int totalAmount;

    public Farmer(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        this.employment = new Employment(Settings.getInt("farmerGenerate"), 0, 0, workWallet);
        totalAmount += 1;
        setStatus(Status.Farmer);
    }
}
