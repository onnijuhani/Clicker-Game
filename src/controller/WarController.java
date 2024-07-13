package controller;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Model;
import model.stateSystem.SpecialEventsManager;
import model.war.War;
import model.worldCreation.Nation;

import java.util.Collection;
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
        if(onGoingWar == null) return;
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

        mPowerRight.setText("Military Power: " +  onGoingWar.getTotalPower(n));

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

    private void updateInspector(String inspectorListName, Collection<?> collection){
        inspector.setVisible(true);
        this.inspectorListName.setText(inspectorListName);

        inspectList.getChildren().clear();

        for(Object o : collection){
            Label label = new Label(o.toString());
            label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            inspectList.getChildren().add(label);
        }
    }

    @FXML
    void openOnGoingBattles(MouseEvent event) {
        if(onGoingWar == null) return;
        updateInspector("On Going Battles", onGoingWar.getOnGoingBattles());
    }

    @FXML
    void closeInspector(ActionEvent event) {
        inspector.setVisible(false);
    }

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

        Platform.runLater(() -> {
            warNotesContainer.getChildren().clear();

            for (String s : logs) {
                Label label = new Label(s);
                label.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                label.setWrapText(true);
                label.setMaxWidth(600);
                warNotesContainer.getChildren().add(label);
            }

            // Use Timeline to set the scroll position with a slight delay
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> {
                // Ensure the layout is updated
                warNotes.layout();
                // Scroll to bottom
                warNotes.setVvalue(1.0);
            }));
            timeline.play();
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



}
