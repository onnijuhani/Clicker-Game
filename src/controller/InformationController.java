package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.util.Duration;
import model.characters.payments.PaymentManager;
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


    @FXML
    private CheckBox combineUtility;

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


    @FXML
    void combineUtilityIncomes(ActionEvent actionEvent) {
        boolean isChecked = combineUtility.isSelected();
        model.getPlayerPerson().getPaymentCalendar().setCombineUtilities(isChecked );
    }


    void updatePaymentListView() {
        ObservableList<String> paymentItems = FXCollections.observableArrayList();
        for (PaymentManager.PaymentInfo paymentInfo : model.getPlayerPerson().getPaymentCalendar().getAllPaymentsInOrder()) {
            paymentItems.add(paymentInfo.toString());
        }
        paymentList.setItems(paymentItems);
    }

    void updateFullExpense(){
        TransferPackage amount = model.getPlayerPerson().getPaymentCalendar().getFullExpense();
        fullExpense.setText(amount.toShortString());
    }

    void updateStrikes(){
        strikes.setText("Strikes left: " + model.getPlayerPerson().getStrikesTracker().getStrikes());
    }

    void updateFullIncome(){
        TransferPackage amount = model.getPlayerPerson().getPaymentCalendar().getFullIncome();
        fullIncome.setText(amount.toShortString());
    }



}
