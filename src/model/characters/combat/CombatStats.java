package model.characters.combat;


import model.shop.UpgradeSystem;

public class CombatStats {

    /* Tried using Composition over Inheritance here to reduce code */
    private final UpgradeSystem offense;
    private final UpgradeSystem defense;

    public CombatStats(int offenseBasePrice, int defenseBasePrice) {
        this.offense = new UpgradeSystem(offenseBasePrice);
        this.defense = new UpgradeSystem(defenseBasePrice);
    }

    public void upgradeOffense() {
        offense.upgradeLevel();
    }

    public void upgradeDefense() {
        defense.upgradeLevel();
    }

    public UpgradeSystem getOffense() {
        return offense;
    }

    public UpgradeSystem getDefense() {
        return defense;
    }

}
