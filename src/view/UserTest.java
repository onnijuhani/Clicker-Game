package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
public class UserTest extends Application {
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/lol.fxml"));
        Image icon = new Image(getClass().getResourceAsStream("/pics/icon.png"));
        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("Outside-In");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}




