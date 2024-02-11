package model.characters.authority;

import model.characters.Character;
import model.time.Time;

public class ProvinceAuthority extends Authority {
    @Override
    public void taxUpdate(int day, int month, int year) {
        if (day == Time.provinceTax) {
            imposeTax();
            paySupporters();

        }

    }
    public ProvinceAuthority(Character character) {
        super(character);
    }
}
