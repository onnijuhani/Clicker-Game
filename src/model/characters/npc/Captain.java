package model.characters.npc;

import model.characters.AuthorityCharacter;
import model.characters.Status;

public class Captain extends AuthorityCharacter {
    public static int totalAmount;

    public Captain() {
        this.totalAmount += 1;
        this.status = Status.Captain;
    }
}
