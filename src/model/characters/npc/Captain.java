package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;
import model.characters.authority.Authority;

public class Captain extends AuthorityCharacter {
    public static int totalAmount;

    public Captain(Authority cityAuthority) {
        totalAmount += 1;
        role.setStatus(Status.Captain);
    }
}
