package model.characters.authority;

import model.characters.Character;
import time.Time;

public class CityAuthority extends Authority {
    @Override
    public void timeUpdate(int day, int month, int year) {
        if (day == Time.cityTax) {
            System.out.println("city toimii  "+Time.cityTax+"   "+day);
            imposeTax();
            paySupporters();
        }
    }
    public CityAuthority(Character character) {
        super(character);
    }
}
