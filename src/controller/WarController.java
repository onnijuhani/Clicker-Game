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



    private War onGoingWar;

    @FXML
    private Label details;


    private void checkForOngoingWar(){
        if(checkIfWarIsNull()){
            Nation nation =  Model.getPlayerRole().getNation();
            if(nation.isAtWar()){
                onGoingWar = nation.getWar();
                if(onGoingWar.getCurrentPhase() == War.Phase.ENDED){
                    onGoingWar = null;
                }
            }else{
                onGoingWar = null;
            }
        }
    }

    private boolean checkIfWarIsNull() {
        return onGoingWar == null;
    }

    private void updateDetails() {
        if(details != null) {
            details.setText(onGoingWar.getDetails());
        }

    }

    @FXML
    void triggerWarRulesInfo(ActionEvent event) {
        if(checkIfWarIsNull()){
            SpecialEventsManager.triggerWarRulesInfo(War.Phase.WAITING);
        }else {
            SpecialEventsManager.triggerWarRulesInfo(onGoingWar.getCurrentPhase());
        }
    }

}
