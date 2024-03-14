package model.buildings;


import javafx.scene.image.Image;
import model.Images;
import model.buildings.utilityBuilding.UtilitySlot;
import model.characters.Character;
import model.resourceManagement.Resource;
import model.resourceManagement.wallets.Vault;
import model.shop.UpgradeSystem;
import model.stateSystem.EventTracker;
import model.stateSystem.State;
import model.time.PropertyManager;
import model.time.PropertyObserver;
import model.worldCreation.Details;
import model.worldCreation.Quarter;
public class Property implements PropertyObserver, Details {

    @Override
    public void propertyUpdate() {
        if (owner == null){
            return;
        }
        if (firstTimeReached) {
                // Skip the first model.time this condition is met
                firstTimeReached = false;
        } else {
                maintenance.payMaintenance(this);
        }
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
    protected Character owner;
    protected Maintenance maintenance;
    protected String name;
    protected Properties propertyEnum;

    protected UtilitySlot utilitySlot;
    private State state = State.NONE;

    public Property(PropertyConfig.PropertyValues propertyValues, String name) {
        this.defense = new UpgradeSystem(propertyValues.getPower());
        this.vault = new Vault();

        depositStartingBalance(propertyValues);

        this.name = name;
        this.maintenance = new Maintenance(propertyValues);
        PropertyManager.subscribe(this);
        this.utilitySlot = new UtilitySlot(5);
    }

    private void depositStartingBalance(PropertyConfig.PropertyValues propertyValues) {
        vault.setFood(propertyValues.food);
        vault.setAlloy(propertyValues.alloy);
        vault.setGold(propertyValues.gold);
    }

    public Image getImage() {
        return Images.PropertyImg.getImage(propertyEnum);
    }

    public Character getOwner() {
        return owner;
    }

    public void setOwner(Character owner) {
        this.owner = owner;
        owner.setProperty(this);
    }

    public void upgradeDefence(){
        int price = getDefense().getUpgradePrice();
        if(owner.getWallet().hasEnoughResource(Resource.Alloy,price)){
            owner.getWallet().subtractAlloy(price);
            getDefense().upgradeLevel();
            owner.getEventTracker().addEvent(EventTracker.Message("Utility", this.getClass().getSimpleName()+"'s defence was increased"));
        }else{
            owner.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough alloys to increase property defence"));
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
    public UpgradeSystem getDefense() {
        return defense;
    }
    public void setDefense(UpgradeSystem defense) {
        this.defense = defense;
    }
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }
}
