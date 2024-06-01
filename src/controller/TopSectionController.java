package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import model.stateSystem.EventTracker;
import model.time.Speed;
import model.time.Time;


public class TopSectionController extends BaseController {
    @FXML
    private Label alloysLabel;
    @FXML
    private Label foodLabel;
    @FXML
    private Label goldLabel;
    @FXML
    protected Button startTimeBtn;
    @FXML
    protected Button stopTimeBtn;

    @FXML
    private Label timeView;

    @FXML
    private Label speedLabel;
    @FXML
    private Button fasterBtn;
    @FXML
    private Button slowerBtn;
    private MainController main;

    public void initialize() {
        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.035), e -> updateTopSection()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        stopTimeBtn.setDisable(true);
        updateTimeline.play();
    }

    public void updateSpeed() {
        speedLabel.setText(Time.getSpeed().toString());
        fasterBtn.setDisable(Time.getSpeed().equals(Speed.Fast));
        slowerBtn.setDisable(Time.getSpeed().equals(Speed.Slow));
    }

    public void updateTopSection() {
        updateWallet();
        timeView.setText(Time.getClock());
        updateSpeed();
    }

    void updateWallet() {
        int[] values = model.getPlayerCharacter().getPerson().getWallet().getWalletValues();
        foodLabel.setText(String.valueOf(values[0]));
        alloysLabel.setText(String.format(String.valueOf(values[1])));
        goldLabel.setText(String.format(String.valueOf(values[2])));
    }

    @FXML
    void startTime(MouseEvent event) {
        main.incrementClicker.setDisable(true);
        main.incrementClicker.setSelected(false);
        Time.setManualSimulation(false);

        Time.startSimulation();

        startTimeBtn.setDisable(true); // Disable the start button
        stopTimeBtn.setDisable(false); // Enable the stop button
    }

    @FXML
    void stopTime(MouseEvent event) {
        main.incrementClicker.setDisable(false);
        main.incrementClicker.setSelected(false);
        Time.setManualSimulation(false);


        Time.stopSimulation();

        stopTimeBtn.setDisable(true); // Disable the stop button
        startTimeBtn.setDisable(false); // Enable the start button
    }

    @FXML
    void faster(MouseEvent event) {
        model.accessTime().updateSpeed(Speed.Fast);
        fasterBtn.setDisable(Time.getSpeed().equals(Speed.Fast));
        slowerBtn.setDisable(false);
        main.clickMeButton.setDisable(false);
        main.updatePauseBtnText();
    }


    @FXML
    void slower(MouseEvent event) {
        model.accessTime().updateSpeed(Speed.Slow);
        slowerBtn.setDisable(Time.getSpeed().equals(Speed.Slow));
        fasterBtn.setDisable(false);

        main.clickMeButton.setDisable(Time.getSpeed().equals(Speed.Slow));
        if (Time.getSpeed().equals(Speed.Slow)){
            model.getPlayerCharacter().getEventTracker().addEvent(EventTracker.Message("Major","Clicker is disabled"));
        }
        main.updatePauseBtnText();
    }
    public void setMain(MainController main) {
        this.main = main;
    }
}
