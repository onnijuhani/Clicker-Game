package model.characters.combat;


import model.shop.UpgradeSystem;

public class CombatPower {

    /* Tried using Composition over Inheritance here to reduce code */
    private final UpgradeSystem offense;
    private final UpgradeSystem defense;

    public CombatPower(int offenseBasePrice, int defenseBasePrice) {
        this.offense = new UpgradeSystem(offenseBasePrice);
        this.defense = new UpgradeSystem(defenseBasePrice);
    }

    public void upgradeOffense() {
        offense.upgrade();
    }

    public void upgradeDefense() {
        defense.upgrade();
    }

    public UpgradeSystem getOffense() {
        return offense;
    }

    public UpgradeSystem getDefense() {
        return defense;
    }

}
