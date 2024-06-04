package model.buildings;


import javafx.scene.image.Image;
import model.Images;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Person;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Vault;
import model.shop.Ownable;
import model.shop.UpgradeSystem;
import model.stateSystem.EventTracker;
import model.stateSystem.State;
import model.time.PropertyManager;
import model.time.PropertyObserver;
import model.worldCreation.Details;
import model.worldCreation.Quarter;

import java.util.EnumSet;

public class Property implements PropertyObserver, Details, Ownable {

    @Override
    public void propertyUpdate() {
        if (owner == null){
            /*
            first time reached allows 1 maintenance free month at the start of the game
            property under construction appears here and shouldn't give free month when ready
            this is set in Construction also
            */
            firstTimeReached = false;
            return;
        }
        if (firstTimeReached) {
                // Skip the first model.time this condition is met
                firstTimeReached = false;

        } else {
                maintenance.payMaintenance(this);
        }

        maintenance.updatePaymentCalendar(owner.getPaymentManager());

    }
    @Override
    public String getDetails(){
        return (this.getClass().getSimpleName() + " " + getName());
    }
    @Override
    public String toString() {
        return name;
    }

    private boolean firstTimeReached = true;
    private UpgradeSystem defense;
    protected Vault vault;
    protected Quarter location;
    protected Person owner;
    protected Maintenance maintenance;
    protected String name;
    protected Properties propertyEnum;
    protected UtilitySlot utilitySlot;
    private final EnumSet<State> states;

    public Property(PropertyConfig.PropertyValues propertyValues, String name, Person owner) {
        this.defense = new UpgradeSystem(50,500);
        this.vault = new Vault(this);

        owner.setProperty(this);

        depositStartingBalance(propertyValues);

        this.name = name;
        this.owner = owner;
        this.maintenance = new Maintenance(propertyValues);
        PropertyManager.subscribe(this);
        this.utilitySlot = new UtilitySlot(5);
        states = EnumSet.noneOf(State.class);

    }

    private void depositStartingBalance(PropertyConfig.PropertyValues propertyValues) {
        vault.setFood(propertyValues.food);
        vault.setAlloy(propertyValues.alloy);
        vault.setGold(propertyValues.gold);
    }

    public Image getImage() {
        return Images.PropertyImg.getImage(propertyEnum);
    }

    public Person getOwner() {
        return owner;
    }

    public void setOwner(Person owner) {
        this.owner = owner;
        owner.setProperty(this);
    }

    public void upgradeDefenceWithGold(){
        int price = getDefenceStats().getUpgradePrice();
        if(owner.getWallet().hasEnoughResource(Resource.Alloy,price)){
            owner.getWallet().subtractAlloy(price);
            getDefenceStats().increaseLevel();
            owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName()+"'s defence was increased"));
        }else{
            if(owner.isPlayer()){
                owner.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough alloys to increase property defence"));
            }
        }
    }
    public Quarter getLocation() {
        return location;
    }

    public void setLocation(Quarter location) {
        this.location = location;
        location.addProperty(this);
    }
    public boolean hasLocation() {
        return location != null;
    }
    public void setFirstTimeReached(boolean firstTimeReached) {
        this.firstTimeReached = firstTimeReached;
    }
    public String getName() {
        return name;
    }
    public Maintenance getMaintenance() {
        return maintenance;
    }
    public void setMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
    }
    public UtilitySlot getUtilitySlot() {
        return utilitySlot;
    }
    public void setUtilitySlot(UtilitySlot utilitySlot) {
        this.utilitySlot = utilitySlot;
    }
    public void setVault(Vault vault) {
        this.vault = vault;
    }
    public Vault getVault() {
        return vault;
    }
    public UpgradeSystem getDefenceStats() {
        return defense;
    }
    public void setDefense(UpgradeSystem defense) {
        this.defense = defense;
    }

    public EnumSet<State> getStates() {
        return states.clone();
    }

    public void addState(State state) {
        states.add(state);
    }
    public void removeState(State state) {
        states.remove(state);
    }
    public boolean hasState(State state) {
        return states.contains(state);
    }
    public void clearStates() {
        states.clear();
    }
    public Properties getPropertyEnum() {
        return propertyEnum;
    }

    @Override
    public EventTracker getEventTracker(){
        return owner.getEventTracker();
    }
}
