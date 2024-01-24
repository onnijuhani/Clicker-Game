package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;

public class Mayor extends AuthorityCharacter {
    public static int totalAmount;

    public Mayor() {
        this.totalAmount += 1;
        this.status = Status.Mayor;
    }
}
