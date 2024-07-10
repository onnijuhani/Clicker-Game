package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import model.stateSystem.MessageTracker;
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

            // Additional null check
            if (stopTimeBtn == null) {
                throw new RuntimeException("stopTimeBtn is null");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSpeed() {
        if (Time.getSpeed() != null) {
            speedLabel.setText(Time.getSpeed().toString());
            fasterBtn.setDisable(Time.getSpeed().equals(Speed.Fast));
            slowerBtn.setDisable(Time.getSpeed().equals(Speed.Slow));
        } else {
            throw new RuntimeException("Time or Speed is null");
        }
    }

    @Override
    public void update() {
        updateWallet();
        if (Time.getClock() != null) {
            timeView.setText(Time.getClock());
        } else {
            throw new RuntimeException("Time.getClock() returned null");
        }
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
        if (main != null && main.incrementClicker != null) {
            main.incrementClicker.setDisable(true);
            main.incrementClicker.setSelected(false);
        } else {
            throw new RuntimeException("MainController or incrementClicker is null");
        }

        Time.setManualSimulation(false);
        Time.startSimulation();

        startTimeBtn.setDisable(true);
        stopTimeBtn.setDisable(false);
    }

    @FXML
    void stopTime(MouseEvent event) {
        stopTimeFunction();
    }

    public void stopTimeFunction() {
        if (main != null && main.incrementClicker != null) {
            main.incrementClicker.setDisable(false);
            main.incrementClicker.setSelected(false);
        } else {
            throw new RuntimeException("MainController or incrementClicker is null");
        }

        Time.setManualSimulation(false);
        Time.stopSimulation();

        stopTimeBtn.setDisable(true);
        startTimeBtn.setDisable(false);
    }

    @FXML
    void faster(MouseEvent event) {
        model.accessTime().updateSpeed(Speed.Fast);
        fasterBtn.setDisable(Time.getSpeed().equals(Speed.Fast));
        slowerBtn.setDisable(false);

        if (main != null && main.clickMeButton != null) {
            main.clickMeButton.setDisable(false);
            main.updatePauseBtnText();
        } else {
            throw new RuntimeException("MainController or clickMeButton is null");
        }
    }

    @FXML
    void slower(MouseEvent event) {
        model.accessTime().updateSpeed(Speed.Slow);
        slowerBtn.setDisable(Time.getSpeed().equals(Speed.Slow));
        fasterBtn.setDisable(false);

        if (main != null && main.clickMeButton != null) {
            main.clickMeButton.setDisable(Time.getSpeed().equals(Speed.Slow));
            if (Time.getSpeed().equals(Speed.Slow)) {
                model.getPlayerCharacter().getMessageTracker().addMessage(MessageTracker.Message("Major", "Clicker is disabled"));
            }
            main.updatePauseBtnText();
        } else {
            throw new RuntimeException("MainController or clickMeButton is null");
        }
    }

    public void setMain(MainController main) {
        this.main = main;
    }
}

