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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.characters.payments.PaymentManager;
import model.characters.player.clicker.Clicker;
import model.resourceManagement.TransferPackage;

import java.util.Objects;

public class InformationController extends BaseController {
    @FXML
    private Label fullExpense;

    @FXML
    private Label fullIncome;
    @FXML
    private Label netBalance;

    @FXML
    private ListView<String> paymentList;

    @FXML
    private Label strikes;

    @FXML
    private CheckBox combineUtility;

    @FXML
    private VBox heartsContainer;
    private final Image heartImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/heart.png")));

    @FXML
    private HBox heartsLower;

    @FXML
    private HBox heartsUpper;

    @FXML
    private CheckBox hideClickerSalary;




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
        updateHearts();
        updateNetBalance();
    }

    private void updateNetBalance() {
        netBalance.setText(model.getPlayerPerson().getPaymentManager().getNetBalance().toString());
    }



    private void updateHearts() {

        int currentLives = model.getPlayerPerson().getStrikesTracker().getStrikes();

        heartsUpper.getChildren().clear();
        heartsLower.getChildren().clear();

        for (int i = 0; i < currentLives; i++) {
            ImageView heart = new ImageView(heartImage);
            heart.setFitHeight(25);
            heart.setFitWidth(25);
            if (i < 5) {
                heartsUpper.getChildren().add(heart);
            } else {
                heartsLower.getChildren().add(heart);
            }
        }
    }




    @FXML
    void combineUtilityIncomes(ActionEvent actionEvent) {
        boolean isChecked = combineUtility.isSelected();
        model.getPlayerPerson().getPaymentManager().setCombineUtilities(isChecked );
    }

    @FXML
    void hideClickerSalary(ActionEvent event) {
        boolean isChecked = hideClickerSalary.isSelected();
        Clicker.getInstance().setShowClickerSalaryInPayments(!isChecked);
    }


    void updatePaymentListView() {
        ObservableList<String> paymentItems = FXCollections.observableArrayList();
        for (PaymentManager.PaymentInfo paymentInfo : model.getPlayerPerson().getPaymentManager().getAllPaymentsInOrder()) {
            paymentItems.add(paymentInfo.toString());
        }
        paymentList.setItems(paymentItems);
    }

    void updateFullExpense(){
        TransferPackage amount = model.getPlayerPerson().getPaymentManager().getFullExpense();
        fullExpense.setText(amount.toShortString());
    }

    void updateStrikes(){
        strikes.setText("Strikes left: " + model.getPlayerPerson().getStrikesTracker().getStrikes());
    }

    void updateFullIncome(){
        TransferPackage amount = model.getPlayerPerson().getPaymentManager().getFullIncome();
        fullIncome.setText(amount.toShortString());
    }



}
