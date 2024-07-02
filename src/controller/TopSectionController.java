package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import model.stateSystem.EventTracker;
import model.time.Speed;
import model.time.Time;

import static model.Settings.formatNumber;


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

    @Override
    public void initialize() {
        try {
            super.initialize();
            stopTimeBtn.setDisable(true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSpeed() {
        speedLabel.setText(Time.getSpeed().toString());
        fasterBtn.setDisable(Time.getSpeed().equals(Speed.Fast));
        slowerBtn.setDisable(Time.getSpeed().equals(Speed.Slow));
    }
    @Override
    public void update() {
        updateWallet();
        timeView.setText(Time.getClock());
        updateSpeed();
    }

    void updateWallet() {
        int[] values = model.getPlayerCharacter().getPerson().getWallet().getWalletValues();
        foodLabel.setText(formatNumber(values[0]));
        alloysLabel.setText(formatNumber(values[1]));
        goldLabel.setText(formatNumber(values[2]));
    }



    @FXML
    void startTime(MouseEvent event) {
        startTimeFunction();

    }

    public void startTimeFunction() {
        main.incrementClicker.setDisable(true);
        main.incrementClicker.setSelected(false);
        Time.setManualSimulation(false);

        Time.startSimulation();

        startTimeBtn.setDisable(true); // Disable the start button
        stopTimeBtn.setDisable(false); // Enable the stop button
    }

    @FXML
    void stopTime(MouseEvent event) {
        stopTimeFunction();
    }

    public void stopTimeFunction() {
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
