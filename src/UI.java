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
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

interface ShopViewCallback {
    void onBackToMainView();
    void openExchange();
}

public class UI extends Application implements ShopViewCallback {

    @Override
    public void onBackToMainView() {
        backToMainView();
    }

    Label playerHome;
    Label totalClicks;
    private Label walletLabel;

    private ObservableList<String> resourceMessage = FXCollections.observableArrayList();

    private Stage primaryStage;

    private Controller control;
    private Label square1Label;

    private Button higherAreaButton;
    private ListView<Button> buttonAreaList;
    private Label higherAreaButtonInfo;
    private Label currentViewAreaInfo;
    private Player player;
    private Scene main;

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
        main = openMainScene();
        primaryStage.setScene(main);
        primaryStage.setTitle("Outside");
        Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
        primaryStage.getIcons().add(icon);


        // Show the stage
        primaryStage.show();
    }

    private Scene openMainScene(){

        ListView<String> resourceListViewBox = getStringListView();
        VBox playerInfoBox = getPlayerInfoBox();
        VBox otherInfoBox = getOtherInfoBox();
        HBox informationSection = getInformationSection(resourceListViewBox, playerInfoBox, otherInfoBox);
        StackPane clickMeSection = getClickMeSection(resourceListViewBox);
        VBox quarterViewContainer = getQuarterViewContainer();
        GridPane areaDetailContainers = getAreaDetailContainers();
        VBox areaViewContainer = getAreaViewContainer();
        HBox botSection = getBotSection(quarterViewContainer, areaDetailContainers,areaViewContainer);


        SplitPane finalSection = getFinalSection(getWalletSection(), getUpperButtonSection(), informationSection, clickMeSection, botSection);
        Scene mainScene = new Scene(finalSection, 1800, 1000);
        return mainScene;

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
        botSection.setStyle("-fx-background-color: lightgray;");
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
        this.playerHome = new Label(playerHome.toString()+"  "+playerHome.getMaintenance().toString());
        playerInfoBox.getChildren().add(totalClicks);
        playerInfoBox.getChildren().add(this.playerHome);
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
        shopButton.setOnAction(e -> openExchange());
        upperButtonSection.setStyle("-fx-background-color: black;");
        return upperButtonSection;
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
    public void openExchange() {
        ExchangeView exchangeView = new ExchangeView(player, control, this);
        VBox shopRoot = exchangeView.initializeShop();
        Scene exchangeViewScene = new Scene(shopRoot, 1800, 1000);
        primaryStage.setScene(exchangeViewScene);
        primaryStage.setTitle("Shop");
    }
    @NotNull
    private HBox getWalletSection() {
        walletLabel = new Label("Wallet: " + player.getWallet().toString());
        walletLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12pt");
        HBox walletSection = new HBox();
        walletSection.getChildren().add(walletLabel);
        walletSection.setAlignment(Pos.CENTER);
        walletSection.setStyle("-fx-background-color: lightgray;");
        return walletSection;
    }
    private void updateWalletLabel() {
        walletLabel.setText("Wallet: " + player.getWallet().toString());
    }

    public void backToMainView() {
        updateWalletLabel();
        primaryStage.setScene(main);
        primaryStage.setTitle("Outside");
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
            TransferPackage basicTransfer = new TransferPackage(100,0,0);
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
class ExchangeView extends VBox {
    private Player player;
    private Controller control;
    private Label shopWalletLabel;
    private ShopViewCallback callback;

    public ExchangeView(Player player, Controller controller, ShopViewCallback callback) {
        this.player = player;
        this.control = controller;
        this.callback = callback;
        initializeShop();
    }



    private void setupBackButton() {
        Button backButton = new Button("Back to Main View");
        backButton.setOnAction(e -> {
            if (callback != null) {
                callback.onBackToMainView();
            }
        });
    }

    public VBox initializeShop() {
        VBox exchangeViewRoot = new VBox();
        exchangeViewRoot.setStyle("-fx-background-image: url('file:C:/Users/onnil/IdeaProjects/Outside/Pictures/BackGround/exchange.png'); " +
                "-fx-background-size: stretch; " +
                "-fx-background-repeat: no-repeat; " +
                "-fx-background-position: center center;");
        Button backButton = new Button("Back to Main View");
        backButton.setOnAction(e -> {
            if (callback != null) {
                callback.onBackToMainView();
            }
        });
        DropShadow shadow = new DropShadow();
        shadow.setRadius(5.0);
        shadow.setOffsetX(3.0);
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.GRAY);
        //exchange
        HBox foodToGoldBox = new HBox(10);
        foodToGoldBox.setStyle("-fx-padding: 10; -fx-border-width: 2; -fx-border-color: black; -fx-background-color: lightGray");
        foodToGoldBox.setEffect(shadow);
        Label foodToGoldLabel = new Label("100 Food to 10 Gold");
        Button foodToGoldBtn = new Button("BuyGold");
        foodToGoldBox.getChildren().addAll(foodToGoldLabel,foodToGoldBtn);
        foodToGoldBtn.setOnAction(e -> {
            control.getModel().accessShop().getExchange().exchangeResources(10, Resource.Gold, Resource.Food, player.getWallet());
            updateShopWalletLabel();
        });
        HBox alloyToGoldBox = new HBox(10);
        alloyToGoldBox.setStyle("-fx-padding: 10; -fx-border-width: 2; -fx-border-color: black; -fx-background-color: lightGray");
        alloyToGoldBox.setEffect(shadow);
        Label alloyToGoldLabel = new Label("50 Alloy to 10 Gold");
        Button alloyToGoldBtn = new Button("BuyGold");
        alloyToGoldBox.getChildren().addAll(alloyToGoldLabel,alloyToGoldBtn);
        alloyToGoldBtn.setOnAction(e -> {
            control.getModel().accessShop().getExchange().exchangeResources(10, Resource.Gold, Resource.Alloy, player.getWallet());
            updateShopWalletLabel();
        });
        HBox goldToAlloyBox = new HBox(10);
        goldToAlloyBox.setStyle("-fx-padding: 10; -fx-border-width: 2; -fx-border-color: black; -fx-background-color: lightGray");
        goldToAlloyBox.setEffect(shadow);
        Label goldToAlloyLabel = new Label("10 gold to 50 Alloys");
        Button GoldToAlloysBtn = new Button("BuyAlloys");
        goldToAlloyBox.getChildren().addAll(goldToAlloyLabel,GoldToAlloysBtn);
        GoldToAlloysBtn.setOnAction(e -> {
            control.getModel().accessShop().getExchange().exchangeResources(50, Resource.Alloy, Resource.Gold, player.getWallet());
            updateShopWalletLabel();
        });
        HBox goldToFoodBox = new HBox(10);
        goldToFoodBox.setStyle("-fx-padding: 10; -fx-border-width: 2; -fx-border-color: black; -fx-background-color: lightGray");
        goldToFoodBox.setEffect(shadow);
        Label goldToFoodLabel = new Label("10 gold to 100 food");
        Button goldToFoodBtn = new Button("BuyFood");
        goldToFoodBox.getChildren().addAll(goldToFoodLabel,goldToFoodBtn);
        goldToFoodBtn.setOnAction(e -> {
            control.getModel().accessShop().getExchange().exchangeResources(100, Resource.Food, Resource.Gold, player.getWallet());
            updateShopWalletLabel();
        });
        HBox exchangeBox = new HBox(10);
        exchangeBox.setAlignment(Pos.CENTER);
        exchangeBox.getChildren().addAll(foodToGoldBox,alloyToGoldBox,goldToAlloyBox,goldToFoodBox);

        HBox exchangeLabelBox = new HBox();
        exchangeLabelBox.setAlignment(Pos.CENTER);
        Label exchangeLabel = new Label("Resources Exchange:");
        exchangeLabelBox.getChildren().add(exchangeLabel);

        exchangeLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12pt;-fx-text-fill: white");


        exchangeViewRoot.getChildren().addAll(getShopWalletSection(),backButton,exchangeLabelBox,exchangeBox );
        return exchangeViewRoot;
    }
    private void updateShopWalletLabel() {
        shopWalletLabel.setText("Wallet: " + player.getWallet().toString());
    }
    private HBox getShopWalletSection() {
        shopWalletLabel = new Label("Wallet: " + player.getWallet().toString());
        shopWalletLabel.setStyle("-fx-font-weight: bold;-fx-font-size: 12pt");
        HBox walletSection = new HBox();
        walletSection.getChildren().add(shopWalletLabel);
        walletSection.setAlignment(Pos.CENTER);
        walletSection.setStyle("-fx-background-color: lightgray;");
        return walletSection;
    }
}





