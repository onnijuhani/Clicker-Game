package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.Model;
import model.characters.Character;
import model.characters.Person;
import model.characters.RelationshipManager;

import java.util.Set;

public class RelationsController extends BaseController {
    @FXML
    public void initialize() {
    }

    public void setCurrentCharacter(Character currentCharacter) {
        this.currentCharacter = currentCharacter;
    }
    private CharacterController characterController;

    private Character currentCharacter; // Character whose relations we are looking at
    private Character current;  //Character that might be selected for inspection
    private Person currentPerson;
    private RelationshipManager relations;

    void setCharacters(Character character){
        this.current = character;
        this.currentPerson = character.getPerson();
    }


    @FXML
    private HBox CombatBox;

    @FXML
    private Label attackLevelLabel;

    void updateLabels(){
        attackLevelLabel.setText("Level: "+currentPerson.getCombatStats().getOffense().getUpgradeLevel());
        defenseLevelLabel.setText("Level: "+currentPerson.getCombatStats().getDefense().getUpgradeLevel());
        characterStatus.setText(current.getRole().getStatus().toString());
        property.setText(currentPerson.getProperty().toString());
        propertyDefenceLevel.setText("Defence: "+currentPerson.getProperty().getDefense().getUpgradeLevel());
        vaultValue.setText(currentPerson.getProperty().getVault().toShortString());
        walletInfo.setText(currentPerson.getWallet().toShortString());
    }

    public void setCurrentCharacterName() {
        currentCharacterName.setText(currentCharacter.toString());
        this.relations = currentCharacter.getPerson().getRelationshipManager();
    }

    @FXML
    private Hyperlink authority;

    @FXML
    private Hyperlink changeCurrentToThis;

    @FXML
    private ImageView characterPicture;

    @FXML
    private Label characterStatus;

    @FXML
    private ListView<?> charactersList;

    @FXML
    private Label compareToPlayer;

    void updatePlayersPerspective(){
        if(Model.getPlayerAsPerson() == currentPerson){
            compareToPlayer.setText("Currently viewing yourself");
        }else {
            compareToPlayer.setText(Model.getPlayerAsPerson().getRelationshipManager().getRelationshipDescription(currentPerson));
        }
    }

    @FXML
    private Label currentCharacterName;

    @FXML
    private AnchorPane currentlyViewing;

    @FXML
    private Label defenseLevelLabel;

    @FXML
    private Hyperlink homeQuarter;

    @FXML
    private Label property;

    @FXML
    private Label propertyDefenceLevel;

    @FXML
    private Button robVaultBtn;

    @FXML
    private Label vaultValue;

    @FXML
    private Label walletInfo;

    @FXML
    void changeCurrentToAuth(ActionEvent event) {

    }

    @FXML
    void executeAuthorityBattle(ActionEvent event) {

    }

    @FXML
    void executeDuel(ActionEvent event) {


    }
    private void changeCurrent(Person ally) {
        setCharacters(ally.getCharacter());
        updateLabels();
        updatePlayersPerspective();
    }

    @FXML
    void getAllies(ActionEvent event) {
        Set<Person> persons = relations.getAllies();
        ObservableList<Node> items = FXCollections.observableArrayList();

        populateList(persons, items, "allies");
    }
    @FXML
    void getDefeats(ActionEvent event) {
        Set<Person> persons = relations.getListOfDefeats();
        ObservableList<Node> items = FXCollections.observableArrayList();
        populateList(persons, items, "defeats");
    }
    @FXML
    void getEnemies(ActionEvent event) {
        Set<Person> persons = relations.getEnemies();
        ObservableList<Node> items = FXCollections.observableArrayList();
        populateList(persons, items, "enemies");
    }
    @FXML
    void getSentinels(ActionEvent event) {
        Set<Person> persons = relations.getListOfSentinels();
        ObservableList<Node> items = FXCollections.observableArrayList();
        populateList(persons, items,"sentinels");
    }
    @FXML
    void getSubordinates(ActionEvent event) {
        Set<Person> persons = relations.getListOfSubordinates();
        ObservableList<Node> items = FXCollections.observableArrayList();
        populateList(persons, items, "subordinates");
    }
    @FXML
    void getVictories(ActionEvent event) {
        Set<Person> persons = relations.getListOfDefeatedPersons();
        ObservableList<Node> items = FXCollections.observableArrayList();
        populateList(persons, items, "victories");
    }

    private void populateList(Set<Person> persons, ObservableList<Node> items, String listName) {
        if (persons.isEmpty()) {
            Label nothingHereLabel = new Label("No " + listName);
            items.add(nothingHereLabel);
        }else {
            for (Person person : persons) {
                Hyperlink hyperlink = new Hyperlink(person.getCharacter().toString());
                hyperlink.setOnAction(e -> {
                    changeCurrent(person);
                });
                items.add(hyperlink);
            }
        }
        charactersList.setItems((ObservableList) items);
    }

    @FXML
    void openHomeQuarter(ActionEvent event) {

    }

    @FXML
    void robVault(ActionEvent event) {

    }

    public void setCharacterController(CharacterController characterController) {
        this.characterController = characterController;
    }
}
