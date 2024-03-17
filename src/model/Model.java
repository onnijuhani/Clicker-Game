package model;

import model.characters.Character;
import model.characters.Person;
import model.characters.player.PlayerAuthorityCharacter;
import model.characters.player.PlayerPeasant;
import model.characters.player.clicker.Clicker;
import model.map.CurrentPosition;
import model.map.CurrentView;
import model.time.Time;
import model.worldCreation.CreateWorld;

public class Model {

    private static final CreateWorld world = new CreateWorld();
    private static CurrentPosition currentPosition = new CurrentPosition();
    private static CurrentView currentView = new CurrentView();
    private static final Time time = new Time();

    private static Person playerPerson = new Person(false);
    private static PlayerPeasant initialCharacter = new PlayerPeasant(world.getSpawnQuarter(), playerPerson);
    private static PlayerAuthorityCharacter authorityCharacter = null;



    public Model(){


        currentPosition.updateCurrentQuarter(world.getSpawnQuarter());

        currentView.setCurrentView(world.getSpawnQuarter().getHigher());

        Clicker.initializeClicker(playerPerson);
    }


    public Person getPlayerPerson() {
        return playerPerson;
    }

    public static void setInitialCharacter(PlayerPeasant initialCharacter) {
        Model.initialCharacter = initialCharacter;
    }
    public static void setAuthorityCharacter(PlayerAuthorityCharacter authorityCharacter) {
        setInitialCharacter(null);
        Model.authorityCharacter = authorityCharacter;
    }

    public void setPlayer(Person player) {
        Model.playerPerson = player;
    }


    public Time accessTime() {
        return time;
    }

    public CreateWorld accessWorld() {
        return world;
    }
    public CurrentPosition accessCurrentPosition() {
        return currentPosition;
    }
    public CurrentView accessCurrentView() {
        return currentView;
    }
    public Character accessCharacter() {
        if (initialCharacter != null) {
            return initialCharacter;
        } else {
            return authorityCharacter;
        }
    }


    public Person accessPerson(){
        return playerPerson;
    }

    public static PlayerPeasant getInitialCharacter() {
        return initialCharacter;
    }

    public static PlayerAuthorityCharacter getAuthorityCharacter() {
        return authorityCharacter;
    }



}

