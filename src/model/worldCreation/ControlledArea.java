package model.worldCreation;

import model.characters.authority.Authority;

public abstract class ControlledArea extends Area implements Details {
    public Authority getAuthority() {
        return authority;
    }
    Authority authority;
}
