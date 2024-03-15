package model.characters;

import model.characters.authority.Authority;
import model.worldCreation.Nation;

public interface RoleBasedAttributes {
    Nation getNation();
    Authority getAuthority();
    Status getStatus();

    void setNation(Nation nation);
    void setAuthority(Authority authority);

    void setStatus(Status status);

}

