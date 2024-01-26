package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Settings;
import model.characters.player.clicker.*;
import model.resourceManagement.Resource;

public class ClickerShopController extends BaseController {
    @FXML private Button buyAlloyClickerButton;
    @FXML private Button buyGoldClickerButton;
    @FXML private Button buyFoodClickerButton;


    @FXML
    private Button alloyUpgradeBtn;
    @FXML
    private Button foodUpgradeBtn;
    @FXML
    private Button goldUpgradeBtn;

    @FXML
    private VBox foodBox;
    @FXML
    private VBox alloyBox;
    @FXML
    private VBox goldBox;


    @FXML
    private Label foodInfo;
    @FXML
    private Label foodLevel;
    @FXML
    private Label alloyInfo;
    @FXML
    private Label alloyLevel;
    @FXML
    private Label goldInfo;
    @FXML
    private Label goldLevel;



    public ClickerShopController() {
    }

    @FXML
    public void initialize() {
    }

    void setInfoTexts(){
        Clicker clicker = model.accessPlayer().getClicker();
        FoodClicker foodClicker = (FoodClicker) clicker.getClickerTool(Resource.Food);
        foodLevel.setText("Level: "+foodClicker.getUpgradeLevel());
        foodInfo.setText("Produces: " + foodClicker.getResourceAmount() + " Food");

        if(clicker.getClickerTool(Resource.Alloy) != null) {
            AlloyClicker alloyClicker = (AlloyClicker) clicker.getClickerTool(Resource.Alloy);
            alloyLevel.setText("Level: " + alloyClicker.getUpgradeLevel());
            alloyInfo.setText("Produces: " + alloyClicker.getResourceAmount() + " Alloys");
        }
        if(clicker.getClickerTool(Resource.Gold) != null) {
            GoldClicker goldClicker = (GoldClicker) clicker.getClickerTool(Resource.Gold);
            goldLevel.setText("Level: "+goldClicker.getUpgradeLevel());
            goldInfo.setText("Produces: " + goldClicker.getResourceAmount() + " Gold");
        }


    }


    protected void updateClickerShopPrices() {
        updateClickerPrice(Resource.Alloy, buyAlloyClickerButton, alloyUpgradeBtn);
        updateClickerPrice(Resource.Gold, buyGoldClickerButton, goldUpgradeBtn);
        updateClickerPrice(Resource.Food, buyFoodClickerButton, foodUpgradeBtn);
        setInfoTexts();
    }

    private void updateClickerPrice(Resource resource, Button buyButton, Button upgradeButton) {
        int basePrice = Settings.get(resource.name().toLowerCase() + "Clicker");
        buyButton.setText(basePrice + " Gold");

        ClickerTools tool = model.accessPlayer().getClicker().getClickerTool(resource);
        if (tool != null) {
            int upgradePrice = tool.getUpgradePrice();
            upgradeButton.setText(upgradePrice + " Gold");
        }
    }

    @FXML
    void buyAlloyClicker() {
        boolean purchaseSuccessful = model.accessShop().getClickerShop().buyClicker(Resource.Alloy, model.accessPlayer());
        buyAlloyClickerButton.setVisible(!purchaseSuccessful);
        alloyBox.setVisible(purchaseSuccessful);
        updateClickerShopPrices();
    }

    @FXML
    void buyGoldClicker() {
        boolean purchaseSuccessful = model.accessShop().getClickerShop().buyClicker(Resource.Gold, model.accessPlayer());
        buyGoldClickerButton.setVisible(!purchaseSuccessful);
        goldBox.setVisible(purchaseSuccessful);
        updateClickerShopPrices();
    }



    @FXML
    void upgradeAlloyClicker() {
        handleUpgradeClicker(Resource.Alloy, alloyUpgradeBtn);
    }

    @FXML
    void upgradeFoodClicker() {
        handleUpgradeClicker(Resource.Food, foodUpgradeBtn);
    }

    @FXML
    void upgradeGoldClicker() {
        handleUpgradeClicker(Resource.Gold, goldUpgradeBtn);
    }

    private void handleUpgradeClicker(Resource resource, Button upgradeButton) {
        Clicker clicker = model.accessPlayer().getClicker();
        ClickerTools tool = clicker.getClickerTool(resource);
        if (tool != null) {
            boolean upgradeSuccessful = model.accessShop().getClickerShop().buyUpgrade(resource, model.accessPlayer());
            if (upgradeSuccessful) {
                upgradeButton.setText("Upgrade to Level " + (tool.getUpgradeLevel() + 1) + " (" + tool.getUpgradePrice() + " Gold)");
            }
        }
        updateClickerShopPrices();
    }
}
