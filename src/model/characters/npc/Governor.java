package model.characters.npc;

import model.characters.AuthorityCharacter;

public class Governor extends AuthorityCharacter {
    public static int totalAmount;

    public Governor() {
        this.totalAmount += 1;
    }
}
