package controller;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.Model;
import model.stateSystem.SpecialEventsManager;
import model.war.War;
import model.worldCreation.Nation;

import java.util.List;

@SuppressWarnings("CallToPrintStackTrace")
public class WarController extends BaseController {
    @Override
    public void update() {
        if(!onGoingWar()) return;
        updateWarNotes();
        updateWarInformation();
    }



    private void updateWarInformation(){
        updateCommon();
        updateLeft();
        updateRight();



    }

    private void updateCommon() {
        currentDay.setText("Day: " + onGoingWar.getDays());
        currentPhase.setText(onGoingWar.getCurrentPhase().toString());
        onGoingBattles.setText("On Going Battles: " + onGoingWar.getOnGoingBattles().size());
        warName.setText(onGoingWar.getWarNotes().getWarName());
    }

    private void updateLeft(){
        Nation n = Model.getPlayerRole().getNation();

        nationNameLeft.setText(n.toString());
        mInPlayLeft.setText("Militaries in play: " + onGoingWar.getCorrectSet(n, War.SetName.ALL_IN_PLAY).size());
        mDefeatedLeft.setText("Armies Defeated: " + onGoingWar.getCorrectSet(n, War.SetName.DEFEATED).size());
        mPowerLeft.setText("Military Power: " +  onGoingWar.getTotalPower(n));

    }

    private void updateRight(){
        Nation pn = Model.getPlayerRole().getNation();
        Nation n;

        if(onGoingWar.getAttacker() == pn){
            n = onGoingWar.getDefender();
        }else{
            n = onGoingWar.getAttacker();
        }

        nationNameRight.setText(n.toString());
        mInPlayRight.setText("Militaries in play: " + onGoingWar.getCorrectSet(n, War.SetName.ALL_IN_PLAY).size());
        mDefeatedRight.setText("Armies Defeated: " + onGoingWar.getCorrectSet(n, War.SetName.DEFEATED).size());
        mPowerRight.setText("Military Power: " +  onGoingWar.getTotalPower(n));


        mPowerRight.setText("Military Power: " + Nation.countTotalMilitaryStrength(onGoingWar.getCorrectSet(n, War.SetName.ALL_IN_PLAY)));

    }





    @FXML
    private Label currentDay;

    @FXML
    private Label currentPhase;

    @FXML
    private VBox inspectList;

    @FXML
    private ScrollPane inspector;

    @FXML
    private Label inspectorListName;

    @FXML
    private Label mDefeatedLeft;

    @FXML
    private Label mDefeatedRight;

    @FXML
    private Label mInPlayLeft;

    @FXML
    private Label mInPlayRight;

    @FXML
    private Label mPowerLeft;

    @FXML
    private Label mPowerRight;

    @FXML
    private Label nationNameLeft;

    @FXML
    private Label nationNameRight;

    @FXML
    private Button nobleBtn;

    @FXML
    private Label onGoingBattles;

    @FXML
    private Label warName;

    @FXML
    private ScrollPane warNotes;

    @FXML
    private VBox warNotesContainer;

    Nation nation;
    private War onGoingWar;

    private int noteAmount = 0;





    private void updateWarNotes() {
        if (onGoingWar == null) return;

        War.WarNotes currentNotes = onGoingWar.getWarNotes();

        if (noteAmount == currentNotes.getWarLog().size()) {
            return; // No changes in war notes, so no need to update UI
        }

        noteAmount = currentNotes.getWarLog().size();

        List<String> logs = currentNotes.getWarLog();

        // Get the current scroll position
        double scrollPosition = warNotes.getVvalue();

        Platform.runLater(() -> {
            // Clear and update warNotesContainer
            warNotesContainer.getChildren().clear();

            for (String s : logs) {
                Label label = new Label(s);
                label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                label.setWrapText(true);
                label.setMaxWidth(600);
                warNotesContainer.getChildren().add(label);
            }
            // Ensure the layout is updated before setting the scroll position
            warNotes.layout();

            // Set scroll position to the previous value or 1.0 if scrolled to bottom
            warNotes.setVvalue(scrollPosition >= 1.0 ? 1.0 : scrollPosition);
        });
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    private boolean onGoingWar(){
        if(nation == null){
            nation = Model.getPlayerAsCharacter().getRole().getNation();
        }
        if(nation.isAtWar()){
            onGoingWar = nation.getWar();
            return true;
        }
        return false;
    }

    private boolean checkIfWarIsNull() {
        return onGoingWar == null;
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
    void useNobleBonus(ActionEvent event) {

    }

    @FXML
    void closeInspector(ActionEvent event) {

    }

}
