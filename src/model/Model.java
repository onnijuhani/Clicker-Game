package model;

import model.characters.Character;
import model.characters.Person;
import model.characters.Role;
import model.characters.player.clicker.Clicker;
import model.map.CurrentView;
import model.time.Time;
import model.worldCreation.CreateWorld;

public class Model {

    private static final CreateWorld world = new CreateWorld();
    private static final CurrentView currentView = new CurrentView();
    private static final Time time = new Time();

    private static Person playerPerson;
    private static Role playerRole;
    private static Character playerCharacter;

    public Model(){
        currentView.setCurrentView(world.getSpawnQuarter().getHigher());
        setUpPlayer();
        Clicker.initializeClicker(playerPerson);
    }

    private void setUpPlayer() {
        playerCharacter = world.getInitialPlayer();
        playerPerson = playerCharacter.getPerson();
        playerRole = playerCharacter.getRole();
    }

    public static void updatePlayer(){
        setPlayerCharacter(playerPerson.getCharacter());
        setPlayerRole(playerPerson.getRole());
    }

    public static Role getPlayerRole() {
        return playerRole;
    }
    public static void setPlayerRole(Role playerRole) {
        Model.playerRole = playerRole;
    }

    public Person getPlayerPerson(){
        return playerPerson;
    }
    public void setPlayerPerson(Person player) {
        Model.playerPerson = player;
    }
    public static void setPlayerCharacter(Character playerCharacter) {
        Model.playerCharacter = playerCharacter;
    }
    public Character getPlayerCharacter() {
        return playerCharacter;
    }
    public Time accessTime() {
        return time;
    }
    public CreateWorld accessWorld() {
        return world;
    }
    public CurrentView accessCurrentView() {
        return currentView;
    }






}

