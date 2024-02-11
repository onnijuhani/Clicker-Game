package model.characters.authority;

import model.characters.Character;
import model.time.Time;

public class NationAuthority extends Authority {
    @Override
    public void taxUpdate(int day, int month, int year) {
        if (day == Time.nationTax) {
            imposeTax();
            paySupporters();
            if(getSupervisor().equals(this)) {
                workWallet.setTaxedOrNot(true); //nation authority can set his own taxation status if he is his own authority
                workWallet.cashOutSalary();
            }
        }
    }
    public NationAuthority(Character character) {
        super(character);
    }
}
