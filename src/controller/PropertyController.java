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
    private boolean isShowing = true;
    @FXML
    private AnchorPane content;
    private MainController main;
    private CharacterController characterController;
    private Property property;
    private Shop shop;
    private Character character;



    @FXML
    private Label meadowInfo;
    @FXML
    private Button meadowPrice;
    @FXML
    private Button meadowUpgrade;
    @FXML
    private VBox meadowLands;
    @FXML
    private VBox meadowBuy;




    void detectPlayer(){
        if (character instanceof Player) {
            if (property.getUtilitySlot().isUtilityBuildingOwned(UtilityBuildings.MeadowLands)) {
                loadMeadowlands();
            }else{
                meadowBuy.setVisible(true);
                meadowLands.setVisible(false);
            }
        }
    }
    @FXML
    void buyMeadow(ActionEvent event) {
        boolean wasPurchaseSuccessful = shop.getUtilityBuildingShop().buyBuilding(UtilityBuildings.MeadowLands, character);
        if (wasPurchaseSuccessful) {
            loadMeadowlands();
        }
    }

    @FXML
    void upgradeMeadow(ActionEvent event) {
        boolean wasUpgradeSuccessful = shop.getUtilityBuildingShop().upgradeBuilding(UtilityBuildings.MeadowLands, character);
        if (wasUpgradeSuccessful) {
            loadMeadowlands();
        }
    }

    private void loadMeadowlands() {
        meadowUpgrade.setText(property.getUtilitySlot().getUtilityBuilding(UtilityBuildings.MeadowLands).getUpgradePrice() + " Gold");
        meadowInfo.setText(property.getUtilitySlot().getUtilityBuilding(UtilityBuildings.MeadowLands).getInfo());
        meadowBuy.setVisible(false);
        meadowLands.setVisible(true);
    }

    void updatePrices() {
        meadowPrice.setText(Settings.get("meadowLandsCost") + " Gold");




    }


    public void updatePropertyTab(){
        updatePropertyName();
        updatePropertyType();
        updatePropertyImage();
        updatePrices();
        detectPlayer();

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
    public Property getCurrentProperty() {
        return property;
    }

    public void setCurrentProperty(Property property) {
        this.property = property;
        setCharacter();
        setShop();
    }

    public Character getCharacter() {
        return character;
    }
    public void setCharacter() {
        this.character = property.getOwner();
    }
    public Shop getShop() {
        return shop;
    }
    public void setShop() {
        this.shop = model.accessShop();
    }


}
