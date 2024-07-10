package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Model;
import model.characters.AuthorityCharacter;
import model.characters.Character;
import model.characters.Person;
import model.characters.npc.King;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.MessageTracker;
import model.stateSystem.SpecialEventsManager;
import model.war.WarPlanningManager;
import model.war.WarService;
import model.worldCreation.Nation;

import java.text.DecimalFormat;
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
        updateCurrentTerritoryOwned();
        updateNationWallet();
    }

    @FXML
    private VBox guildBox;
    @FXML
    private VBox rivalingNationsBox;
    private static final int UPDATE_THRESHOLD = 50;
    private static int updateRivalsCounter = UPDATE_THRESHOLD; // makes sure rivals aren't updated too often

    @FXML
    private VBox taxBox;
    @FXML
    private Button increaseTaxBtn;
    @FXML
    private Button lowerTaxBtn;
    @FXML
    private Button joinGuildBtn;
    private Person player;
    private String guildInfo;
    @FXML
    private VBox nationWalletBox;
    @FXML
    private Label nationWallet;
    @FXML
    private Label guildName;
    @FXML
    private Label currentTaxPercent;
    @FXML
    private Label currentTerritoryOwned;


    private void updateNationWallet(){
        nationWalletBox.setVisible(false);
        if(player.getCharacter() instanceof King){
            Wallet wallet = player.getRole().getNation().getWallet();
            nationWallet.setText("Nation Wallet:\n" + wallet.toStringValuesRows());
            nationWalletBox.setVisible(true);
        }
    }

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


    private void updateCurrentTerritoryOwned(){
        double t = Model.getPlayerTerritory();
        t *= 100;
        String formattedTerritory;

        if (t < 1) {
            formattedTerritory = new DecimalFormat("#.##").format(t);
        } else if (t < 10) {
            formattedTerritory = new DecimalFormat("#.#").format(t);
        } else {
            formattedTerritory = new DecimalFormat("#").format(t);
        }

        currentTerritoryOwned.setText(formattedTerritory + "%");
    }


    @FXML
    void triggerTerritoryInfo(MouseEvent event) {

        SpecialEventsManager.triggerTerritoryInfo((int) Model.getPlayerTerritory()*100);
    }

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

        Hyperlink hyperlink = getHyperlink(nationDetails);

        // Add labels and hyperlink to the GridPane
        nationGrid.add(nameLabel, 0, 0);
        nationGrid.add(powerLabel, 1, 0);

        if(Model.getPlayerAsCharacter() instanceof Character) {
            nationGrid.add(hyperlink, 2, 0);
        }

        return nationGrid;
    }

    private Hyperlink getHyperlink(WarPlanningManager.NationDetails nationDetails) {
        Hyperlink hyperlink;

        if(nationDetails.nation().isVassal()){
            // Hyperlink for vassals ( Cannot enter war with nations that are vassals of another nation)
            hyperlink = new Hyperlink("Vassal");
            hyperlink .setOnAction(event -> vassalInfo(nationDetails.nation()));
        }else {
            // Create hyperlink for starting a war
            hyperlink  = new Hyperlink("Start War");
            hyperlink .setOnAction(event -> startWar(nationDetails.nation()));
        }
        return hyperlink;
    }

    private void startWar(Nation nation) {
        WarService.startWar(Model.getPlayerRole().getNation(), nation);
    }

    private void vassalInfo(Nation nation) {
        Model.getPlayerAsPerson().getMessageTracker().addMessage(MessageTracker.Message("Major", nation + " is vassal under: " + nation.getOverlord()));
    }

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

    public void setPlayer(Person player) {
        this.player = player;
    }


}
