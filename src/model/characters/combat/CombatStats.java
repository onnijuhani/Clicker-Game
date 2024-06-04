package model.characters.combat;


import model.characters.Person;
import model.resourceManagement.Resource;
import model.shop.UpgradeSystem;
import model.stateSystem.EventTracker;

public class CombatStats {

    /* Tried using Composition over Inheritance here to reduce code */
    private final UpgradeSystem offense;
    private final UpgradeSystem defense;
    private final Person person;

    public CombatStats(int offenseBasePrice, int defenseBasePrice, Person person) {
        this.offense = new UpgradeSystem(offenseBasePrice,2000);
        this.defense = new UpgradeSystem(defenseBasePrice,2000);
        this.person = person;
    }

    public void upgradeOffenseWithGold(){
        int price = getOffense().getUpgradePrice();
        if(person.getWallet().hasEnoughResource(Resource.Gold,price)){
            person.getWallet().subtractGold(price);
            if(getOffense().increaseLevel()) {
                person.getEventTracker().addEvent(EventTracker.Message("Utility", "Offence was increased"));
            }else{ // useless because UI doesn't allow the button to be pressed anyway. // even more useless now
                person.getEventTracker().addEvent(EventTracker.Message("Error", "Offence is already at max level"));
            }
        }else{
            person.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough Gold to increase offence"));
        }
    }

    public void upgradeDefenceWithGold(){
        int price = getDefense().getUpgradePrice();
        if(person.getWallet().hasEnoughResource(Resource.Gold,price)){
            person.getWallet().subtractGold(price);
            getDefense().increaseLevel();
            person.getEventTracker().addEvent(EventTracker.Message("Utility",  "Defence was increased"));
        }else{
            person.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough Gold to increase defence"));
        }
    }

    public void increaseOffence(){
        getOffense().increaseLevel();
    }
    public void increaseDefence(){
        getDefense().increaseLevel();
    }

    public void decreaseOffense(){
        if(offense.getUpgradeLevel() == 1){
            return;
        }
        getOffense().decreaseLevel();
    }
    public void decreaseDefence(){
        if(defense.getUpgradeLevel() == 1){
            return;
        }
        getDefense().decreaseLevel();
    }


    public int getOffenseLevel(){
        return offense.getUpgradeLevel();
    }
    public int getDefenseLevel(){
        return defense.getUpgradeLevel();
    }

    public UpgradeSystem getOffense() {
        return offense;
    }

    public UpgradeSystem getDefense() {
        return defense;
    }

}
