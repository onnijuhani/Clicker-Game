package model;

import model.characters.player.Player;
import model.map.CurrentPosition;
import model.map.CurrentView;
import model.shop.Shop;
import model.worldCreation.*;

public class Model {
    private final CreateWorld world;
    private CurrentPosition currentPosition;
    private CurrentView currentView;
    private Player player;
    private Shop shop;
    private Time time;



    public Model(){
        this.world = new CreateWorld();
        this.currentPosition = new CurrentPosition();
        currentPosition.updateCurrentQuarter(world.getSpawnQuarter());
        this.currentView = new CurrentView();
        currentView.setCurrentView(world.getSpawnQuarter().getHigher());
        this.player = new Player(world.getSpawnQuarter());
        this.shop = new Shop();
        this.time = new Time();
    }

    public Shop accessShop(){
        return shop;
    }

    public Time accessTime() {
        return time;
    }

    public CreateWorld getWorld() {
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

