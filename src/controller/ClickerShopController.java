package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Model;
import model.Settings;
import model.characters.player.clicker.*;
import model.resourceManagement.Resource;
import model.shop.ClickerShop;
import model.shop.Shop;

import static model.Settings.formatNumber;

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
    private Shop shop; //shop in the player's nation

    @FXML
    private Button autoClickerBtn;

    @FXML
    private Label alloyOwned;

    @FXML
    private Label goldOwned;

    @FXML
    private Label autoClickerPrice;



    public ClickerShopController() {
    }

    @FXML
    public void initialize() {
        setShop(Model.getPlayerRole().getNation().getShop());
        super.initialize();
    }

    void setInfoTexts(){
        Clicker clicker = Clicker.getInstance();
        FoodClicker foodClicker = (FoodClicker) clicker.getClickerTool(Resource.Food);
        foodLevel.setText("Level: "+foodClicker.getUpgradeLevel());
        foodInfo.setText("Produces: " + foodClicker.getValue() + " Food");

        if(clicker.getClickerTool(Resource.Alloy) != null) {
            AlloyClicker alloyClicker = (AlloyClicker) clicker.getClickerTool(Resource.Alloy);
            alloyLevel.setText("Level: " + alloyClicker.getUpgradeLevel());
            alloyInfo.setText("Produces: " + alloyClicker.getValue() + " Alloys");
        }
        if(clicker.getClickerTool(Resource.Gold) != null) {
            GoldClicker goldClicker = (GoldClicker) clicker.getClickerTool(Resource.Gold);
            goldLevel.setText("Level: "+goldClicker.getUpgradeLevel());
            goldInfo.setText("Produces: " + goldClicker.getValue() + " Gold");
        }

    }


    protected void updateClickerShopPrices() {
        setShop(Model.getPlayerRole().getNation().getShop());
        updateClickerPrice(Resource.Alloy, buyAlloyClickerButton, alloyUpgradeBtn);
        updateClickerPrice(Resource.Gold, buyGoldClickerButton, goldUpgradeBtn);
        updateClickerPrice(Resource.Food, buyFoodClickerButton, foodUpgradeBtn);
        setInfoTexts();
        autoClickerPrice.setText(Clicker.getInstance().getAutoClickerLevel() == 1 ?  " " :ClickerShop.getAutoClickerPrice().toString());
    }

    private void updateClickerPrice(Resource resource, Button buyButton, Button upgradeButton) {
        setShop(Model.getPlayerRole().getNation().getShop());
        int basePrice = Settings.getInt(resource.name().toLowerCase() + "Clicker");
        buyButton.setText(formatNumber(basePrice) + " Gold");

        ClickerTools tool = Clicker.getInstance().getClickerTool(resource);
        if (tool != null) {
            int upgradePrice = tool.getUpgradePrice();
            upgradeButton.setText(formatNumber(upgradePrice) + " Gold");
        }
    }

    @FXML
    void buyAlloyClicker() {
        setShop(Model.getPlayerRole().getNation().getShop());
        boolean purchaseSuccessful = shop.getClickerShop().buyClicker(Resource.Alloy, model.getPlayerPerson());
        buyAlloyClickerButton.setVisible(!purchaseSuccessful);
        alloyUpgradeBtn.setVisible(purchaseSuccessful);
        if (Clicker.getInstance().isAlloyClickerOwned()) {
            alloyOwned.setText("Upgrade For:");
        }
        alloyBox.setVisible(purchaseSuccessful);
        updateClickerShopPrices();
    }

    @FXML
    void buyGoldClicker() {
        setShop(Model.getPlayerRole().getNation().getShop());
        boolean purchaseSuccessful = shop.getClickerShop().buyClicker(Resource.Gold, model.getPlayerPerson());
        buyGoldClickerButton.setVisible(!purchaseSuccessful);
        goldUpgradeBtn.setVisible(purchaseSuccessful);
        if (Clicker.getInstance().isGoldClickerOwned()) {
            goldOwned.setText("Upgrade For:");
        }
        goldBox.setVisible(purchaseSuccessful);
        updateClickerShopPrices();
    }

    @FXML
    void buyAutoClicker() {
        setShop(Model.getPlayerRole().getNation().getShop());
        boolean purchaseSuccessful = shop.getClickerShop().buyAutoClicker(model.getPlayerPerson());
        if(purchaseSuccessful) {
            autoClickerBtn.setText("Upgrade");
        };
        if(Clicker.getInstance().getAutoClickerLevel() == 1) {
            autoClickerBtn.setDisable(purchaseSuccessful);
            autoClickerBtn.setText("Max");
            autoClickerPrice.setText("");
            autoClickerPrice.setVisible(false);
        }
        updateClickerShopPrices();
    }



    @FXML
    void upgradeAlloyClicker() {
        handleUpgradeClicker(Resource.Alloy, alloyUpgradeBtn);
    }

    @FXML
    void upgradeFoodClicker() {
        setShop(Model.getPlayerRole().getNation().getShop());
        handleUpgradeClicker(Resource.Food, foodUpgradeBtn);
    }

    @FXML
    void upgradeGoldClicker() {
        handleUpgradeClicker(Resource.Gold, goldUpgradeBtn);
    }

    private void handleUpgradeClicker(Resource resource, Button upgradeButton) {
        Clicker clicker = Clicker.getInstance();
        ClickerTools tool = clicker.getClickerTool(resource);
        if (tool != null) {
            boolean upgradeSuccessful = shop.getClickerShop().buyUpgrade(resource, model.getPlayerPerson());
            if (upgradeSuccessful) {
                upgradeButton.setText("Upgrade to Level " + (tool.getUpgradeLevel() + 1) + " (" + tool.getUpgradePrice() + " Gold)");
            }
        }
        updateClickerShopPrices();
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }
}
