package model.characters.player;


import model.buildings.properties.Shack;
import model.characters.Character;
import model.characters.PersonalDetails;
import model.characters.Status;
import model.characters.player.clicker.Clicker;
import model.resourceManagement.payments.Tax;
import model.resourceManagement.wallets.WorkWallet;
import model.stateSystem.EventTracker;
import model.time.Time;
import model.worldCreation.Quarter;

public class Player extends Character {
    @Override
    public void taxUpdate(int day, int month, int year) {
        if (day == Time.quarterTax) {
            payTaxes();
        }
    }
    private PersonalDetails personalDetails;
    private final WorkWallet workWallet;
    private final Clicker clicker;


    private Status status = Status.Peasant;

    public Player(Quarter spawn){
        this.personalDetails = new PersonalDetails(false);

        getWallet().setGold(1000000000);
        getWallet().setFood(1000000000);
        getWallet().setAlloy(1000000000);


        this.workWallet = new WorkWallet(getWallet());
        this.clicker = new Clicker(this);
        setAuthority(spawn.getAuthority());
        setNation(spawn.getAuthority().getCharacter().getNation());
        setProperty(new Shack("Your Own", this));
        getProperty().setLocation(spawn);
    }


    public void payTaxes(){
        if (status == Status.Peasant) {
            Tax taxForm = getAuthority().getTaxForm();
            WorkWallet supervisorWallet = getAuthority().getWorkWallet();
            EventTracker supervisorTracker = getAuthority().getCharacter().getEventTracker();
            taxForm.collectTax(workWallet, getEventTracker(), supervisorWallet, supervisorTracker);
        }
    }

    public Clicker getClicker() {
        return clicker;
    }

    @Override
    protected boolean shouldSubscribeToNpcEvent() {
        return false;
    }

    public WorkWallet getWorkWallet() {
        return workWallet;
    }

    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }

    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }


}











