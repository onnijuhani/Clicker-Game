package model.characters.combat;


import model.characters.Person;
import model.resourceManagement.Resource;
import model.shop.UpgradeSystem;
import model.stateSystem.EventTracker;

public class CombatStats {

    private final UpgradeSystem offense;
    private final UpgradeSystem defense;
    private final Person person;

    public CombatStats(int offenseBasePrice, int defenseBasePrice, Person person) {
        this.offense = new UpgradeSystem(offenseBasePrice,2000, 1.5);
        this.defense = new UpgradeSystem(defenseBasePrice,2000,1.5);
        this.person = person;
    }

    public boolean upgradeOffenseWithGold(){
        int price = getOffense().getUpgradePrice();
        if(person.getWallet().hasEnoughResource(Resource.Gold,price)){
            person.getWallet().subtractGold(price);
            getOffense().increaseLevel();
            person.getEventTracker().addEvent(EventTracker.Message("Utility", "Offence was increased"));
            return true;
        }else{
            person.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough Gold to increase offence"));
            return false;
        }
    }

    public boolean upgradeDefenceWithGold(){
        int price = getDefense().getUpgradePrice();
        if(person.getWallet().hasEnoughResource(Resource.Gold,price)){
            person.getWallet().subtractGold(price);
            getDefense().increaseLevel();
            person.getEventTracker().addEvent(EventTracker.Message("Utility",  "Defence was increased"));
            return true;
        }else{
            person.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough Gold to increase defence"));
            return false;
        }
    }

    public void increaseOffence(int amount){
        for(int i = 0; i<amount; i++) {
            getOffense().increaseLevel();
        }
    }
    public void increaseDefence(int amount){
        for(int i = 0; i<amount; i++) {
            getDefense().increaseLevel();
        }
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
