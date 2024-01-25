package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import model.resourceManagement.Resource;

public class ClickerShopController extends BaseController {
    @FXML private Button buyAlloyClickerButton;
    @FXML private Button buyGoldClickerButton;
    @FXML private Label alloyPriceLabel;
    @FXML private Label goldPriceLabel;


    public ClickerShopController() {
    }

    @FXML
    public void initialize() {
    }

    private void updateShopPrices() {
        int alloyPrice = model.accessShop().getClickerShop().getPrice(Resource.Alloy);
        int goldPrice = model.accessShop().getClickerShop().getPrice(Resource.Gold);
        alloyPriceLabel.setText(alloyPrice + " Gold");
        goldPriceLabel.setText(goldPrice + " Gold");
        // Update other shop prices and information here...
    }

    @FXML
    void buyAlloyClicker() {
        boolean purchaseSuccessful = model.accessShop().getClickerShop().buyClicker(Resource.Alloy, model.accessPlayer());
        buyAlloyClickerButton.setDisable(purchaseSuccessful);
        if (purchaseSuccessful) {
            buyAlloyClickerButton.setText("Owned!");
        }
    }
    protected void updateClickerShopPrices() {
        int alloyPrice = model.accessShop().getClickerShop().getPrice(Resource.Alloy);
        int goldPrice = model.accessShop().getClickerShop().getPrice(Resource.Gold);
        buyAlloyClickerButton.setText(alloyPrice + " Gold");
        buyGoldClickerButton.setText(goldPrice + " Gold");
    }

    @FXML
    void buyGoldClicker() {
        try {
            model.accessShop().getClickerShop().buyClicker(Resource.Gold, model.accessPlayer());
            buyGoldClickerButton.setDisable(true);
            buyGoldClickerButton.setText("Owned!");
        } catch (IllegalArgumentException e) {
            model.accessPlayer().getEventTracker().addEvent("Not Enough Gold To Buy Gold Clicker");
        }

    }

    @FXML
    void upgradeAlloyClicker(MouseEvent event) {

    }

    @FXML
    void upgradeFoodClicker(MouseEvent event) {

    }

    @FXML
    void upgradeGoldClicker(MouseEvent event) {

    }
}
