package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import time.Time;

public class TopSectionController extends BaseController  {
    @FXML
    private Label alloysLabel;
    @FXML
    private Label foodLabel;
    @FXML
    private Label goldLabel;
    @FXML
    private Button startTimeBtn;
    @FXML
    private Button stopTimeBtn;

    @FXML
    private Label timeView;
    private Timeline updateTimeline;
    public void initialize() {
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> updateTopSection()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        stopTimeBtn.setDisable(true);
        updateTimeline.play();
    }

    public void updateTopSection(){
        updateWallet();
        timeView.setText(Time.getClock());
    }
    void updateWallet(){
        int[] values = model.accessPlayer().getWallet().getWalletValues();
        foodLabel.setText(String.valueOf(values[0]));
        alloysLabel.setText(String.format(String.valueOf(values[1])));
        goldLabel.setText(String.format(String.valueOf(values[2])));
    }
    @FXML
    void startTime(MouseEvent event) {
        model.accessTime().startSimulation();
        updateTimeline.play();

        startTimeBtn.setDisable(true); // Disable the start button
        stopTimeBtn.setDisable(false); // Enable the stop button
    }

    @FXML
    void stopTime(MouseEvent event) {
        model.accessTime().stopSimulation();
        updateTimeline.stop();

        stopTimeBtn.setDisable(true); // Disable the stop button
        startTimeBtn.setDisable(false); // Enable the start button
    }
}
