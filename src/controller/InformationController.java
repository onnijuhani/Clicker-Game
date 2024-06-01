package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;
import model.characters.payments.PaymentCalendar;
import model.resourceManagement.TransferPackage;

public class InformationController extends BaseController {
    @FXML
    private Label fullExpense;

    @FXML
    private Label fullIncome;

    @FXML
    private ListView<String> paymentList;

    @FXML
    private Label strikes;

    public void initialize() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> updateInfoTab()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }

    public void updateInfoTab() {
        updateFullExpense();
        updateFullIncome();
        updatePaymentListView();
        updateStrikes();
    }


    void updatePaymentListView() {
        ObservableList<String> paymentItems = FXCollections.observableArrayList();
        for (PaymentCalendar.PaymentInfo paymentInfo : model.getPlayerPerson().getPaymentCalendar().getAllPaymentsInOrder()) {
            paymentItems.add(paymentInfo.toString());
        }
        paymentList.setItems(paymentItems);
    }

    void updateFullExpense(){
        TransferPackage amount = model.getPlayerPerson().getPaymentCalendar().getFullExpense();
        fullExpense.setText(amount.toString());
    }

    void updateStrikes(){
        strikes.setText("Strikes left: " + model.getPlayerPerson().getStrikesTracker().getStrikes());
    }

    void updateFullIncome(){
        TransferPackage amount = model.getPlayerPerson().getPaymentCalendar().getFullIncome();
        fullIncome.setText(amount.toString());
    }


}
