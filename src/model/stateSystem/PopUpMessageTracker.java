package model.stateSystem;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.Settings;

import java.util.List;
import java.util.Random;

public class PopUpMessageTracker {
    private static final ObjectProperty<PopUpMessage> currentMessage = new SimpleObjectProperty<>();
    private static final BooleanProperty gameOver = new SimpleBooleanProperty();

    public static PopUpMessage getMessage() {
        return currentMessage.get();
    }

    public static void resetMessage() {
        currentMessage.set(null);
    }

    public static void sendMessage(PopUpMessage newMessage) {
        Platform.runLater(() -> currentMessage.set(newMessage));
    }

    public static ObjectProperty<PopUpMessage> messageProperty() {
        return currentMessage;
    }

    public static BooleanProperty gameOverProperty() {
        return gameOver;
    }

    public static void sendGameOver() {
        Platform.runLater(() -> gameOver.set(true));
    }


    public static String getRandomButtonText(List<String> texts) {
        Random random = Settings.getRandom();
        return texts.get(random.nextInt(texts.size()));
    }

    public static class PopUpMessage {
        private final String headline;
        private final String mainText;
        private final String imagePath;
        private final String buttonText;

        public PopUpMessage(String headline, String mainText, String imagePath, String buttonText) {
            this.headline = headline;
            this.mainText = mainText;
            this.imagePath = imagePath;
            this.buttonText = buttonText;
        }

        public String getHeadline() {
            return headline;
        }

        public String getMainText() {
            return mainText;
        }

        public String getImagePath() {
            return imagePath;
        }

        public String getButtonText() {
            return buttonText;
        }
    }
}
