package model.characters;

import model.characters.authority.Authority;

public class AuthorityCharacter extends Character {


    public AuthorityCharacter() {
    }

    public Authority getAuthorityPosition() {
        return getRole().getPosition();
    }

    public void setAuthorityPosition(Authority authorityPosition) {
        getRole().setPosition(authorityPosition);
    }

}
