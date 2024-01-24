package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;

public class King extends AuthorityCharacter {
    public static int totalAmount;

    public King() {
        this.totalAmount += 1;
        this.status = Status.King;
    }
}
