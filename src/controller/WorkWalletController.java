package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.characters.authority.Authority;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.wallets.WorkWallet;

public class WorkWalletController extends BaseController {

    @FXML
    private Label workWallet;
    @FXML
    private Label taxRates;

    private WorkWallet playerWorkWallet;

    @Override
    public void update(){
        if(playerWorkWallet == null){
            playerWorkWallet = model.getPlayerCharacter().getPerson().getWorkWallet();
        }
        String values = playerWorkWallet.toStringValuesRows();

        if(workWallet != null) {
            workWallet.setText(values);
        }


        Authority authority = model.getPlayerCharacter().getRole().getAuthority();
        if(authority == null) return;
        Tax taxForm = authority.getTaxForm();
        if(taxForm == null) return;

        String rates = taxForm.toStringTaxRates();
        if(taxRates != null) {
            taxRates.setText(rates);
        }
    }

}