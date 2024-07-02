package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ControllerManager {
    private static final List<Updatable> controllers = new ArrayList<>();

    public static void registerController(Updatable controller) {
        controllers.add(controller);
    }

    public static void startUpdateLoop() {
        try {
            Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.10),e -> updateControllers()));
            updateTimeline.setCycleCount(Timeline.INDEFINITE);
            updateTimeline.play();
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    public static void updateControllers() {
        for (Updatable controller : controllers) {
            controller.update();
        }
    }
}
