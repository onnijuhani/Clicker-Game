package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.Settings;
import model.buildings.Construct;
import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.Character;
import model.characters.decisions.CombatService;
import model.characters.player.Player;
import model.resourceManagement.TransferPackage;
import model.shop.Shop;
import model.stateSystem.Event;
import model.stateSystem.GameEvent;

import java.util.HashMap;
import java.util.Map;

// UPDATED BY CHARACTER CONTROLLER
public class PropertyController extends BaseController {
    @FXML
    private Label propertyName;
    @FXML
    private ImageView propertyPicture;
    @FXML
    private Label propertyType;
    @FXML
    private Button showHideButton;
    @FXML
    private AnchorPane content;
    @FXML
    private Label utilitySlots;
    private boolean isShowing = true;
    private MainController main;
    private CharacterController characterController;
    private Property property;
    private Shop shop;
    private Character character;
    @FXML
    private Label maintenanceCost;

    @FXML
    private Label vaultValue;
    @FXML
    private Button robVaultBtn;
    @FXML
    private Label defenceLevel;

    @FXML
    private Button upgradeDefBtn;
    @FXML
    private Label upgradeDefLabel;
    @FXML
    private Label upgradeCost;
    @FXML
    void upgradeProperty(){
        Construct.constructProperty(character);
    }
    @FXML
    private VBox upgradeBox;

    @FXML
    private Label constructDaysLeft;
    @FXML
    private Button constructBtn;

     //MEADOWLANDS
    @FXML
    private Label utilityInfo; //shows information about the current state of the building. Gets it from the object itself
    @FXML
    private Button utilityPrice; //shows the initial purchase price
    @FXML
    private Button utilityUpgrade; //shows the upgrade price
    @FXML
    private VBox utilityInfoView; // shown when the building is actually owned. Shows this one always for NPCs
    @FXML
    private VBox utilityBuyView; // shown ONLY to player is they dont own the player
    @FXML
    private VBox utilityPlayerView; // this one exists to hide upgrade button from NPCs

    //ALLOYMINE
    @FXML
    private Label utilityInfo1;
    @FXML
    private Button utilityPrice1;
    @FXML
    private Button utilityUpgrade1;
    @FXML
    private VBox utilityInfoView1;
    @FXML
    private VBox utilityBuyView1;
    @FXML
    private VBox utilityPlayerView1;

    //GOLDMINE
    @FXML
    private Label utilityInfo2;
    @FXML
    private Button utilityPrice2;
    @FXML
    private Button utilityUpgrade2;
    @FXML
    private VBox utilityInfoView2;
    @FXML
    private VBox utilityBuyView2;
    @FXML
    private VBox utilityPlayerView2;

    //SLAVE FACILITY
    @FXML
    private Label utilityInfo3;
    @FXML
    private Button utilityPrice3;
    @FXML
    private Button utilityUpgrade3;
    @FXML
    private VBox utilityInfoView3;
    @FXML
    private VBox utilityBuyView3;
    @FXML
    private VBox utilityPlayerView3;

    //MYSTIC MINE
    @FXML
    private Label utilityInfo4;
    @FXML
    private Button utilityPrice4;
    @FXML
    private Button utilityUpgrade4;
    @FXML
    private VBox utilityInfoView4;
    @FXML
    private VBox utilityBuyView4;
    @FXML
    private VBox utilityPlayerView4;

    private final Map<UtilityBuildings, UtilityBuildingUI> buildingUIs = new HashMap<>();

    private void initializeUIMappings() {
        buildingUIs.put(UtilityBuildings.MeadowLands, new UtilityBuildingUI(utilityInfo, utilityPrice, utilityUpgrade, utilityInfoView, utilityBuyView, utilityPlayerView));
        buildingUIs.put(UtilityBuildings.AlloyMine, new UtilityBuildingUI(utilityInfo1, utilityPrice1, utilityUpgrade1, utilityInfoView1, utilityBuyView1, utilityPlayerView1));
        buildingUIs.put(UtilityBuildings.GoldMine, new UtilityBuildingUI(utilityInfo2, utilityPrice2, utilityUpgrade2, utilityInfoView2, utilityBuyView2, utilityPlayerView2));
        buildingUIs.put(UtilityBuildings.SlaveFacility, new UtilityBuildingUI(utilityInfo3, utilityPrice3, utilityUpgrade3, utilityInfoView3, utilityBuyView3, utilityPlayerView3));
        buildingUIs.put(UtilityBuildings.MysticMine, new UtilityBuildingUI(utilityInfo4, utilityPrice4, utilityUpgrade4, utilityInfoView4, utilityBuyView4, utilityPlayerView4));
    }

    void updatePrices() {
        utilityPrice.setText(Settings.get("meadowLandsCost") + " Gold");
        utilityPrice1.setText(Settings.get("alloyMineCost") + " Gold");
        utilityPrice2.setText(Settings.get("goldMineCost") + " Gold");
        utilityPrice3.setText(Settings.get("slaveFacilityCost") + " Gold");
        utilityPrice4.setText(Settings.get("mysticMineCost") + " Gold");
    }


    private void updateUIForBuilding(UtilityBuildings building) {
        UtilityBuildingUI ui = buildingUIs.get(building);
        if (property.getUtilitySlot().isUtilityBuildingOwned(building)) {
            ui.upgradeButton.setText(property.getUtilitySlot().getUtilityBuilding(building).getUpgradePrice() + " Gold");
            ui.upgradeButton.setVisible(true);
            ui.buyView.setVisible(false);
            ui.infoView.setVisible(true);
            ui.infoLabel.setText(property.getUtilitySlot().getUtilityBuilding(building).getInfo());
            ui.utilityPlayerView.setVisible(true);
        } else {
            ui.priceButton.setVisible(true);
            ui.utilityPlayerView.setVisible(true);
            ui.buyView.setVisible(true);
            ui.infoView.setVisible(false);
        }
    }

    private void updateNPCUIForBuilding(UtilityBuildings building) {
        UtilityBuildingUI ui = buildingUIs.get(building);
        ui.utilityPlayerView.setVisible(false);
        ui.buyView.setVisible(false);
        ui.infoView.setVisible(true);
        if (property.getUtilitySlot().isUtilityBuildingOwned(building)) {
            ui.infoLabel.setText(property.getUtilitySlot().getUtilityBuilding(building).getInfo());
        } else {
            ui.infoLabel.setText("Not Owned");
        }
        ui.priceButton.setVisible(false);
        ui.upgradeButton.setVisible(false);

    }

    @FXML
    void robVault(){
        CombatService.executeRobbery(model.accessPlayer(), character);
    }


    private void differentiatePlayer() {
        if(character instanceof Player) {
            buildingUIs.keySet().forEach(this::updateUIForBuilding);
        } else {
            buildingUIs.keySet().forEach(this::updateNPCUIForBuilding);
        }
        playerVersionState();
    }


    public void updatePropertyTab(){
        updatePropertyName();
        updatePropertyType();
        updatePropertyImage();
        updateMaintenance();
        updatePrices();
        differentiatePlayer();
        updateUtilitySlot();
        updateVaultValue();
        updateDefenceLevel();
        updateConstructInfo();

    }

    void updateConstructionTimeLeft() {
        GameEvent constructionEvent = character.getOngoingEvents().stream()
                .filter(event -> event.getEvent() == Event.CONSTRUCTION)
                .findFirst()
                .orElse(null);

        if (constructionEvent != null) {
            // Ongoing construction
            int[] timeLeft = constructionEvent.timeLeftUntilExecution();
            constructDaysLeft.setText(String.format("%d days, %d months, %d years left", timeLeft[2], timeLeft[1], timeLeft[0]));
            constructBtn.setDisable(true);
            constructBtn.setText("Under Construction");
            constructDaysLeft.setVisible(true);
        } else {
            // No ongoing construction event
            constructDaysLeft.setText("No construction in progress");
            constructBtn.setDisable(false);
            constructDaysLeft.setVisible(false);
        }
    }

    void updateConstructInfo(){
        upgradeBox.setVisible(character instanceof Player);
        TransferPackage cost = Construct.getCost(character);
        constructBtn.setText("Construct "+ Construct.getNextProperty(character));
        if (cost != null) {
            upgradeCost.setText(cost.toShortString());
        } else {
            upgradeCost.setText("Max");
            constructBtn.setVisible(false);
        }
        updateConstructionTimeLeft();
    }




    @FXML
    void showHideContent(ActionEvent event) {
        if (isShowing) {
            content.setVisible(false);
            showHideButton.setText("Show");
            isShowing = false;
        } else {
            content.setVisible(true);
            showHideButton.setText("Hide");
            isShowing = true;
        }
    }



    void playerVersionState(){
        if(model.accessPlayer().equals(character)) {
            robVaultBtn.setVisible(false);
            upgradeDefBtn.setVisible(true);
            upgradeDefBtn.setText(property.getDefense().getUpgradePrice()+" Alloys");
            upgradeDefLabel.setVisible(true);

        }else{
            robVaultBtn.setVisible(true);
            upgradeDefBtn.setVisible(false);
            upgradeDefLabel.setVisible(false);
        }
    }

    @FXML
    void upgradeDef(){
        property.upgradeDefence();
        updatePropertyTab();
    }
    void updateVaultValue() {
        vaultValue.setText(property.getVault().toShortString());
    }
    void updateDefenceLevel() {
        defenceLevel.setText("Level: " + property.getDefense().getUpgradeLevel());
    }

    void updateMaintenance(){
        maintenanceCost.setText(property.getMaintenance().toString());
    }
    public void updatePropertyName(){
        propertyName.setText(property.getName());
    }
    public void updatePropertyImage(){
        propertyPicture.setImage(property.getImage());
    }
    public void updatePropertyType(){
        propertyType.setText(property.getClass().getSimpleName());

    }
    void updateUtilitySlot(){
        int amountTotal = property.getUtilitySlot().getSlotAmount();
        int amountUsed = property.getUtilitySlot().getOwnedUtilityBuildings().size();

        utilitySlots.setText(amountUsed + "/" + amountTotal);
    }

    public void setMain(MainController main) {
        this.main = main;
    }
    @FXML
    public void initialize() {
        initializeUIMappings();
    }

    public controller.CharacterController getCharacterController() {
        return characterController;
    }

    public void setCharacterController(controller.CharacterController characterController) {
        this.characterController = characterController;
    }

    public void setCurrentProperty(Property property) {
        this.property = property;
        setCharacter();
        setShop();
        updatePropertyTab();
    }

    public void setCharacter() {
        this.character = property.getOwner();
    }

    public void setShop() {
        this.shop = character.getNation().getShop();
    }


    @FXML
    void buyMysticMine(ActionEvent event) {
        boolean wasPurchaseSuccessful = shop.getUtilityShop().buyBuilding(UtilityBuildings.MysticMine, character);
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void upgradeMysticMine(ActionEvent event) {
        boolean wasUpgradeSuccessful = shop.getUtilityShop().upgradeBuilding(UtilityBuildings.MysticMine, character);
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void buySlaveFacility(ActionEvent event) {
        boolean wasPurchaseSuccessful = shop.getUtilityShop().buyBuilding(UtilityBuildings.SlaveFacility, character);
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void upgradeSlaveFacility(ActionEvent event) {
        boolean wasUpgradeSuccessful = shop.getUtilityShop().upgradeBuilding(UtilityBuildings.SlaveFacility, character);
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void buyGold(ActionEvent event) {
        boolean wasPurchaseSuccessful = shop.getUtilityShop().buyBuilding(UtilityBuildings.GoldMine, character);
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void upgradeGold(ActionEvent event) {
        boolean wasUpgradeSuccessful = shop.getUtilityShop().upgradeBuilding(UtilityBuildings.GoldMine, character);
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void buyAlloy(ActionEvent event) {
        boolean wasPurchaseSuccessful = shop.getUtilityShop().buyBuilding(UtilityBuildings.AlloyMine, character);
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void upgradeAlloy(ActionEvent event) {
        boolean wasUpgradeSuccessful = shop.getUtilityShop().upgradeBuilding(UtilityBuildings.AlloyMine, character);
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void buyMeadow(ActionEvent event) {
        boolean wasPurchaseSuccessful = shop.getUtilityShop().buyBuilding(UtilityBuildings.MeadowLands, character);
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void upgradeMeadow(ActionEvent event) {
        boolean wasUpgradeSuccessful = shop.getUtilityShop().upgradeBuilding(UtilityBuildings.MeadowLands, character);
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }


    public static class UtilityBuildingUI {
        public Label infoLabel;
        public Button priceButton;
        public Button upgradeButton;
        public VBox infoView;
        public VBox buyView;
        private VBox utilityPlayerView;
        public UtilityBuildingUI(Label infoLabel, Button priceButton, Button upgradeButton, VBox infoView, VBox buyView, VBox utilityPlayerView) {
            this.infoLabel = infoLabel;
            this.priceButton = priceButton;
            this.upgradeButton = upgradeButton;
            this.infoView = infoView;
            this.buyView = buyView;
            this.utilityPlayerView = utilityPlayerView;
        }
    }



}


