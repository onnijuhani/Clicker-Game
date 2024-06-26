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

    @FXML
    void hideWorkWalletPermanently(MouseEvent event) { // in case the box needs to be hidden or replaced eventually
    }

    @FXML
    public void initialize() {
        try {
            Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateWorkWallet()));
            updateTimeline.setCycleCount(Timeline.INDEFINITE);
            updateTimeline.play();
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    void updateWorkWallet(){
        String values = model.getPlayerCharacter().getPerson().getWorkWallet().toStringValuesRows();
        workWallet.setText(values);

        String rates = model.getPlayerCharacter().getRole().getAuthority().getTaxForm().toStringTaxRates();
        taxRates.setText(rates);
    }

}