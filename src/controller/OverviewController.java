package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Model;
import model.characters.AuthorityCharacter;
import model.characters.Peasant;
import model.characters.Person;
import model.resourceManagement.payments.Tax;
import model.war.WarManager;
import model.war.WarPlanningManager;
import model.worldCreation.Nation;

import java.util.ArrayList;
import java.util.Objects;

import static model.buildings.utilityBuilding.UtilityBuildings.SlaveFacility;
import static model.buildings.utilityBuilding.UtilityBuildings.WorkerCenter;
import static model.resourceManagement.payments.Tax.TaxRate.EXTREME;
import static model.resourceManagement.payments.Tax.TaxRate.LOW;

public class OverviewController extends BaseController{

    @Override
    public void update() {
        updateGuildBox();
        updateTaxBox();
        updateRivalingNationsBox();
    }

    @FXML
    private VBox guildBox;
    @FXML
    private VBox rivalingNationsBox;
    @FXML
    private ScrollPane rivalingNationsScrollPane;
    private static final int UPDATE_THRESHOLD = 50;
    private static int updateRivalsCounter = UPDATE_THRESHOLD; // makes sure rivals aren't updated too often


    private void updateRivalingNationsBox() {

        if (updateRivalsCounter < UPDATE_THRESHOLD) {
            updateRivalsCounter++;
            return;
        }

        updateRivalsCounter = 0;

        rivalingNationsBox.getChildren().clear();

        ArrayList<WarPlanningManager.NationDetails> nationsInfo = WarPlanningManager.getNationsInfo();

        // Create header
        GridPane header = createHeader();
        rivalingNationsBox.getChildren().add(header);

        for (WarPlanningManager.NationDetails nationDetails : nationsInfo) {
            GridPane nationGrid = createNationGrid(nationDetails);
            rivalingNationsBox.getChildren().add(nationGrid);
        }
    }

    private GridPane createHeader() {
        GridPane header = new GridPane();
        header.setHgap(10);
        header.setPadding(new Insets(10, 10, 10, 10));

        ColumnConstraints nameCol = new ColumnConstraints();
        nameCol.setMinWidth(150);
        nameCol.setPrefWidth(200);
        nameCol.setMaxWidth(300);

        ColumnConstraints powerCol = new ColumnConstraints();
        powerCol.setMinWidth(150);
        powerCol.setPrefWidth(150);

        header.getColumnConstraints().addAll(nameCol, powerCol);

        Label nameLabel = new Label("Nation");
        styleHeaderLabel(nameLabel);

        Label powerLabel = new Label("Military Power");
        styleHeaderLabel(powerLabel);

        header.add(nameLabel, 0, 0);
        header.add(powerLabel, 1, 0);

        return header;
    }

    private void styleHeaderLabel(Label label) {
        label.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
    }

    private GridPane createNationGrid(WarPlanningManager.NationDetails nationDetails) {
        GridPane nationGrid = new GridPane();
        nationGrid.setHgap(10); // Set horizontal gap between columns
        nationGrid.setVgap(5); // Set vertical gap between rows
        nationGrid.setPadding(new Insets(5, 10, 5, 10)); // Add some padding

        // Set column constraints
        ColumnConstraints nameCol = new ColumnConstraints();
        nameCol.setMinWidth(150);
        nameCol.setPrefWidth(200);
        nameCol.setMaxWidth(300);

        ColumnConstraints powerCol = new ColumnConstraints();
        powerCol.setMinWidth(150);
        powerCol.setPrefWidth(150);

        ColumnConstraints actionCol = new ColumnConstraints();
        actionCol.setMinWidth(75);
        actionCol.setPrefWidth(75);

        nationGrid.getColumnConstraints().addAll(nameCol, powerCol, actionCol);

        // Create labels for each piece of information
        Label nameLabel = new Label(nationDetails.nation().toString());
        nameLabel.setStyle("-fx-text-fill: white;");
        Label powerLabel = new Label(String.valueOf(nationDetails.militaryPower()));
        powerLabel.setStyle("-fx-text-fill: white;");

        // Create hyperlink for starting a war
        Hyperlink startWarLink = new Hyperlink("Start War");
        startWarLink.setOnAction(event -> startWar(nationDetails.nation()));

        // Add labels and hyperlink to the GridPane
        nationGrid.add(nameLabel, 0, 0);
        nationGrid.add(powerLabel, 1, 0);

        if(Model.getPlayerAsCharacter() instanceof Peasant) {
            nationGrid.add(startWarLink, 2, 0);
        }

        return nationGrid;
    }

    private void startWar(Nation nation) {
        WarManager.startWar(Model.getPlayerRole().getNation(), nation);
    }





    @FXML
    private VBox taxBox;

    @FXML
    private Button increaseTaxBtn;

    @FXML
    private Button lowerTaxBtn;
    public void setPlayer(Person player) {
        this.player = player;
    }
    @FXML
    private Button joinGuildBtn;
    private Person player;

    private String guildInfo;

    void updateGuildBox(){
        if(player.getProperty().getUtilitySlot().isUtilityBuildingOwned(SlaveFacility)){
            guildBox.setVisible(true);
            guildName.setText("Slaver Guild");
            guildInfo = "Slaver";
        } else if (player.getProperty().getUtilitySlot().isUtilityBuildingOwned(WorkerCenter)) {
            guildBox.setVisible(true);
            guildName.setText("Freedom Guild");
            guildInfo = "Liberal";
        } else{
            guildBox.setVisible(false);
        }
    }

    @FXML
    void joinGuild(ActionEvent event) {
        if(guildInfo == null){
            return;
        }
        if(Objects.equals(guildInfo, "Slaver")){
            player.getRole().getNation().joinSlaverGuild(player);
        }else{
            player.getRole().getNation().joinLiberalGuild(player);
        }
        joinGuildBtn.setText("Member");
        joinGuildBtn.setDisable(true);

    }


    @FXML
    private Label currentPosition;

    @FXML
    private Label currentTaxPercent;

    @FXML
    private Label currentTerritoryOwned;

    @FXML
    private Label guildName;


    void updateTaxBox(){
        if(player.getCharacter() instanceof AuthorityCharacter authorityCharacter){
            taxBox.setVisible(true);
            double taxRate = authorityCharacter.getAuthorityPosition().getTaxForm().getTaxRate();
            currentTaxPercent.setText( (int) taxRate + "% " + authorityCharacter.getAuthorityPosition().getTaxForm().getCurrentTaxRate());
        }else{
            taxBox.setVisible(false);
        }
        testTaxRate();
    }

    @FXML
    void increaseTax(ActionEvent event) {
        if(player.getCharacter() instanceof AuthorityCharacter authorityCharacter){
            authorityCharacter.getAuthorityPosition().getTaxForm().increaseTaxRate();
            testTaxRate();
        }

    }



    @FXML
    void lowerTax(ActionEvent event) {
        if(player.getCharacter() instanceof AuthorityCharacter authorityCharacter){
            authorityCharacter.getAuthorityPosition().getTaxForm().decreaseTaxRate();
            testTaxRate();
        }
    }

    private void testTaxRate() {
        if(player.getCharacter() instanceof AuthorityCharacter authorityCharacter) {
            Tax.TaxRate taxRate = authorityCharacter.getAuthorityPosition().getTaxForm().getCurrentTaxRate();
            increaseTaxBtn.setDisable(taxRate == EXTREME);
            lowerTaxBtn.setDisable(taxRate == LOW);
        }
    }


}
