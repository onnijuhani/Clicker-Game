package model.characters.npc;

import model.Settings;
import model.characters.authority.Authority;
import model.characters.Peasant;

public class Farmer extends Peasant {
    public static int totalAmount;

    public Farmer(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        this.employment = new Employment(Settings.get("farmerGenerate"), 0, 0, workWallet);
        this.totalAmount += 1;
    }
}
