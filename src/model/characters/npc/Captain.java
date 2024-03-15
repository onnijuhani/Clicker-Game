package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;

public class Captain extends AuthorityCharacter {
    public static int totalAmount;

    public Captain() {
        totalAmount += 1;
        setStatus(Status.Captain);
    }
}
