package model.characters.authority;

import model.characters.Character;
import model.resourceManagement.wallets.WorkWallet;
import model.stateSystem.MessageTracker;
import model.time.Time;
import model.worldCreation.Nation;

public class NationAuthority extends Authority {
    @Override
    public void taxUpdate(int day, int month, int year) {
        if (day == Time.nationTax) {
            imposeTax();
            taxVassalKing();
            paySupporters();
            if(getSupervisor().equals(this)) {
                workWallet.setTaxedOrNot(true); //nation authority can set his own taxation status if he is his own authority
                workWallet.cashOutSalary(day);
            }
            if(areaUnderAuthority instanceof Nation nation && month == 6 && !nation.isAtWar() && nation.getWarsFoughtAmount() > 0){
                nation.sendWalletBalanceToLeaders();
            }
        }




    }
    public NationAuthority(Character character) {
        super(character);
        this.taxDay = Time.nationTax;
    }

    private void taxVassalKing(){
        Nation nation  = (Nation) areaUnderAuthority;
        if(nation.getVassals().isEmpty()){
            return;
        }

        for(Nation n : nation.getVassals()){
            WorkWallet walletUnderTaxation = n.getAuthorityHere().getWorkWallet();
            MessageTracker tracker =  n.getAuthorityHere().getCharacterInThisPosition().getMessageTracker();
            taxForm.collectTax(walletUnderTaxation,tracker,workWallet,this.getCharacterInThisPosition().getMessageTracker(), taxDay);
        }
    }


}