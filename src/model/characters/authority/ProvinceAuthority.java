package model.characters.authority;

import model.characters.Character;
import time.Time;

public class ProvinceAuthority extends Authority {
    @Override
    public void timeUpdate(int day, int month, int year) {
        if (day == Time.provinceTax) {
            System.out.println("provinssi toimii toimii");
            imposeTax();
            paySupporters();
        }
    }
    public ProvinceAuthority(Character character) {
        super(character);
    }
}
