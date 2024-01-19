import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.List;

public class Control {
    private Model model = new Model();
    @FXML
    private ListView<String> eventList;
    @FXML
    private Label foodLabel; // Changed to Label
    @FXML
    private Label alloysLabel; // Changed to Label
    @FXML
    private Label goldLabel; // Changed to Label
    @FXML
    private Label timeView;
    @FXML
    private ImageView homeImageView;
    private Timeline updateTimeline;

    public Control(){
    }
    @FXML
    public void initialize() {
        // Initialize the timeline for updating the UI
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.1), e -> updateUI()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
    }
    private void updateUI() {
        timeView.setText(model.accessTime().getClock());
        updateHomeImage();
        updateWallet();
    }
    @FXML
    void generateResources(MouseEvent event) {
        if (!model.accessTime().isSimulationRunning()) {
            updateEventList("Simulation is paused. Cannot generate resources.");
            return;
        }

        model.accessPlayer().getClicker().generateResources();
        updateEventList(model.accessPlayer().getEventTracker().getEvents().getLast());
        updateWallet();
        System.out.println(TimeEventManager.getObservers().size());
    }
    private void updateEventList(String eventMessage) {
        model.accessPlayer().getEventTracker().addEvent(eventMessage);
        List<String> events = model.accessPlayer().getEventTracker().getEvents();
        String latestEvent = events.get(events.size() - 1);
        eventList.getItems().add(latestEvent);
        eventList.scrollTo(eventList.getItems().size() - 1);
    }

    void updateWallet(){
        double[] values = model.accessPlayer().getWallet().getWalletValues();
        foodLabel.setText(String.valueOf(values[0]));
        alloysLabel.setText(String.valueOf(values[1]));
        goldLabel.setText(String.valueOf(values[2]));
    }

    public void updateHomeImage() {
        Image newImage = model.accessPlayer().getProperty().getImage();
        homeImageView.setImage(newImage);
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
