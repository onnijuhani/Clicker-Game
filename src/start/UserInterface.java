package start;

import controller.ControllerManager;
import controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.Model;

import java.util.Objects;

public class UserInterface extends Application {
    public void start(Stage primaryStage) {
        try {
            Model model = new Model();

            // Load main interface
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/view/interface.fxml"));
            Parent mainRoot = mainLoader.load();
            MainController mainController = mainLoader.getController();
            mainController.setModel(model);

            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/pics/icon.png")));
            primaryStage.getIcons().add(icon);
            primaryStage.setTitle("Territorial Clickers");

            Scene scene = new Scene(mainRoot);
            String css = Objects.requireNonNull(this.getClass().getResource("/characterStyle.css")).toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setScene(scene);

            primaryStage.setResizable(false);

//            URL resource = getClass().getResource("/test.mp3");
//            String musicFile = resource.toString();
//            Media sound = new Media(musicFile);
//            MediaPlayer mediaPlayer = new MediaPlayer(sound);
//            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely
//            mediaPlayer.play();
//            mediaPlayer.setMute(true);


            // Key event ENTER  for CLICKER
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    mainController.generateResourcesAction();
                    event.consume();
                }
            });

            // Key event SPACE to stop/start time
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    mainController.toggleSimulation();
                    event.consume();
                }
            });

            ControllerManager.startUpdateLoop();

            // makes sure game stops running when window is closed
            primaryStage.setOnCloseRequest(event -> Platform.exit());

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}