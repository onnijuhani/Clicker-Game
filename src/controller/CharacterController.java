package controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import model.characters.Character;

public class CharacterController extends BaseController  {

    @FXML
    private Label characterName;
    @FXML
    private ImageView characterPicture;
    @FXML
    private Label characterStatus;
    @FXML
    private Label walletInfo;
    private Timeline updateTimeline;

    private MainController main;
    private PropertyController propertyController;

    private Character currentCharacter;

    private void updateWalletInfo(){
        walletInfo.setText(currentCharacter.getWallet().toString());
    }

    public void updateCharacterTab(){
        updateCharacterName();
        updateCharacterStatus();
        propertyController.setCurrentProperty(currentCharacter.getProperty());
        propertyController.updatePropertyTab();
        updateWalletInfo();
    }
    public void updateCharacterName(){
        characterName.setText(currentCharacter.getName());
    }
    public void updateCharacterStatus(){
        characterStatus.setText(currentCharacter.getStatus().toString());
    }

    public void setMain(MainController main) {
        this.main = main;
    }
    @FXML
    public void initialize() {
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateCharacterTab()));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();
    }




    public PropertyController getPropertyController() {
        return propertyController;
    }

    public void setPropertyController(PropertyController propertyController) {
        this.propertyController = propertyController;
    }

    public Character getCurrentCharacter() {
        return currentCharacter;
    }

    public void setCurrentCharacter(Character currentCharacter) {
        this.currentCharacter = currentCharacter;
        updateCharacterTab();
    }

}
