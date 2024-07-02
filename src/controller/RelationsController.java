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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Model;
import model.characters.Character;
import model.characters.Person;
import model.characters.RelationsManager;
import model.characters.combat.CombatService;
import model.stateSystem.SpecialEventsManager;

import java.util.Set;

public class RelationsController extends BaseController {


    public void setCurrentCharacter(Character currentCharacter) {
        this.currentCharacter = currentCharacter;
        setUpCurrentCharacter();
    }
    private CharacterController characterController;


    private Character currentCharacter; // Character whose relations we are looking at. CURRENT CHARACTER ALWAYS REFERS TO THE ONE IN CHARACTER CONTROLLER
    protected Character current;  //Character that might be or is selected for inspection.
    private Person currentPerson;
    private RelationsManager relations;

    void setCharacters(Character character){
        this.current = character;
        this.currentPerson = character.getPerson();
    }


    @FXML
    private HBox CombatBox;

    @FXML
    private Label attackLevelLabel;

    void updateLabels(){
        currentlyViewing.setVisible(true);
        compareToPlayer.setVisible(true);
        attackLevelLabel.setText("Level: "+currentPerson.getCombatStats().getOffense().getUpgradeLevel());
        defenseLevelLabel.setText("Level: "+currentPerson.getCombatStats().getDefense().getUpgradeLevel());
        characterStatus.setText(current.getRole().getStatus().toString());
        property.setText(currentPerson.getProperty().getClass().getSimpleName());
        propertyDefenceLevel.setText("Defence: "+currentPerson.getProperty().getDefenceStats().getUpgradeLevel());
        vaultValue.setText(currentPerson.getProperty().getVault().toShortString());
        walletInfo.setText(currentPerson.getWallet().toShortString());
        homeQuarter.setText(currentPerson.getProperty().getLocation().toString());
        changeCurrentToThisHyper.setText(currentPerson.toString());
        compareToCharacter.setText(currentCharacter.getPerson().getRelationsManager().getRelationshipDescription(currentPerson));
        authority.setText(current.getRole().getAuthority().toString());
    }

    public void setUpCurrentCharacter() {
        currentCharacterName.setText(currentCharacter.toString());
        this.relations = currentCharacter.getPerson().getRelationsManager();
    }

    void updatePlayersPerspective(){
        if(Model.getPlayerAsPerson() == currentPerson){
            compareToPlayer.setText("Currently viewing yourself");
        }else {
            compareToPlayer.setText("For you they are:\n"+Model.getPlayerAsPerson().getRelationsManager().getRelationshipDescription(currentPerson));
        }
    }

    public void resetEverything(){
        currentlyViewing.setVisible(false);
        compareToPlayer.setVisible(false);
        current = null;
        clearCharacterView();
    }

    void clearCharacterView() {
        ObservableList<?> items = charactersList.getItems();
        items.clear();
    }
    @FXML
    private Label compareToCharacter;

    @FXML
    private Hyperlink authority;

    @FXML
    private Hyperlink changeCurrentToThisHyper;

    @FXML
    private ImageView characterPicture;

    @FXML
    private Label characterStatus;

    @FXML
    private ListView<?> charactersList;

    @FXML
    private Label compareToPlayer;



    @FXML
    private Label currentCharacterName;

    @FXML
    private VBox currentlyViewing;

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
        characterController.setCurrentCharacter(current.getRole().getAuthority().getCharacterInThisPosition());
        main.mainTabPane.getSelectionModel().select(main.characterTab);
    }

    @FXML
    void executeAuthorityBattle(ActionEvent event) {
        CombatService.executeAuthorityBattle(Model.getPlayerAsCharacter(), current);
    }

    @FXML
    void executeDuel(ActionEvent event) {
        CombatService.executeDuel(Model.getPlayerAsCharacter(), current);

    }
    private void changeCurrent(Person person) {
        setCharacters(person.getCharacter());
        updateLabels();
        updatePlayersPerspective();
        setCharacterPicture();
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
        SpecialEventsManager.triggerSentinelsInfo();
        currentCharacter.getPerson().getRelationsManager().updateSets();
        Set<Person> persons = relations.getListOfSentinels();
        ObservableList<Node> items = FXCollections.observableArrayList();
        populateList(persons, items,"sentinels");
    }
    @FXML
    void getSubordinates(ActionEvent event) {
        currentCharacter.getPerson().getRelationsManager().updateSets();
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
        model.accessCurrentView().setCurrentView(current.getPerson().getProperty().getLocation());
        main.exploreMapController.updateExploreTab();
    }
    @FXML // CHANGES CHARACTER IN CHARACTER CONTROLLER TO THE CHARACTER CURRENTLY IN VIEW
    void changeCharacter(ActionEvent event) {
        characterController.setCurrentCharacter(current);
        main.mainTabPane.getSelectionModel().select(main.characterTab);
    }

    @FXML
    void robVault(ActionEvent event) {
        CombatService.executeRobbery(Model.getPlayerAsCharacter(), current);
    }

    public void setCharacterController(CharacterController characterController) {
        this.characterController = characterController;
    }


    private void setCharacterPicture(){

        if(current.getPerson().getCharacterPic() == 0){
            current.getPerson().generatePicture();
        }

        Image image;
        String number = String.valueOf(current.getPerson().getCharacterPic());
        String imagePath = String.format("/characterImages/%s.png", number);
        image = new Image(imagePath);

        characterPicture.setImage(image);
    }

}
