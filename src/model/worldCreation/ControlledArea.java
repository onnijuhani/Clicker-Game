package model.worldCreation;

import model.characters.authority.Authority;

public abstract class ControlledArea extends Area implements Details {
    public Authority getAuthority() {
        return authority;
    }
    protected Nation nation;
    Authority authority;
    public Nation getNation(){
        return nation;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
