import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.Stage;



public class UI extends Application {

    Wallet playerWallet = new Wallet(0, 0, 0);
    Shack playerHome = new Shack("Your own");
    Label home;
    Label walletLabel;
    Label totalClicks;
    private int clickCount = 0;
    private ObservableList<String> resourceMessage = FXCollections.observableArrayList();

    private Stage primaryStage;
    private Scene mainScene;

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        primaryStage.setTitle("Outside");

        walletLabel = new Label("Wallet: " + playerWallet.toString());
        walletLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12pt"); //-fx-font-style: italic
        updateWalletLabel();

        // Header
        HBox header = new HBox();
        header.getChildren().add(walletLabel);
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: lightblue;");

        ListView<String> resourceListView = new ListView<>(resourceMessage);
        resourceListView.setPrefHeight(150); // Set the preferred height of the ListView
        resourceListView.getItems().addAll("Moi");

        VBox rightBox = new VBox();
        totalClicks = new Label("Click count: "+ clickCount);
        updateTotalClicks();
        home = new Label(playerHome.toString()+"  "+playerHome.getMaintenanceCost());
        rightBox.getChildren().add(totalClicks);
        rightBox.getChildren().add(home);
        rightBox.setPrefHeight(150);



        // Left Sidebar
        VBox leftSidebar = new VBox();
        leftSidebar.getChildren().add(new Button("Left Sidebar Button 1"));
        leftSidebar.getChildren().add(new Button("Left Sidebar Button 2"));
        leftSidebar.setStyle("-fx-background-color: lightgreen;");

        // Center Content
        GridPane centerContent = new GridPane();
        centerContent.add(new Button("Center Button 1"), 4, 0);
        centerContent.add(new Button("Center Button 2"), 6, 0);
        Button shopButton = new Button("Shop");
        centerContent.add(shopButton, 8, 0, 2, 1);
        shopButton.setOnAction(e -> openShop());
        centerContent.setStyle("-fx-background-color: lightcoral;");

        // Right Sidebar
        VBox rightSidebar = new VBox();
        rightSidebar.getChildren().add(new Button("Right Sidebar Button 1"));
        rightSidebar.getChildren().add(new Button("Right Sidebar Button 2"));
        rightSidebar.setStyle("-fx-background-color: lightpink;");

        // Footer
        HBox footer = new HBox();
        footer.getChildren().add(new Label("Footer"));
        footer.setAlignment(Pos.CENTER);
        footer.setStyle("-fx-background-color: lightsalmon;");




        // StackPane for overlaying a centered button
        StackPane clickMeArea = new StackPane();
        Button generateBtn = new Button("Click me!");
        clickMeArea.getChildren().add(generateBtn);
        clickMeArea.setStyle("-fx-background-color: lightyellow;");
        generateBtn.setOnAction(e -> {
            handleButtonClick();
            updateWalletLabel();
            updateTotalClicks();
            resourceListView.scrollTo(this.resourceMessage.size() - 1);
        });


        ListView<Button> buttonListView = new ListView<>();
        ObservableList<Button> buttonList = FXCollections.observableArrayList();
        int numberOfButtons = 50;
        for (int i = 0; i < numberOfButtons; i++) {
            Button button = new Button("Button " + (i + 1));
            buttonList.add(button);
        }
        buttonListView.setItems(buttonList);
        VBox areas = new VBox(buttonListView);

        // AnchorPane for precise control over the position of a label
        AnchorPane bottomArea = new AnchorPane();
        Label anchorLabel = new Label("Anchored Label");
        bottomArea.getChildren().add(areas);
        bottomArea.setStyle("-fx-background-color: lightblue;");

        // BorderPane as the main layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(header);
        borderPane.setLeft(leftSidebar);
        borderPane.setCenter(centerContent);
        borderPane.setRight(rightSidebar);
        borderPane.setBottom(footer);

        // Create a VBox to hold all layout panes vertically
        VBox layoutsBox = new VBox(5);
        layoutsBox.setAlignment(Pos.TOP_CENTER);

        HBox middleArea = new HBox(10);
        middleArea.getChildren().addAll(resourceListView, rightBox);

        layoutsBox.getChildren().addAll(borderPane, middleArea, clickMeArea, bottomArea);

        // Create a scene and set it to the stage
        mainScene = new Scene(layoutsBox, 800, 600);
        primaryStage.setScene(mainScene);

        // Show the stage
        primaryStage.show();
    }

    private void openShop() {
        StackPane shopViewRoot = new StackPane();
        Button backButton = new Button("Back to Main View");
        backButton.setOnAction(e -> backToMainView());
        shopViewRoot.getChildren().add(backButton);

        Scene shopViewScene = new Scene(shopViewRoot, 800, 600);

        primaryStage.setScene(shopViewScene);
        primaryStage.setTitle("Shop");
    }

    private void backToMainView() {
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Outside");
    }

    private void updateWalletLabel() {
        walletLabel.setText("Wallet: " + playerWallet.toString());
    }
    private void updateTotalClicks() {
        totalClicks.setText("Click count: " + clickCount);
    }


    private void handleButtonClick() {
        clickCount++;
        if (clickCount % 100 == 0) {
            playerWallet.addResources(10, 0, 0);
            addMessage("10 Resources Added!");

        } else {
            playerWallet.addResources(1, 0, 0);
            addMessage("1 Resource Added!");
        }
    }

    private void addMessage(String resourceMessage) {
        this.resourceMessage.add(resourceMessage);
        // if (this.resourceMessage.size() > limit) {
        //    this.resourceMessage.remove(0, this.resourceMessage.size() - limit);
        // }
    }



    public static void main(String[] args) {
        launch(args);
    }



}





