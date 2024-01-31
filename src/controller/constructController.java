package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import model.buildings.Construct;
import model.buildings.Properties;

public class constructController extends BaseController {

    @FXML
    private Label newCost;

    @FXML
    private Label newType;

    @FXML
    private Label oldType;

    @FXML
    private ListView<Button> propertyList;

    private Properties typeSelected;

    @FXML
    void constructNew(ActionEvent event) {
        Construct.constructProperty(typeSelected, model.accessPlayer());
    }
    @FXML
    public void initialize() {
        for (Properties type : Properties.values()) {
            Button button = new Button(type.toString());
            button.setOnAction(event -> handlePropertySelection(type));
            propertyList.getItems().add(button);
        }

    }

    private void handlePropertySelection(Properties type) {
        this.typeSelected = type;
        newCost.setText(String.valueOf(Construct.getCost(type)));
        newType.setText(type.toString());
        oldType.setText(model.accessPlayer().getProperty().getClass().getSimpleName());
    }

}
