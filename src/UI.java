import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class UI extends Application {

    Label home;
    Label walletLabel;
    Label totalClicks;
    private ObservableList<String> resourceMessage = FXCollections.observableArrayList();

    private Stage primaryStage;
    private Scene mainScene;

    private Controller control;
    private Label square1Label;

    private Button higherAreaButton;
    private ListView<Button> buttonAreaList;
    private Label higherAreaButtonInfo;
    private Label currentViewAreaInfo;
    private Player player;



    @Override
    public void start(Stage primaryStage) {



        this.control = new Controller(this);
        this.player = control.getModel().accessPlayer();

        this.higherAreaButton = new Button();
        this.higherAreaButtonInfo = new Label();
        higherAreaButtonInfo.setText(control.getModel().accessCurrentView().getCurrentView().getHigher().getClass().getSimpleName());


        this.buttonAreaList = new ListView<>();
        this.currentViewAreaInfo = new Label();
        if (!control.getModel().accessCurrentView().getCurrentView().getContents().isEmpty()) {
            currentViewAreaInfo.setText(control.getModel().accessCurrentView().getCurrentView().getContents().get(0).getClass().getSimpleName());
        } else {
            currentViewAreaInfo.setText("No contents available");
        }
        currentViewAreaInfo.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");
        updateViewInfo();


        this.primaryStage = primaryStage;
        primaryStage.setTitle("Outside");

        // First box in the upper center, contains information log
        ListView<String> resourceListViewBox = getStringListView();
        // Second box in the upper center, contains basic information
        VBox playerInfoBox = getPlayerInfoBox();
        // Third box in the upper center
        VBox otherInfoBox = getOtherInfoBox();

        
        HBox walletSection = getWalletSection();
        GridPane upperButtonSection = getUpperButtonSection();
        HBox informationSection = getInformationSection(resourceListViewBox, playerInfoBox, otherInfoBox);
        StackPane clickMeSection = getClickMeSection(resourceListViewBox);

        VBox quarterViewContainer = getQuarterViewContainer();
        GridPane areaDetailContainers = getAreaDetailContainers();
        VBox areaViewContainer = getAreaViewContainer();
        HBox botSection = getBotSection(quarterViewContainer, areaDetailContainers,areaViewContainer);


        SplitPane finalSection = getFinalSection(walletSection, upperButtonSection, informationSection, clickMeSection, botSection);

        updateAreaViewContainer();

        // Create a scene and set it to the stage
        mainScene = new Scene(finalSection, 1000, 800);
        primaryStage.setScene(mainScene);
        // Show the stage
        primaryStage.show();
    }

    private VBox getAreaViewContainer() {
        Label listViewTitle = new Label("Explore The Map");
        listViewTitle.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

        HBox higherAreaButtonAndLabel = new HBox(5);
        higherAreaButtonAndLabel.getChildren().addAll(higherAreaButton, higherAreaButtonInfo);

        higherAreaButton.setOnAction(event -> {
            Area areaHigher = control.getModel().accessCurrentView().getHigher();
            if (areaHigher != null) {
                control.getModel().accessCurrentView().updateCurrentView(areaHigher);
                updateViewInfo();
            }
        });

        VBox areaViewListAndLabel = new VBox(5);
        areaViewListAndLabel.getChildren().addAll(currentViewAreaInfo, buttonAreaList);

        ObservableList<Button> areasList = FXCollections.observableArrayList();
        ArrayList<Area> availableAreas = control.getModel().accessCurrentView().getContents();
        for (Area area : availableAreas) {
            Button button = new Button(area.getName());
            button.setOnAction(event -> {
                control.getModel().accessCurrentView().updateCurrentView(area);
                updateViewInfo();
            });
            areasList.add(button);
        }
        buttonAreaList.setItems(areasList);

        return new VBox(listViewTitle, higherAreaButtonAndLabel, areaViewListAndLabel);
    }

    private void updateAreaViewContainer() {
        updateViewInfo();
        ObservableList<Button> areasList = FXCollections.observableArrayList();
        ArrayList<Area> availableAreas = control.getModel().accessCurrentView().getContents();
        for (Area area : availableAreas) {
            Button button = new Button(area.getName());
            button.setOnAction(event -> {
                control.getModel().accessCurrentView().updateCurrentView(area);
                updateViewInfo();
            });
            areasList.add(button);
        }
        buttonAreaList.setItems(areasList);
    }
    private void updateViewInfo() {
        CurrentView currentView = control.getModel().accessCurrentView();
        Area currentAreaView = currentView.getCurrentView();

        // Päivitä higherAreaButton
        higherAreaButton.setText(currentAreaView != null ? currentAreaView.getName() : "Higher Area");

        // Päivitä higherAreaButtonInfo
        higherAreaButtonInfo.setText(currentAreaView != null ? currentAreaView.getClass().getSimpleName() : "No Higher Area");

        // Päivitä currentViewAreaInfo
        if (!currentView.getContents().isEmpty()) {
            currentViewAreaInfo.setText(currentView.getContents().get(0).getClass().getSimpleName());
        } else {
            currentViewAreaInfo.setText("No contents available");
        }

        // Päivitä buttonAreaList
        ObservableList<Button> areasList = FXCollections.observableArrayList();
        ArrayList<Area> availableAreas = currentView.getContents();
        for (Area area : availableAreas) {
            Button button = new Button(area.getName());
            button.setOnAction(event -> {
                control.getModel().accessCurrentView().updateCurrentView(area);
                updateViewInfo();
            });
            areasList.add(button);
        }
        buttonAreaList.setItems(areasList);
    }




    @NotNull
    private static SplitPane getFinalSection(HBox walletSection, GridPane upperButtonSection, HBox informationSection, StackPane clickMeSection, HBox botSection) {
        SplitPane finalSection = new SplitPane();
        finalSection.setOrientation(Orientation.VERTICAL);
        finalSection.getItems().addAll(walletSection, upperButtonSection, informationSection, clickMeSection, botSection);
        finalSection.setDividerPositions(0.05, 0.1, 0.4, 0.45, 0.7);
        return finalSection;
    }

    @NotNull
    private static HBox getBotSection(VBox listViewContainer, GridPane areaDetailContainers, VBox areaViewContainer) {
        HBox botSection = new HBox(10);
        botSection.getChildren().addAll(listViewContainer, areaDetailContainers, areaViewContainer);
        botSection.setStyle("-fx-background-color: lightblue;");
        return botSection;
    }

    @NotNull
    private static VBox getOtherInfoBox() {
        VBox otherInfoBox = new VBox();
        Label temp = new Label("Temporary");
        otherInfoBox.getChildren().add(temp);
        return otherInfoBox;
    }

    @NotNull
    private VBox getPlayerInfoBox() {
        VBox playerInfoBox = new VBox();
        totalClicks = new Label("Click count: "+ control.getModel().accessPlayer().getTotalClicks());
        updateTotalClicks();
        Property playerHome = control.getModel().accessPlayer().getProperty();
        home = new Label(playerHome.toString()+"  "+playerHome.getMaintenance().toString());
        playerInfoBox.getChildren().add(totalClicks);
        playerInfoBox.getChildren().add(home);
        playerInfoBox.setPrefHeight(150);
        playerInfoBox.setStyle("-fx-background-color: white;");


        Image playerHomeImage = playerHome.getImage();
        ImageView playerProperty = new ImageView(playerHomeImage);
        playerProperty.setFitHeight(200);
        playerProperty.setFitWidth(200);
        playerProperty.setPreserveRatio(true);

        playerInfoBox.getChildren().add(playerProperty);

        return playerInfoBox;
    }

    @NotNull
    private ListView<String> getStringListView() {
        ListView<String> resourceListViewBox = new ListView<>(resourceMessage);
        resourceListViewBox.setPrefHeight(150); //
        resourceListViewBox.getItems().addAll("Moi");
        return resourceListViewBox;
    }

    @NotNull
    private static HBox getInformationSection(ListView<String> resourceListViewBox, VBox playerInfoBox, VBox otherInfoBox) {
        HBox informationSection = new HBox(10);
        informationSection.setPrefWidth(150);
        informationSection.getChildren().addAll(resourceListViewBox, playerInfoBox, otherInfoBox);
        return informationSection;
    }

    @NotNull
    private GridPane getUpperButtonSection() {
        GridPane upperButtonSection = new GridPane();
        Button shopButton = new Button("Shop");
        upperButtonSection.add(shopButton, 8, 0, 2, 1);
        shopButton.setOnAction(e -> openShop());
        upperButtonSection.setStyle("-fx-background-color: black;");
        return upperButtonSection;
    }

    @NotNull
    private HBox getWalletSection() {
        walletLabel = new Label("Wallet: " + player.getWallet().toString());
        walletLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12pt"); //-fx-font-style: italic
        updateWalletLabel();

        // Top section with wallet information
        HBox walletSection = new HBox();
        walletSection.getChildren().add(walletLabel);
        walletSection.setAlignment(Pos.CENTER);
        walletSection.setStyle("-fx-background-color: lightblue;");
        return walletSection;
    }

    @NotNull
    private StackPane getClickMeSection(ListView<String> resourceListViewBox) {
        StackPane clickMeSection = new StackPane();
        Button generateBtn = new Button("Click me!");
        clickMeSection.getChildren().add(generateBtn);
        clickMeSection.setStyle("-fx-background-color: black;");
        generateBtn.setOnAction(e -> {
            handleButtonClick();
            updateWalletLabel();
            updateTotalClicks();
            resourceListViewBox.scrollTo(this.resourceMessage.size() - 1);
        });
        return clickMeSection;
    }

    @NotNull
    private GridPane getAreaDetailContainers() {
        Label square1TitleLabel = new Label("Your current location");
        square1TitleLabel.setStyle("-fx-font-weight: bold; -fx-underline: true;");
        square1Label = new Label(currentQuarterInfo());
        VBox square1 = new VBox(square1TitleLabel,square1Label);

        VBox square2 = new VBox(new Label("Square 2 Content"));
        VBox square3 = new VBox(new Label("Square 3 Content"));


        VBox square4 = new VBox(new Label("Square 4 "));

        square1.setStyle("-fx-border-color: black; -fx-padding: 10;");
        square2.setStyle("-fx-border-color: black; -fx-padding: 10;");
        square3.setStyle("-fx-border-color: black; -fx-padding: 10;");
        square4.setStyle("-fx-border-color: black; -fx-padding: 10;");

        GridPane grid = new GridPane();
        grid.add(square1, 0, 0); // Column 0, Row 0
        grid.add(square2, 1, 0); // Column 1, Row 0
        grid.add(square3, 0, 1); // Column 0, Row 1
        grid.add(square4, 1, 1); // Column 1, Row 1

        grid.setHgap(10); // Horizontal gap
        grid.setVgap(10); // Vertical gap
        GridPane.setHgrow(square1, Priority.SOMETIMES); // Grow horizontally
        GridPane.setVgrow(square1, Priority.ALWAYS); // Grow vertically
        return grid;
    }


    private VBox getQuarterViewContainer() {
        Label listViewTitle = new Label("Available regions");
        listViewTitle.setStyle("-fx-font-weight: bold; -fx-padding: 5px;");

        ListView<Button> buttonListBox = new ListView<>();
        ObservableList<Button> quarterList = FXCollections.observableArrayList();
        ArrayList<Quarter> availableQuarters = control.getQuarterList();
        for (Quarter quarter : availableQuarters) {
            Button button = new Button(quarter.getName());
            quarterList.add(button);
            button.setOnAction(event -> {
                control.getModel().accessCurrentPosition().updateCurrentQuarter(quarter);
                updateSquare1Text();
            });
        }
        buttonListBox.setItems(quarterList);

        VBox listViewContainer = new VBox(listViewTitle, buttonListBox);
        return listViewContainer;
    }

    private void openShop() {
        StackPane shopViewRoot = new StackPane();
        Button backButton = new Button("Back to Main View");
        backButton.setOnAction(e -> backToMainView());
        shopViewRoot.getChildren().add(backButton);

        Scene shopViewScene = new Scene(shopViewRoot, 1000, 800);

        primaryStage.setScene(shopViewScene);
        primaryStage.setTitle("Shop");
    }

    private void backToMainView() {
        primaryStage.setScene(mainScene);
        primaryStage.setTitle("Outside");
    }

    private void updateWalletLabel() {
        walletLabel.setText("Wallet: " + player.getWallet().toString());
    }
    private void updateTotalClicks() {
        totalClicks.setText("Click count: " + player.getTotalClicks());
    }


    private void handleButtonClick() {
        player.addClick();
        if (player.getTotalClicks()% 100 == 0) {
            TransferPackage transfer = new TransferPackage(10,0,0);
            player.getWallet().addResources(transfer);
            addMessage("10 Resources Added!");

        } else {
            TransferPackage basicTransfer = new TransferPackage(1,0,0);
            player.getWallet().addResources(basicTransfer);
            addMessage("1 Resource Added!");
        }
    }

    private void addMessage(String resourceMessage) {
        this.resourceMessage.add(resourceMessage);
        // if (this.resourceMessage.size() > limit) {
        //    this.resourceMessage.remove(0, this.resourceMessage.size() - limit);
        // }
    }

    private void updateSquare1Text() {
        square1Label.setText(currentQuarterInfo());
    }

    private String currentQuarterInfo() {
        return control.getModel().accessCurrentPosition().getCurrentQuarter().fullHierarchyInfo();
    }

    public static void main(String[] args) {
        launch(args);
    }

}





