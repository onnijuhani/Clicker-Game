package model.worldCreation;

public enum AreaState {

    /**
     * Nation that currently claims the area and holds full authority rights.
     */
    CLAIMED,

    /**
     * Nation that has waged the war against the claiming nation.
     * AT_WAR happens if an area is claimed by a nation, and then another one declares war on them.
     * This affects every area the nation controls even if the particular area is not under attack yet.
     */
    AT_WAR,

    /**
     * Nation that has waged the war against the claiming nation.
     * UNDER_ATTACK happens if the area is at war and one or more military buildings are under siege.
     * If none of the military buildings are at war, this should be null, otherwise the nation should be the attacker.
     */
    UNDER_ATTACK,

    /**
     * Nation that has waged the war against the claiming nation.
     * UNDER_OCCUPATION happens if 50% or more military buildings are occupied, otherwise null.
     * If 100% of them are occupied, this one goes to null and CLAIMED needs to change.
     */
    UNDER_OCCUPATION;

    private Nation nation;

    AreaState() {
    }

    public Nation getNation() {
        return nation;
    }

    public void setNation(Nation nation) {
        this.nation = nation;
    }

    public static AreaState determineState(Nation claimingNation, int militaryBuildings, int occupiedBuildings, int buildingsUnderSiege) {
        if (occupiedBuildings >= militaryBuildings) {
            return CLAIMED;
        } else if (occupiedBuildings >= militaryBuildings * 0.5) {
            AreaState state = UNDER_OCCUPATION;
            state.setNation(claimingNation);
            return state;
        } else if (buildingsUnderSiege > 0) {
            AreaState state = UNDER_ATTACK;
            state.setNation(claimingNation);
            return state;
        } else if (claimingNation != null) {
            AreaState state = AT_WAR;
            state.setNation(claimingNation);
            return state;
        } else {
            return CLAIMED;
        }
    }
}

