package model.characters;

import model.TimeEventManager;
import model.TimeObserver;
import model.buildings.Property;
import model.characters.authority.AuthorityCharacter;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.payments.Salary;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.wallets.Wallet;
import model.worldCreation.Details;

import java.util.ArrayList;

public class Authority implements TimeObserver, Details {

    @Override
    public void timeUpdate(int day, int week, int month, int year) {
        if (day == 7 && week % 2 == 0) {
            imposeTax();
            paySupporters();
            System.out.println(this.getClass().getSimpleName() + " + " + character.wallet);
        }
    }
    protected Property property;
    protected AuthorityCharacter character;
    protected ArrayList<Authority> authOver;
    protected Authority authUnder;
    protected ArrayList<Support> supporters;
    protected Tax taxForm;

    public void subscribeToTimeEvents() {
        TimeEventManager.subscribe(this);
    }

    public Authority(Character character) {
        this.character = (AuthorityCharacter) character;
        this.taxForm = new Tax();
        this.authOver = new ArrayList<>();
        this.supporters = new ArrayList<>();
        this.property = character.getProperty();
        subscribeToTimeEvents();
    }
    public void imposeTax(){
        for (Authority authority : authOver){
            Wallet wallet = authority.getCharacter().getWallet();
            taxForm.collectTax(wallet,this.getCharacter().getWallet());
        }
    }
    public void paySupporters(){
        for (Support support : getSupporters()) {
            Salary salary = support.getSalary();
            TransferPackage transfer = TransferPackage.fromArray(salary.getAll());
            support.getWallet().withdrawal(character.getWallet(), transfer);
        }
    }
    public String getDetails(){
        String propertyName = this.property.getName();
        String characterName = this.character.getName();
        String characterClass = this.character.getClass().getSimpleName();
        return characterClass+" "+characterName+" living in a "+propertyName;
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
}


