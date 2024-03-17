package model.characters.authority;

import model.characters.Character;
import model.time.Time;

public class CityAuthority extends Authority {
    @Override
    public void taxUpdate(int day, int month, int year) {
        if (day == Time.cityTax) {
            imposeTax();
            paySupporters();
        }
    }

    public CityAuthority(Character character) {
        super(character);
    }
}
