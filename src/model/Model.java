package model;

import model.characters.player.Player;
import model.map.CurrentPosition;
import model.map.CurrentView;
import model.time.Time;
import model.worldCreation.CreateWorld;

public class Model {

    private final CreateWorld world;
    private CurrentPosition currentPosition;
    private CurrentView currentView;
    private Player player;

    private Time time;



    public Model(){
        this.world = new CreateWorld();
        this.currentPosition = new CurrentPosition();
        currentPosition.updateCurrentQuarter(world.getSpawnQuarter());
        this.currentView = new CurrentView();
        currentView.setCurrentView(world.getSpawnQuarter().getHigher());
        this.player = new Player(world.getSpawnQuarter());
        this.time = new Time();
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
    public Player accessPlayer() {
        return player;
    }



}

