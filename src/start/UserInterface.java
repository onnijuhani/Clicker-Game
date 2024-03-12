package start;

import controller.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.Model;

public class UserInterface extends Application {
    public void start(Stage primaryStage) {
        try {
            Model model = new Model();

            // Load main interface
            FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/view/interface.fxml"));
            Parent mainRoot = mainLoader.load();
            MainController mainController = mainLoader.getController();
            mainController.setModel(model);

            Image icon = new Image(getClass().getResourceAsStream("/pics/icon.png"));
            primaryStage.getIcons().add(icon);


            Scene scene = new Scene(mainRoot);
            String css = this.getClass().getResource("/characterStyle.css").toExternalForm();
            scene.getStylesheets().add(css);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1600);
            primaryStage.setMinWidth(1600);
            primaryStage.setMinHeight(920);
            primaryStage.setMaxHeight(920);
            primaryStage.setResizable(false);

            // make sure game stops running when window is closed
            primaryStage.setOnCloseRequest(event -> Platform.exit());

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}