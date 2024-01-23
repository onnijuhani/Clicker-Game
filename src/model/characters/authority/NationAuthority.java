package model.characters.authority;

import model.characters.Character;
import time.Time;

public class NationAuthority extends Authority {
    @Override
    public void timeUpdate(int day, int month, int year) {
        if (day == Time.nationTax) {
            System.out.println("nation toimii");
            imposeTax();
            paySupporters();
        }
    }
    public NationAuthority(Character character) {
        super(character);
    }
}
