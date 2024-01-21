package model.characters.npc;

import model.characters.AuthorityCharacter;

public class King extends AuthorityCharacter {
    public static int totalAmount;

    public King() {
        this.totalAmount += 1;
    }
}
