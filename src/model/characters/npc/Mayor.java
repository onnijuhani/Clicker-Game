package model.characters.npc;

import model.characters.AuthorityCharacter;

public class Mayor extends AuthorityCharacter {
    public static int totalAmount;

    public Mayor() {
        this.totalAmount += 1;
    }
}
