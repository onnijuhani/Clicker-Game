import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class Control {

    public Model model = new Model();

    @FXML
    private ListView<String> eventList;
    @FXML
    private Label foodLabel; // Changed to Label
    @FXML
    private Label alloysLabel; // Changed to Label
    @FXML
    private Label goldLabel; // Changed to Label
    @FXML
    private ImageView homeImageView;

    @FXML
    void generateResources(MouseEvent event) {
        model.accessPlayer().getClicker().generateResources();
        List<String> events = model.accessPlayer().getEventTracker().getEvents();
        String latestEvent = events.getLast();
        eventList.getItems().add(latestEvent);
        eventList.scrollTo(eventList.getItems().size() - 1);
        updateWallet();
        System.out.println(model.accessPlayer().getWallet().getFood().getAmount());
    }
    void updateWallet(){
        double[] values = model.accessPlayer().getWallet().getWalletValues();
        foodLabel.setText(String.valueOf(values[0]));
        alloysLabel.setText(String.valueOf(values[1]));
        goldLabel.setText(String.valueOf(values[2]));
        updateHomeImage();
    }

    public void updateHomeImage() {
        Image newImage = model.accessPlayer().getProperty().getImage();
        homeImageView.setImage(newImage);
    }


}
