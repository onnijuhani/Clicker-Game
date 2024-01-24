package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;

public class Governor extends AuthorityCharacter {
    public static int totalAmount;

    public Governor() {
        this.totalAmount += 1;
        this.status = Status.Governor;
    }
}
