package model.characters.npc;

import model.characters.authority.AuthorityCharacter;

public class Captain extends AuthorityCharacter {
    public static int totalAmount;

    public Captain() {
        this.totalAmount += 1;
    }
}
