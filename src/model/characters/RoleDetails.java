package model.characters;

import model.characters.authority.Authority;
import model.worldCreation.Nation;

public class RoleDetails implements RoleBasedAttributes {
    private Nation nation;
    private Authority authority;
    private int foodUpdateDay;


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
    public int getFoodUpdateDay() {
        return foodUpdateDay;
    }

    @Override
    public void setFoodUpdateDay(int foodUpdateDay) {
        this.foodUpdateDay = foodUpdateDay;
    }


}
