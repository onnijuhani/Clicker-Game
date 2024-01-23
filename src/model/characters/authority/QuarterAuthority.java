package model.characters.authority;

import model.characters.Character;
import model.characters.Peasant;
import model.characters.npc.Farmer;
import model.characters.npc.Merchant;
import model.characters.npc.Miner;
import model.characters.player.EventTracker;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.resources.Resource;
import model.resourceManagement.wallets.WorkWallet;
import time.Time;

import java.util.LinkedList;

public class QuarterAuthority extends Authority {

    @Override
    public void timeUpdate(int day, int month, int year) {
        if (day == Time.quarterTax) {
            System.out.println("quarter toimii");
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
        taxFormFarmers.setTaxInfo(Resource.Food, 50,0);
        taxFormMiners.setTaxInfo(Resource.Alloy, 60, 0);
        taxFormMerchants.setTaxInfo(Resource.Gold, 70,0);
        taxForm.setTaxInfo(Resource.Food, 50,0);
        taxForm.setTaxInfo(Resource.Alloy, 60,0);
        taxForm.setTaxInfo(Resource.Gold, 70,0);
    }

    @Override
    public void imposeTax(){
        for (Peasant peasant : peasants){
            Tax taxForm = peasant instanceof Farmer ? taxFormFarmers
                    : peasant instanceof Miner ? taxFormMiners
                    : peasant instanceof Merchant ? taxFormMerchants
                    : getTaxForm();
            WorkWallet wallet = peasant.getWorkWallet();
            EventTracker tracker = peasant.getEventTracker();
            taxForm.collectTax(wallet,tracker,this.getCharacter().getWallet(),this.getCharacter().getEventTracker());
            peasant.getWorkWallet().setTaxedOrNot(true);
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
