package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;
import model.characters.Payment;
import model.characters.PaymentCalendar;
import model.resourceManagement.TransferPackage;

public class InformationController extends BaseController {
    @FXML
    private Label maintenanceCost;


    public void initialize() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateInfoTab()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    private void updateInfoTab() {
        updateMaintenanceCost();
    }


    void updateMaintenanceCost(){
        PaymentCalendar.PaymentInfo info = model.getPlayerPerson().getPaymentCalendar().getExpense(Payment.MAINTENANCE_EXPENSE);
        TransferPackage amount = info.amount;

        maintenanceCost.setText(amount.toString());

    }


}
