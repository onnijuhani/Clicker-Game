package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.characters.AuthorityCharacter;
import model.characters.Person;
import model.resourceManagement.payments.Tax;

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
    }

    @FXML
    private VBox guildBox;
    @FXML
    private VBox rivalingNationsBox;
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
    }

    @FXML
    void increaseTax(ActionEvent event) {
        if(player.getCharacter() instanceof AuthorityCharacter authorityCharacter){
            authorityCharacter.getAuthorityPosition().getTaxForm().increaseTaxRate();
            Tax.TaxRate taxRate = authorityCharacter.getAuthorityPosition().getTaxForm().getCurrentTaxRate();
            increaseTaxBtn.setDisable(taxRate == EXTREME);
            lowerTaxBtn.setDisable(taxRate == LOW);
        }

    }



    @FXML
    void lowerTax(ActionEvent event) {
        if(player.getCharacter() instanceof AuthorityCharacter authorityCharacter){
            authorityCharacter.getAuthorityPosition().getTaxForm().decreaseTaxRate();
            Tax.TaxRate taxRate = authorityCharacter.getAuthorityPosition().getTaxForm().getCurrentTaxRate();
            increaseTaxBtn.setDisable(taxRate == EXTREME);
            lowerTaxBtn.setDisable(taxRate == LOW);
        }
    }



}
