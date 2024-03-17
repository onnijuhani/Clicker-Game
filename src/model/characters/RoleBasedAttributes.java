package model.characters;

import model.characters.authority.Authority;
import model.worldCreation.Nation;

public interface RoleBasedAttributes {
    Nation getNation();
    Authority getAuthority();
    Status getStatus();
    Authority getPosition();

    void setNation(Nation nation);
    void setAuthority(Authority authority);
    void setStatus(Status status);
    void setPosition(Authority authority);

}

