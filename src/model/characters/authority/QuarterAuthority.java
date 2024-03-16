package model.characters.authority;

import model.Settings;
import model.characters.Character;
import model.characters.Peasant;
import model.characters.npc.Farmer;
import model.characters.npc.Merchant;
import model.characters.npc.Miner;
import model.stateSystem.EventTracker;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.WorkWallet;
import model.time.Time;

import java.util.LinkedList;

public class QuarterAuthority extends Authority {

    @Override
    public void taxUpdate(int day, int month, int year) {
        if (day == Time.quarterTax) {
            imposeTax();
            paySupporters();
        }

    }

    private LinkedList<Peasant> peasants;
    private Tax taxFormFarmers;
    private Tax taxFormMiners;
    private Tax taxFormMerchants;

    public QuarterAuthority(Character character) {
        super(character);
        this.peasants = new LinkedList<>();
        this.taxFormFarmers = new Tax();
        this.taxFormMiners = new Tax();
        this.taxFormMerchants = new Tax();
        taxFormFarmers.setTaxInfo(Resource.Food, Settings.getInt("peasantFoodTax"));
        taxFormMiners.setTaxInfo(Resource.Alloy, Settings.getInt("peasantAlloyTax"));
        taxFormMerchants.setTaxInfo(Resource.Gold, Settings.getInt("peasantGoldTax"));
        taxForm.setTaxInfo(Resource.Food, Settings.getInt("peasantFoodTax"));
        taxForm.setTaxInfo(Resource.Alloy, Settings.getInt("peasantAlloyTax"));
        taxForm.setTaxInfo(Resource.Gold, Settings.getInt("peasantGoldTax"));
    }

    @Override
    public void imposeTax(){
        for (Peasant peasant : peasants){
            Tax taxForm = peasant instanceof Farmer ? taxFormFarmers
                    : peasant instanceof Miner ? taxFormMiners
                    : peasant instanceof Merchant ? taxFormMerchants
                    : getTaxForm();
            WorkWallet taxedWallet = peasant.getWorkWallet();
            EventTracker tracker = peasant.getEventTracker();
            taxForm.collectTax(taxedWallet,tracker,workWallet,this.getCharacter().getEventTracker());
        }
    }
    public void addPeasant(Peasant peasant){
        peasants.add(peasant);
    }
    public void removePeasant(Peasant peasant){
        peasants.remove(peasant);
    }
    public LinkedList<Peasant> getPeasants() {
        return peasants;
    }
}
