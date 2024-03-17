package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;
import model.characters.authority.Authority;

public class Mayor extends AuthorityCharacter {
    public static int totalAmount;

    public Mayor(Authority provinceAuthority) {
        totalAmount += 1;
        role.setStatus(Status.Mayor);
    }
}
