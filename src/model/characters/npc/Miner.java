package model.characters.npc;

import model.Settings;
import model.characters.Employment;
import model.characters.Peasant;
import model.characters.Status;
import model.characters.authority.Authority;

public class Miner extends Peasant {
    public static int totalAmount;

    public Miner(Authority quarterAuthority) {
        role.setAuthority(quarterAuthority);
        super.employment = new Employment(0, Settings.getInt("minerGenerate"), 0, person.getWorkWallet());
        totalAmount += 1;
        role.setStatus(Status.Miner);
    }

}
