package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class WorkWalletController extends BaseController {

    @FXML
    private Label workWallet;
    @FXML
    private Label taxRates;

    @FXML
    private AnchorPane workWalletWindow;

    @FXML
    void hideWorkWalletPermanently(MouseEvent event) { // in case the box needs to be hidden or replaced eventually
    }


    @Override
    public void update(){
        String values = model.getPlayerCharacter().getPerson().getWorkWallet().toStringValuesRows();
        workWallet.setText(values);

        String rates = model.getPlayerCharacter().getRole().getAuthority().getTaxForm().toStringTaxRates();
        taxRates.setText(rates);
    }

}