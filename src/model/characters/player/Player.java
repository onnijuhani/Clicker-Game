package model.characters.player;


import model.buildings.Property;
import model.buildings.Shack;
import model.characters.Character;
import model.characters.Peasant;
import model.characters.authority.Authority;
import model.characters.authority.QuarterAuthority;
import model.characters.player.clicker.Clicker;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.worldCreation.Quarter;

import java.util.ArrayList;

public class Player extends Character {
    private Property property;
    private Wallet wallet;
    private WorkWallet workWallet;
    private String name;
    private EventTracker eventTracker;
    private Clicker clicker;
    private Authority authUnder;
    private ArrayList<Authority> authOver;
    private Peasant role;

    public Player(Quarter spawn){
        this.property = new Shack("Your Own");
        this.property.setLocation(spawn);
        this.property.setOwner(this);
        this.wallet = new Wallet();
        this.workWallet = new WorkWallet();
        this.eventTracker = new EventTracker();
        this.setNation(spawn.getAuthority().getCharacter().getNation());
        this.clicker = new Clicker(eventTracker, wallet);
        this.role = new Peasant();
        becomePeasant();

    }

    void becomePeasant(){
        role.setWorkWallet(workWallet);
        role.setEventTracker(eventTracker);
        role.setWallet(wallet);
        QuarterAuthority quarterAuthority = (QuarterAuthority) property.getLocation().getAuthority();
        quarterAuthority.addPeasant((Peasant) role);
    }



    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }
    public Wallet getWallet() {
        return wallet;
    }
    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public EventTracker getEventTracker() {
        return eventTracker;
    }
    public Clicker getClicker() {
        return clicker;
    }
    public ArrayList<Authority> getAuthOver() {
        return authOver;
    }
    public void setAuthOver(Authority authority) {
        authOver.add(authority);
    }
    public Authority getAuthUnder() {
        return authUnder;
    }
    public void setAuthUnder(Authority authUnder) {
        this.authUnder = authUnder;
    }
}











