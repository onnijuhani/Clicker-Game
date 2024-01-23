package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class WorkWalletController extends BaseController {

    @FXML
    private Label workWallet;
    @FXML
    private Label taxRates;

    @FXML
    private AnchorPane workWalletWindow;
    private Timeline updateTimeline;

    @FXML
    void hideWorkWalletPermanently(MouseEvent event) {
    }

    @FXML
    public void initialize() {
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> updateWorkWallet()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    void updateWorkWallet(){
        String values = model.accessPlayer().getWorkWallet().toStringValuesRows();
        workWallet.setText(values);

        String rates = model.accessPlayer().getSupervisor().getTaxForm().toStringTaxRates();
        taxRates.setText(rates);
    }

}