package controller;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ControllerManager {
    private static final List<Updatable> controllers = new ArrayList<>();

    public static void registerController(Updatable controller) {
        controllers.add(controller);
    }

    private static final AnimationTimer animationTimer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            updateControllers();
        }
    };

    public static void startUpdateLoop() {
        try {
            Timeline updateTimeline = new Timeline(new KeyFrame(Duration.millis(250),e -> updateControllers()));
            updateTimeline.setCycleCount(Timeline.INDEFINITE);
            updateTimeline.play();
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    public static void start() {
        animationTimer.start();
    }

    public void stop() {
        animationTimer.stop();
    }



    public static void updateControllers() {

        for (Updatable controller : controllers) {
            Platform.runLater(controller::update);
        }
    }
}
