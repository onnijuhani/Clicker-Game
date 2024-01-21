package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class TopSectionController extends BaseController  {
    @FXML
    private Label alloysLabel;
    @FXML
    private Label foodLabel;
    @FXML
    private Label goldLabel;
    @FXML
    private Button stopTime;
    @FXML
    private Label timeView;
    private Timeline updateTimeline;
    public void initialize() {
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateTopSection()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void updateTopSection(){
        updateWallet();
        timeView.setText(model.accessTime().getClock());
    }
    void updateWallet(){
        double[] values = model.accessPlayer().getWallet().getWalletValues();
        foodLabel.setText(String.format("%.2f", values[0]));
        alloysLabel.setText(String.format("%.2f", values[1]));
        goldLabel.setText(String.format("%.2f", values[2]));
    }
    @FXML
    void startTime(MouseEvent event) {
        model.accessTime().startSimulation();
        updateTimeline.play();
    }
    @FXML
    void stopTime(MouseEvent event) {
        model.accessTime().stopSimulation();
        updateTimeline.stop();
    }
}
