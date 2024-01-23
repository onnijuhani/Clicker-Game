package model.characters.player;


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
import time.Time;

import java.util.ArrayList;

public class Player extends Character {
    @Override
    public void timeUpdate(int day, int month, int year) {
        if (day == Time.provinceTax) {
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
        this.property = new Shack("Your Own");
        this.property.setLocation(spawn);
        this.property.setOwner(this);
        this.wallet = new Wallet();
        this.workWallet = new WorkWallet();
        this.eventTracker = new EventTracker();
        this.setNation(spawn.getAuthority().getCharacter().getNation());
        this.clicker = new Clicker(this);
        setSupervisor(spawn.getAuthority());
    }


    public void payTaxes(){
        if (status == Status.Peasant) {
            Tax taxForm = getSupervisor().getTaxForm();
            Wallet supervisorWallet = getSupervisor().getCharacter().getWallet();
            EventTracker supervisorTracker = getSupervisor().getCharacter().getEventTracker();
            taxForm.collectTax(workWallet, eventTracker, supervisorWallet, supervisorTracker);
            workWallet.setTaxedOrNot(true);
            cashOutSalary();
        }
    }

    public void cashOutSalary() {
        wallet.depositAll(workWallet);
        String message = EventTracker.Message("Major","Salary added to main wallet");
        eventTracker.addEvent(message);
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











