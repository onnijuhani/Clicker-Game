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

            workWallet.setTaxedOrNot(true);
            workWallet.cashOutSalary(day);

            if(areaUnderAuthority instanceof Nation nation && month == 6 && !nation.isAtWar() && nation.getWarsFoughtAmount() > 0){
                nation.sendWalletBalanceToLeaders();
            }
        }
    }


    public NationAuthority(Character character) {
        super(character);
        this.taxDay = Time.nationTax;
    }


}
