package model.characters.combat;


import model.characters.Character;
import model.stateSystem.EventTracker;
import model.resourceManagement.Resource;
import model.shop.UpgradeSystem;

public class CombatStats {

    /* Tried using Composition over Inheritance here to reduce code */
    private final UpgradeSystem offense;
    private final UpgradeSystem defense;
    private final Character character;

    public CombatStats(int offenseBasePrice, int defenseBasePrice, Character character) {
        this.offense = new UpgradeSystem(offenseBasePrice);
        this.defense = new UpgradeSystem(defenseBasePrice);
        this.character = character;
    }

    public void upgradeOffense(){
        int price = getOffense().getUpgradePrice();
        if(character.getWallet().hasEnoughResource(Resource.Gold,price)){
            character.getWallet().subtractGold(price);
            getOffense().upgradeLevel();
            character.getEventTracker().addEvent(EventTracker.Message("Utility",  "Offence was increased"));
        }else{
            character.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough Gold to increase offence"));
        }
    }

    public void upgradeDefence(){
        int price = getDefense().getUpgradePrice();
        if(character.getWallet().hasEnoughResource(Resource.Gold,price)){
            character.getWallet().subtractGold(price);
            getDefense().upgradeLevel();
            character.getEventTracker().addEvent(EventTracker.Message("Utility",  "Defence was increased"));
        }else{
            character.getEventTracker().addEvent(EventTracker.Message("Error", "Not enough Gold to increase defence"));
        }
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
