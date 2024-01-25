package model.characters.authority;

import model.characters.Character;

public class CityAuthority extends Authority {
    @Override
    public void timeUpdate(int day, int month, int year) {
            imposeTax();
            paySupporters();
        }

    public CityAuthority(Character character) {
        super(character);
    }
}
