package model.characters.authority;

import model.buildings.Property;
import model.characters.AuthorityCharacter;
import model.characters.Character;
import model.characters.Support;
import model.characters.player.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.payments.Salary;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.wallets.WorkWallet;
import time.TimeEventManager;
import time.TimeObserver;

import java.util.ArrayList;

public class Authority implements TimeObserver {

    @Override
    public void timeUpdate(int day, int month, int year) {
        System.out.println("Kuka tätä kutsui??"+this);
        if (day == 0) {
            imposeTax();
            paySupporters();
            cashOutSalary();
        }
    }
    protected Property property;
    protected AuthorityCharacter character;
    protected ArrayList<Authority> subordinate;
    protected Authority supervisor;
    protected ArrayList<Support> supporters;
    protected Tax taxForm;

    protected WorkWallet workWallet;

    public void subscribeToTimeEvents() {
        TimeEventManager.subscribe(this);
    }

    public Authority(Character character) {
        this.character = (AuthorityCharacter) character;
        this.taxForm = new Tax();
        this.workWallet = new WorkWallet();
        this.subordinate = new ArrayList<>();
        this.supporters = new ArrayList<>();
        this.property = character.getProperty();
        subscribeToTimeEvents();
    }
    public void imposeTax(){
        for (Authority authority : subordinate){
            WorkWallet taxedWallet = authority.getWorkWallet();
            EventTracker tracker = authority.getCharacter().getEventTracker();
            taxForm.collectTax(taxedWallet,tracker,workWallet,this.getCharacter().getEventTracker());
            taxedWallet.setTaxedOrNot(true);
        }
    }

    public void cashOutSalary() {
        getCharacter().getWallet().depositAll(workWallet);
        workWallet.setTaxedOrNot(false);
    }

    public void paySupporters(){
        for (Support support : getSupporters()) {
            Salary salary = support.getSalary();
            TransferPackage transfer = TransferPackage.fromArray(salary.getAll());
            support.getWallet().deposit(workWallet, transfer);
        }
    }
    @Override
    public String toString(){
        String characterName = this.character.getName();
        String characterClass = this.character.getClass().getSimpleName();
        return characterClass+" "+characterName;
    }
    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }
    public Character getCharacter() {
        return character;
    }
    public void setCharacter(AuthorityCharacter character) {
        this.character = character;
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
    public ArrayList<Support> getSupporters() {
        return supporters;
    }
    public void addSupporter(Support support) {
        this.supporters.add(support);
    }
    public Tax getTaxForm() {
        return taxForm;
    }
    public void setTaxForm(Tax taxForm) {
        this.taxForm = taxForm;
    }
    public WorkWallet getWorkWallet() {
        return workWallet;
    }

    public void setWorkWallet(WorkWallet workWallet) {
        this.workWallet = workWallet;
    }
}


