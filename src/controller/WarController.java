package controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Model;
import model.worldCreation.Nation;
@SuppressWarnings("CallToPrintStackTrace")
public class WarController extends BaseController {
    @Override
    public void update() {
        updateDetails();
    }

    private void updateDetails() {
        Nation nation =  Model.getPlayerRole().getNation();
        if(nation.isAtWar()){
            details.setText(nation.getWar().getDetails());
        }
    }

    @FXML
    private Label details;

}
