package model.characters.npc;

import model.Settings;
import model.characters.Employment;
import model.characters.Peasant;
import model.characters.Status;
import model.characters.authority.Authority;

public class Miner extends Peasant {
    public static int totalAmount;

    public Miner(Authority quarterAuthority) {
        this.quarterAuthority = quarterAuthority;
        super.employment = new Employment(0, Settings.get("minerGenerate"), 0, workWallet);
        totalAmount += 1;
        setStatus(Status.Miner);
    }

}
