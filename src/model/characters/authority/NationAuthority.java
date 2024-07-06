package model.characters.authority;

import model.characters.Character;
import model.time.Time;
import model.worldCreation.Nation;

public class NationAuthority extends Authority {
    @Override
    public void taxUpdate(int day, int month, int year) {
        if (day == Time.nationTax) {
            imposeTax();
            paySupporters();
            if(getSupervisor().equals(this)) {
                workWallet.setTaxedOrNot(true); //nation authority can set his own taxation status if he is his own authority
                workWallet.cashOutSalary(day);
            }
            if(areaUnderAuthority instanceof Nation nation && month == 6 && !nation.isAtWar() && nation.getWarsFought() > 0){
                nation.sendWalletBalanceToLeaders();
                System.out.println("balance sent");
            }
        }




    }
    public NationAuthority(Character character) {
        super(character);
        this.taxDay = Time.nationTax;
    }
}
