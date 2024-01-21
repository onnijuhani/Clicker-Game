package controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import model.characters.player.EventTracker;

import java.util.List;
public class EventLogController extends BaseController {
    @FXML
    private ListView<String> eventList;
    private EventTracker eventTracker;

    public EventLogController() {
    }

    @FXML
    public void initialize() {
        updateEventList();
        this.eventTracker = model.accessPlayer().getEventTracker();
    }

    public void updateEventList() {
        List<String> events = model.accessPlayer().getEventTracker().getCombinedEvents();
        eventList.getItems().clear();
        for (String event : events) {
            eventList.getItems().add(event);
        }
        eventList.scrollTo(eventList.getItems().size() - 1);
    }

}
