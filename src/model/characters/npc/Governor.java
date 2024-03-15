package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;

public class Governor extends AuthorityCharacter {
    public static int totalAmount;

    public Governor() {
        totalAmount += 1;
        setStatus(Status.Governor);
    }
}
