package model.characters.authority;

import model.Settings;
import model.characters.Character;
import model.characters.Peasant;
import model.resourceManagement.Resource;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.wallets.WorkWallet;
import model.stateSystem.MessageTracker;
import model.time.Time;

import java.util.LinkedList;

public class QuarterAuthority extends Authority {

    @Override
    public void taxUpdate(int day, int month, int year) {
        if (day == Time.quarterTax) {
            imposeTax();
            paySupporters();
        }
        if (getCharacterInThisPosition().getPerson().isPlayer()){
            return;
        }

    }

    private final LinkedList<Peasant> peasants;

    public QuarterAuthority(Character character) {
        super(character);
        this.peasants = new LinkedList<>();
        taxForm.setTaxInfo(Resource.Food, Settings.getInt("peasantFoodTax"));
        taxForm.setTaxInfo(Resource.Alloy, Settings.getInt("peasantAlloyTax"));
        taxForm.setTaxInfo(Resource.Gold, Settings.getInt("peasantGoldTax"));
        this.taxDay = Time.quarterTax;
    }

    @Override
    public void imposeTax(){
        for (Peasant peasant : peasants){
            Tax taxForm = getTaxForm();
            WorkWallet taxedWallet = peasant.getPerson().getWorkWallet();
            MessageTracker tracker = peasant.getMessageTracker();
            taxForm.collectTax(taxedWallet,tracker,workWallet,this.getCharacterInThisPosition().getMessageTracker(), taxDay);
        }
    }
    public void addPeasant(Peasant peasant){
        peasants.add(peasant);
    }
    public void removePeasant(Character character){
        Peasant peasant = (Peasant) character;
        peasants.remove(peasant);
    }
    public LinkedList<Peasant> getPeasants() {
        return peasants;
    }
}
