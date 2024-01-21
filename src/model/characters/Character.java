package model.characters;

import model.NameCreation;
import model.characters.player.EventTracker;
import model.resourceManagement.wallets.WorkWallet;
import model.worldCreation.Nation;
import model.TimeEventManager;
import model.TimeObserver;
import model.buildings.Property;
import model.characters.npc.Slave;
import model.resourceManagement.wallets.Wallet;

import java.util.LinkedList;

public class Character implements TimeObserver {
    protected static int totalAmount;
    protected LinkedList<Slave> slaves;
    protected  Nation nation;
    public String name;
    protected Wallet wallet;
    protected WorkWallet workWallet;
    protected Property property;
    protected LinkedList<Character> allies;
    protected LinkedList<Character> enemies;
    protected EventTracker eventTracker;
    public Character() {
        this.wallet = new Wallet();
        this.slaves = new LinkedList<>();
        this.allies = new LinkedList<>();
        this.enemies = new LinkedList<>();
        this.name = NameCreation.generateCharacterName();
        this.eventTracker = new EventTracker();
        TimeEventManager.subscribe(this);
    }
    public String getName() {
        return name;
    }
    public Nation getNation() {
        return nation;
    }
    public void setNation(Nation nation) {
        this.nation = nation;
    }
    @Override
    public void timeUpdate(int day, int week, int month, int year) {
    }

    @Override
    public String toString() {
        return name + "  Main House: " + property;
    }
    public void setProperty(Property property){
        this.property = property;
    }
    public Property getProperty(){
        return property;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void addSlave(Slave slave){
        slaves.add(slave);
    }
    public void deleteSlave(Slave slave){
        slaves.remove(slave);
    }
    public void addAlly(Character ally){
        allies.add(ally);
    }
    public void deleteAlly(Character ally){
        allies.remove(ally);
    }
    public void addEnemy(Character enemy){
        enemies.add(enemy);
    }
    public void deleteEnemy(Character enemy){
        enemies.remove(enemy);
    }
    public EventTracker getEventTracker() {
        return eventTracker;
    }
    public void setEventTracker(EventTracker eventTracker) {
        this.eventTracker = eventTracker;
    }
    public WorkWallet getWorkWallet() {
        return workWallet;
    }
    public void setWorkWallet(WorkWallet workWallet) {
        this.workWallet = workWallet;
    }
}


