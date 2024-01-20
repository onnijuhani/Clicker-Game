package model.characters.authority;

import model.characters.Authority;
import model.characters.Character;
import model.characters.Peasant;
import model.characters.npc.Farmer;
import model.characters.npc.Miner;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.resources.Resource;
import model.resourceManagement.wallets.WorkWallet;

import java.util.LinkedList;

public class QuarterAuthority extends Authority {

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
        taxFormFarmers.setTaxInfo(Resource.Food, 50,20);
        taxFormMiners.setTaxInfo(Resource.Alloy, 50, 20);
        taxFormMerchants.setTaxInfo(Resource.Gold, 50,10);
    }

    @Override
    public void imposeTax(){
        for (Peasant peasant : peasants){
            Tax taxForm = peasant instanceof Farmer ? taxFormFarmers
                    : peasant instanceof Miner ? taxFormMiners
                    : taxFormMerchants;
            WorkWallet wallet = peasant.getWorkWallet();
            taxForm.collectTax(wallet,this.getCharacter().getWallet());
            peasant.getWorkWallet().setTaxedOrNot(true);
        }
    }
    public void addPeasant(Peasant peasant){
        peasants.add(peasant);
    }
    public LinkedList<Peasant> getPeasants() {
        return peasants;
    }
}
