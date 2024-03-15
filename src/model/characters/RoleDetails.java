package model.characters;

import model.characters.authority.Authority;
import model.worldCreation.Nation;

public class RoleDetails implements RoleBasedAttributes {
    private Nation nation;
    private Authority authority;
    private Status status;

    public RoleDetails() {
    }

    @Override
    public Nation getNation() {
        return nation;
    }

    @Override
    public void setNation(Nation nation) {
        this.nation = nation;
    }

    @Override
    public Authority getAuthority() {
        return authority;
    }

    @Override
    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
    @Override
    public Status getStatus() {
        return status;
    }
    @Override
    public void setStatus(Status status) {
        this.status = status;
    }





}
