package model.characters;

import model.characters.authority.Authority;
import model.worldCreation.Nation;

public interface RoleBasedAttributes {
    Nation getNation();
    Authority getAuthority();
    int getFoodUpdateDay();


    void setNation(Nation nation);
    void setAuthority(Authority authority);
    void setFoodUpdateDay(int foodUpdateDay);

}

