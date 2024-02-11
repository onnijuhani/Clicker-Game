package model.characters.player;


import model.NameCreation;
import model.buildings.Property;
import model.buildings.Shack;
import model.characters.Character;
import model.characters.Status;
import model.characters.authority.Authority;
import model.characters.player.clicker.Clicker;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.wallets.Wallet;
import model.resourceManagement.wallets.WorkWallet;
import model.worldCreation.Quarter;
import model.time.Time;

import java.util.ArrayList;

public class Player extends Character {
    @Override
    public void taxUpdate(int day, int month, int year) {
        if (day == Time.quarterTax) {
            payTaxes();
        }
    }
    private Property property;
    private Wallet wallet;

    private WorkWallet workWallet;
    private String name;
    private EventTracker eventTracker;
    private Clicker clicker;
    private Authority supervisor;
    private ArrayList<Authority> subordinate;
    private Status status = Status.Peasant;

    public Player(Quarter spawn){
        setAuthority(spawn.getAuthority());
        this.wallet = new Wallet();
        wallet.setGold(10000);
        wallet.setFood(10000);
        wallet.setAlloy(10000);
        this.property = new Shack("Your Own");
        this.property.setLocation(spawn);
        this.property.setOwner(this);
        this.workWallet = new WorkWallet(wallet);
        this.eventTracker = new EventTracker();
        this.setNation(spawn.getAuthority().getCharacter().getNation());
        this.clicker = new Clicker(this);
        setSupervisor(spawn.getAuthority());
        this.name = NameCreation.generateCharacterName();
    }


    public void payTaxes(){
        if (status == Status.Peasant) {
            Tax taxForm = getSupervisor().getTaxForm();
            WorkWallet supervisorWallet = getSupervisor().getWorkWallet();
            EventTracker supervisorTracker = getSupervisor().getCharacter().getEventTracker();
            taxForm.collectTax(workWallet, eventTracker, supervisorWallet, supervisorTracker);
        }
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
    public ArrayList<Authority> getSubordinate() {
        return subordinate;
    }
    public void setSubordinate(Authority authority) {
        subordinate.add(authority);
    }
    public Authority getSupervisor() {
        return supervisor;
    }
    public void setSupervisor(Authority supervisor) {
        this.supervisor = supervisor;
    }
    public WorkWallet getWorkWallet() {
        return workWallet;
    }
    public void setWorkWallet(WorkWallet workWallet) {
        this.workWallet = workWallet;
    }
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}











