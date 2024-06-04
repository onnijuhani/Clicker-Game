package model.stateSystem;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PopUpMessageTracker {
    private static final StringProperty message = new SimpleStringProperty();
    private static final BooleanProperty gameOver = new SimpleBooleanProperty();

    public static String getMessage() {
        return message.get();
    }

    public static void resetMessage() {
        message.set(null);
    }

    public static void sendMessage(String newMessage) {
        Platform.runLater(() -> message.set(newMessage));
    }

    public static StringProperty messageProperty() {
        return message;
    }

    public static BooleanProperty gameOverProperty() {
        return gameOver;
    }

    public static void sendGameOver(){
        Platform.runLater(() -> gameOver.set(true));
    }
}
