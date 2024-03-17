package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;
import model.characters.authority.Authority;

public class Governor extends AuthorityCharacter {
    public static int totalAmount;

    public Governor(Authority nationAuthority) {
        totalAmount += 1;
        role.setStatus(Status.Governor);
    }
}
