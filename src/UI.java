import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UI extends Application {

    Wallet playerWallet = new Wallet(0, 0, 0);
    Label walletLabel; // Declare the Label variable

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Your JavaFX App");

        Button btn = new Button("Click me!");
        playerWallet.addResources(1, 0, 0);
        btn.setOnAction(e -> {
            playerWallet.addResources(1, 0, 0);
            updateWalletLabel(); // Update the label when the button is clicked
        });

        walletLabel = new Label("Wallet Information: " + playerWallet.toString()); // Initialize the Label
        updateWalletLabel(); // Initial update of the label

        VBox vBox = new VBox(10); // Vertical spacing of 10 pixels
        vBox.setAlignment(Pos.CENTER); // Center align the elements
        vBox.getChildren().addAll(btn, walletLabel); // Add the button and label to the VBox

        primaryStage.setScene(new Scene(vBox, 300, 250));
        primaryStage.show();
    }

    private void updateWalletLabel() {
        walletLabel.setText("Wallet Information: " + playerWallet.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}


