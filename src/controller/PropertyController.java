package controller;


import customExceptions.InsufficientResourcesException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Model;
import model.Settings;
import model.buildings.Construct;
import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.Character;
import model.characters.Person;
import model.characters.combat.CombatService;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.shop.Shop;
import model.shop.UtilityShop;
import model.stateSystem.Event;
import model.stateSystem.GameEvent;
import model.stateSystem.SpecialEventsManager;
import model.stateSystem.State;
import model.time.Time;

import java.util.HashMap;
import java.util.Map;

import static model.Settings.formatNumber;
import static model.Settings.formatShortNumber;
import static model.buildings.utilityBuilding.UtilityBuildings.*;

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

    private Property property;
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

    //WORKER CENTER
    @FXML
    private Label utilityInfo5;
    @FXML
    private Button utilityPrice5;
    @FXML
    private Button utilityUpgrade5;
    @FXML
    private VBox utilityInfoView5;
    @FXML
    private VBox utilityBuyView5;
    @FXML
    private VBox utilityPlayerView5;

    // VAULT DEPOSIT WITHDRAWAL SLIDER
    @FXML
    private StackPane VaultStackPane;
    @FXML
    private Button vaultWithdrawBtn;
    @FXML
    private Button vaultDepositBtn;
    @FXML
    private Label playerVaultSliderAmount;

    @FXML
    private Slider vaultSlider;

    @FXML
    private VBox playerVaultBox;
    @FXML
    private Button sabotageButton;
    @FXML
    private Button sabotageButton1;
    @FXML
    private Button sabotageButton2;
    @FXML
    private Button sabotageButton3;
    @FXML
    private Button sabotageButton4;
    @FXML
    private Button sabotageButton5;

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
        updateVaultButtons();
        updateVaultButtons();


    }
    @FXML
    public void initialize() {
        initializeUIMappings();
        Platform.runLater(this::setUpSlider);
    }

    private void updateVaultButtons(){
            vaultDepositBtn.setDisable(Time.isIsSimulationRunning());
            vaultWithdrawBtn.setDisable(Time.isIsSimulationRunning());
            playerVaultSliderAmount.setDisable(Time.isIsSimulationRunning());
    }

    public void setUpSlider() {
        vaultSlider.setMin(0);
        vaultSlider.setMax(100);
        vaultSlider.setValue(0);


        vaultSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int percentage = (int) vaultSlider.getValue();
            Person person = model.getPlayerPerson();
            int foodAmount =  percentage  * person.getWallet().getFood() / 100;
            int alloyAmount =  percentage * person.getPerson().getWallet().getAlloy() / 100;
            int goldAmount = percentage  * person.getPerson().getWallet().getGold() / 100;



            playerVaultSliderAmount.setText(percentage + "%\t F:" + formatShortNumber(foodAmount) + " A:" + formatShortNumber(alloyAmount) + " G:" + formatShortNumber(goldAmount));
        });

        vaultDepositBtn.setOnAction(e -> vaultDeposit());
        vaultWithdrawBtn.setOnAction(e -> vaultWithdraw());
        resetSlider();
    }

    private void vaultDeposit() {
        SpecialEventsManager.triggerVaultInfo();
        int percentage = (int) vaultSlider.getValue();
        if (percentage == 0){
            return;
        }
        if(character.getPerson().getWallet().isEmpty()){
            return;
        }
        int[] walletBalance = character.getPerson().getWallet().getWalletValues();

        executeVaultDeposit(percentage, walletBalance, character.getPerson().getProperty().getVault(), character.getPerson().getWallet());
        updatePropertyTab();
    }
    private void resetSlider() {
        vaultSlider.setValue(50);
    }

    private void vaultWithdraw() {
        SpecialEventsManager.triggerVaultInfo();
        int percentage = (int) vaultSlider.getValue();
        if (percentage == 0){
            return;
        }
        int[] vaultBalance = character.getPerson().getProperty().getVault().getWalletValues();
        executeVaultWithdrawal(percentage, vaultBalance, character.getPerson().getWallet(), character.getPerson().getProperty().getVault());
        updatePropertyTab();
    }
    private void executeVaultWithdrawal(int percentage, int[] vaultBalance, Wallet wallet, Wallet vault) {
        int[] withdrawAmounts = new int[vaultBalance.length];
        for (int i = 0; i < vaultBalance.length; i++) {
            withdrawAmounts[i] = (vaultBalance[i] * percentage) / 100;
        }
        TransferPackage transfer = TransferPackage.fromArray(withdrawAmounts);
        vault.withdrawal(wallet,transfer);
        resetSlider();
    }

    private void executeVaultDeposit(int percentage, int[] vaultBalance, Wallet wallet, Wallet vault) {
        int[] withdrawAmounts = new int[vaultBalance.length];
        for (int i = 0; i < vaultBalance.length; i++) {
            withdrawAmounts[i] = (vaultBalance[i] * percentage) / 100;
        }
        TransferPackage transfer = TransferPackage.fromArray(withdrawAmounts);
        wallet.deposit(vault, transfer);
        resetSlider();
    }


    private final Map<UtilityBuildings, UtilityBuildingUI> buildingUIs = new HashMap<>();

    private void initializeUIMappings() {
        buildingUIs.put(MeadowLands, new UtilityBuildingUI(MeadowLands, utilityInfo, utilityPrice, utilityUpgrade, utilityInfoView, utilityBuyView, utilityPlayerView, sabotageButton));
        buildingUIs.put(AlloyMine, new UtilityBuildingUI(AlloyMine, utilityInfo1, utilityPrice1, utilityUpgrade1, utilityInfoView1, utilityBuyView1, utilityPlayerView1, sabotageButton1));
        buildingUIs.put(GoldMine, new UtilityBuildingUI(GoldMine, utilityInfo2, utilityPrice2, utilityUpgrade2, utilityInfoView2, utilityBuyView2, utilityPlayerView2, sabotageButton2));
        buildingUIs.put(SlaveFacility, new UtilityBuildingUI(SlaveFacility, utilityInfo3, utilityPrice3, utilityUpgrade3, utilityInfoView3, utilityBuyView3, utilityPlayerView3, sabotageButton3));
        buildingUIs.put(MysticMine, new UtilityBuildingUI(MysticMine, utilityInfo4, utilityPrice4, utilityUpgrade4, utilityInfoView4, utilityBuyView4, utilityPlayerView4, sabotageButton4));
        buildingUIs.put(WorkerCenter, new UtilityBuildingUI(WorkerCenter, utilityInfo5, utilityPrice5, utilityUpgrade5, utilityInfoView5, utilityBuyView5, utilityPlayerView5, sabotageButton5));
    }

    void updatePrices() {
        utilityPrice.setText(formatNumber(Settings.getInt("meadowLandsCost")) + " Gold");
        utilityPrice1.setText(formatNumber(Settings.getInt("alloyMineCost")) + " Gold");
        utilityPrice2.setText(formatNumber(Settings.getInt("goldMineCost")) + " Gold");
        utilityPrice3.setText(formatNumber(Settings.getInt("slaveFacilityCost")) + " Gold");
        utilityPrice4.setText(formatNumber(Settings.getInt("mysticMineCost")) + " Gold");
        utilityPrice5.setText(formatNumber(Settings.getInt("workerCenterCost")) + " Gold");

    }

    @FXML
    void upgradeProperty(){
        try {
            Construct.constructProperty(character.getPerson());
        } catch (InsufficientResourcesException ignored){}
    }


    private void updateUIForBuilding(UtilityBuildings building) {
        UtilityBuildingUI ui = buildingUIs.get(building);
        ui.sabotageButton.setVisible(false);
        if (property.getUtilitySlot().isUtilityBuildingOwned(building)) {
            ui.upgradeButton.setText(formatNumber(property.getUtilitySlot().getUtilityBuilding(building).getUpgradePrice()) + " Gold");
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
        ui.sabotageButton.setVisible(false);

    }

    private void updateNPCUIForBuilding(UtilityBuildings building) {
        UtilityBuildingUI ui = buildingUIs.get(building);
        ui.utilityPlayerView.setVisible(false);
        ui.buyView.setVisible(false);
        ui.infoView.setVisible(true);
        if (property.getUtilitySlot().isUtilityBuildingOwned(building)) {
            ui.infoLabel.setText(property.getUtilitySlot().getUtilityBuilding(building).getInfo());
            ui.sabotageButton.setVisible(true);
            ui.sabotageButton.setVisible(true);
            if (Model.getPlayerAsPerson().hasState(State.SABOTEUR)) {
                ui.sabotageButton.setBlendMode(BlendMode.SOFT_LIGHT);
            } else {
                ui.sabotageButton.setBlendMode(BlendMode.SRC_OVER);
            }
        } else {
            ui.infoLabel.setText("Not Owned");
            ui.sabotageButton.setVisible(false);
        }
        ui.priceButton.setVisible(false);
        ui.upgradeButton.setVisible(false);

    }

    @FXML
    void robVault(){
        CombatService.executeRobbery(model.getPlayerCharacter(), character);
    }


    private void differentiatePlayer() {
        if(character.getPerson().isPlayer()) {
            buildingUIs.keySet().forEach(this::updateUIForBuilding);
        } else {
            buildingUIs.keySet().forEach(this::updateNPCUIForBuilding);
        }
        playerVersionState();
    }




    void updateConstructionTimeLeft() {
        GameEvent constructionEvent = character.getPerson().getOngoingEvents().stream()
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
        upgradeBox.setVisible(character.getPerson().isPlayer());
        TransferPackage cost = Construct.getCost(character.getPerson());
        constructBtn.setText("Construct "+ Construct.getNextProperty(character.getPerson()));
        if (cost != null) {
            upgradeCost.setText(cost.toShortString());
        } else {
            upgradeCost.setText("Max");
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
        if(character.getPerson().isPlayer()) {
            robVaultBtn.setVisible(false);
            playerVaultBox.setVisible(true);
            upgradeDefBtn.setVisible(true);
            upgradeDefBtn.setText(formatNumber(property.getDefenceStats().getUpgradePrice())+" Alloys");
            upgradeDefLabel.setVisible(true);
        }else{
            if(Time.year != 0) {robVaultBtn.setVisible(true);};
            playerVaultBox.setVisible(false);
            upgradeDefBtn.setVisible(false);
            upgradeDefLabel.setVisible(false);
        }
    }

    @FXML
    void upgradeDef(){
        property.upgradeDefenceWithAlloys();
        updatePropertyTab();
    }
    void updateVaultValue() {
        vaultValue.setText(property.getVault().toShortString());
    }
    void updateDefenceLevel() {
        defenceLevel.setText("Level: " + property.getDefenceStats().getUpgradeLevel());
    }

    void updateMaintenance(){
        maintenanceCost.setText(formatShortNumber(property.getMaintenance().toString()));
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



    public void setCharacterController(controller.CharacterController characterController) {
    }

    public void setCurrentProperty(Property property) {
        this.property = property;
        this.character = CharacterController.currentCharacter;
        Shop shop = character.getRole().getNation().getShop();
        updatePropertyTab();
    }



    @FXML
    void buyMysticMine(ActionEvent event) {
        boolean wasPurchaseSuccessful = UtilityShop.buyBuilding(MysticMine, character.getPerson());
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void upgradeMysticMine(ActionEvent event) {
        boolean wasUpgradeSuccessful = UtilityShop.upgradeBuilding(MysticMine, character.getPerson());
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void buyWorkerCenter(ActionEvent event) {
        boolean wasPurchaseSuccessful = UtilityShop.buyBuilding(WorkerCenter, character.getPerson());
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void upgradeWorkerCenter(ActionEvent event) {
        boolean wasUpgradeSuccessful = UtilityShop.upgradeBuilding(WorkerCenter, character.getPerson());
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void buySlaveFacility(ActionEvent event) {
        boolean wasPurchaseSuccessful = UtilityShop.buyBuilding(SlaveFacility, character.getPerson());
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void upgradeSlaveFacility(ActionEvent event) {
        boolean wasUpgradeSuccessful = UtilityShop.upgradeBuilding(SlaveFacility, character.getPerson());
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void buyGold(ActionEvent event) {
        boolean wasPurchaseSuccessful = UtilityShop.buyBuilding(GoldMine, character.getPerson());
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void upgradeGold(ActionEvent event) {
        boolean wasUpgradeSuccessful = UtilityShop.upgradeBuilding(GoldMine, character.getPerson());
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }
    @FXML
    void buyAlloy(ActionEvent event) {
        boolean wasPurchaseSuccessful = UtilityShop.buyBuilding(AlloyMine, character.getPerson());
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void upgradeAlloy(ActionEvent event) {
        boolean wasUpgradeSuccessful = UtilityShop.upgradeBuilding(AlloyMine, character.getPerson());
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void buyMeadow(ActionEvent event) {
        boolean wasPurchaseSuccessful = UtilityShop.buyBuilding(MeadowLands, character.getPerson());
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void upgradeMeadow(ActionEvent event) {
        boolean wasUpgradeSuccessful = UtilityShop.upgradeBuilding(MeadowLands, character.getPerson());
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void triggerPropertyInfo(ActionEvent event) {
        SpecialEventsManager.triggerPropertyInfo();
    }


    public static class UtilityBuildingUI {
        public Label infoLabel;
        public Button priceButton;
        public Button upgradeButton;
        public VBox infoView;
        public VBox buyView;
        private final VBox utilityPlayerView;
        public Button sabotageButton;
        public UtilityBuildingUI(UtilityBuildings type, Label infoLabel, Button priceButton, Button upgradeButton, VBox infoView, VBox buyView, VBox utilityPlayerView, Button sabotageButton) {
            this.infoLabel = infoLabel;
            this.priceButton = priceButton;
            this.upgradeButton = upgradeButton;
            this.infoView = infoView;
            this.buyView = buyView;
            this.utilityPlayerView = utilityPlayerView;
            this.sabotageButton = sabotageButton;

            sabotageButton.setOnAction(event -> handleSabotageAction(type));
        }

        private void handleSabotageAction(UtilityBuildings type) {
            UtilityShop.sabotage(type, CharacterController.currentCharacter.getPerson(), Model.getPlayerAsPerson());
        }
    }



}


