package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import model.Settings;
import model.buildings.Property;
import model.buildings.utilityBuilding.UtilityBuildings;
import model.characters.Character;
import model.characters.player.Player;
import model.shop.Shop;

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




     //MEADOWLANDS
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



    @FXML
    void buyMeadow(ActionEvent event) {
        boolean wasPurchaseSuccessful = shop.getUtilityBuildingShop().buyBuilding(UtilityBuildings.MeadowLands, character);
        if (wasPurchaseSuccessful) {
            differentiatePlayer();
        }
    }

    @FXML
    void upgradeMeadow(ActionEvent event) {
        boolean wasUpgradeSuccessful = shop.getUtilityBuildingShop().upgradeBuilding(UtilityBuildings.MeadowLands, character);
        if (wasUpgradeSuccessful) {
            differentiatePlayer();
        }
    }

    private void differentiatePlayer() {
        if(character instanceof Player) {
            if (property.getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                utilityUpgrade1.setText(property.getUtilitySlot().getUtilityBuilding(UtilityBuildings.MeadowLands).getUpgradePrice() + " Gold");
                showPlayerVersion();
            } else {
                showMeadowBuyScreen();
            }

        } else {
            showNPCVersion();
            if (property.getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                utilityInfo1.setText(property.getUtilitySlot().getUtilityBuilding(UtilityBuildings.MeadowLands).getInfo());
            } else {
                utilityInfo1.setText("Not Owned");
            }
        }
    }

    private void showMeadowBuyScreen() {
        utilityBuyView1.setVisible(true);
        utilityInfoView1.setVisible(false);
    }

    private void showPlayerVersion() {
        utilityPlayerView1.setVisible(true);
        utilityBuyView1.setVisible(false);
        utilityInfoView1.setVisible(true);
        utilityInfo1.setText(property.getUtilitySlot().getUtilityBuilding(UtilityBuildings.MeadowLands).getInfo());
    }
    private void showNPCVersion() {
        utilityBuyView1.setVisible(false);
        utilityInfoView1.setVisible(true);
        utilityPlayerView1.setVisible(false);
    }


    void updatePrices() {
        utilityPrice1.setText(Settings.get("meadowLandsCost") + " Gold");


    }


    public void updatePropertyTab(){
        updatePropertyName();
        updatePropertyType();
        updatePropertyImage();
        updatePrices();
        differentiatePlayer();
        updateUtilitySlot();

    }

    @FXML
    void showHideContent(ActionEvent event) {
        if (isShowing) {
            content.setVisible(false);
            showHideButton.setText("Show");
            isShowing = false;
        } else if (!isShowing) {
            content.setVisible(true);
            showHideButton.setText("Hide");
            isShowing = true;
        }
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
    public void initialize() {}

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
        this.shop = model.accessShop();
    }


}
