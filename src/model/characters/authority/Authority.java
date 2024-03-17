package model.characters.authority;

import model.buildings.Property;
import model.characters.AuthorityCharacter;
import model.characters.Character;
import model.characters.Support;
import model.shop.Ownable;
import model.stateSystem.EventTracker;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.payments.Salary;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.wallets.WorkWallet;
import model.time.TaxEventManager;
import model.time.TaxObserver;

import java.util.ArrayList;

public class Authority implements TaxObserver, Ownable {

    @Override
    public void taxUpdate(int day, int month, int year) {
        System.out.println("Kuka tätä kutsui??"+this);
        if (day == 0) {
            imposeTax();
            paySupporters();
        }
    }

    protected Property property;
    protected Character characterPositionedHere; //the character who is in this position
    protected ArrayList<Authority> subordinate;
    protected Authority supervisor;
    protected ArrayList<Support> supporters;
    protected Tax taxForm;

    protected WorkWallet workWallet;

    public void subscribeToTimeEvents() {
        TaxEventManager.subscribe(this);
    }

    public Authority(Character characterPositionedHere) {
        this.characterPositionedHere = characterPositionedHere;
        this.taxForm = new Tax();
        this.workWallet = characterPositionedHere.getPerson().getWorkWallet();
        this.subordinate = new ArrayList<>();
        this.supporters = new ArrayList<>();
        this.property = characterPositionedHere.getProperty();

        setInitialCharacterToThisPosition();

        subscribeToTimeEvents();
    }

    /**
     Initial character to this role must be AuthorityCharacter, later anyone can become one.
     */
    private void setInitialCharacterToThisPosition(){
        ((AuthorityCharacter) characterPositionedHere).setAuthorityPosition(this);
    }

    public void imposeTax(){
        for (Authority authority : subordinate){
            WorkWallet walletUnderTaxation = authority.getWorkWallet();
            EventTracker tracker = authority.getCharacterInThisPosition().getEventTracker();
            taxForm.collectTax(walletUnderTaxation,tracker,workWallet,this.getCharacterInThisPosition().getEventTracker());
        }
    }

    private WorkWallet getWorkWallet() {
        return characterPositionedHere.getPerson().getWorkWallet();
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
        String characterName = this.characterPositionedHere.getName();
        String characterClass = this.characterPositionedHere.getRole().getStatus().toString();
        return characterClass+" "+characterName;
    }
    public Property getProperty() {
        return property;
    }
    public void setProperty(Property property) {
        this.property = property;
    }
    public Character getCharacterInThisPosition() {
        return characterPositionedHere;
    }

    public void setCharacterToThisPosition(Character character) {
        this.characterPositionedHere = character;
    }
    public ArrayList<Authority> getSubordinate() {
        return subordinate;
    }

    public void removeSubordinate(Authority authority) {
        subordinate.remove(authority);
    }

    public void addSubordinate(Authority authority) {
        subordinate.add(authority);
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

    @Override
    public EventTracker getEventTracker() {
        return characterPositionedHere.getEventTracker();
    }
}


