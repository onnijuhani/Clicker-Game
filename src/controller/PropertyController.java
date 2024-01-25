package controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import model.buildings.Property;

public class PropertyController extends BaseController {

    @FXML
    private Label propertyName;
    @FXML
    private ImageView propertyPicture;
    @FXML
    private Label propertyType;
    private MainController main;
    private CharacterController characterController;
    private Property currentProperty;

    public void updatePropertyTab(){
        updatePropertyName();
        updatePropertyType();
        updatePropertyImage();
    }
    public void updatePropertyName(){
        propertyName.setText(currentProperty.getName());
    }
    public void updatePropertyImage(){
        propertyPicture.setImage(currentProperty.getImage());








    }
    public void updatePropertyType(){
        propertyType.setText(currentProperty.getClass().getSimpleName());

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
        return currentProperty;
    }

    public void setCurrentProperty(Property currentProperty) {
        this.currentProperty = currentProperty;
    }


}
