package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;

public class Mayor extends AuthorityCharacter {
    public static int totalAmount;

    public Mayor() {
        totalAmount += 1;
        setStatus(Status.Mayor);
    }
}
