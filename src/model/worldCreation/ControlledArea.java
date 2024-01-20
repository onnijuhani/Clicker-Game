package model.worldCreation;

import model.characters.Authority;

public abstract class ControlledArea extends Area implements Details {
    public Authority getAuthority() {
        return authority;
    }

    Authority authority;
}
