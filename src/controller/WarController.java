package controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Model;
import model.stateSystem.SpecialEventsManager;
import model.war.War;
import model.worldCreation.Nation;
@SuppressWarnings("CallToPrintStackTrace")
public class WarController extends BaseController {
    @Override
    public void update() {
        checkForOngoingWar();
        if(checkIfWarIsNull()) return;
        updateDetails();
    }

    private boolean checkIfWarIsNull() {
        return onGoingWar == null;
    }

    private War onGoingWar;

    private void checkForOngoingWar(){
        if(checkIfWarIsNull()){
            Nation nation =  Model.getPlayerRole().getNation();
            if(nation.isAtWar()){
                onGoingWar = nation.getWar();
            }
        }
    }

    private void updateDetails() {
        details.setText(onGoingWar.getDetails());

    }

    @FXML
    void triggerWarRulesInfo(ActionEvent event) {
        if(checkIfWarIsNull()){
            SpecialEventsManager.triggerWarRulesInfo(War.Phase.WAITING);
        }else {
            SpecialEventsManager.triggerWarRulesInfo(onGoingWar.getCurrentPhase());
        }
    }


    @FXML
    private Label details;

}
