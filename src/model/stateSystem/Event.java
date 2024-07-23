package model.stateSystem;

public enum Event {
    ROBBERY("Robbery"),
    DUEL("Duel"),
    CONSTRUCTION("Construction"),
    AuthorityBattle("Authority Battle"),
    SupportBattle("Support Battle"),
    RecruitSoldier("Recruit Soldier"),
    ArmyTraining("Army Training"),
    SABOTEUR("Saboteur"),
    TRUCE("Truce"),
    CHALLENGE_WAITING("Challenge Waiting"),
    GRAND_FOUNDRY_UNDER_OCCUPATION("Grand foundry under occupation"),
    GRAND_FOUNDRY("Grand foundry being Constructed"),
    DUEL_TRUCE("Has lost a duel lately"),
    WAR_ENDING_SOON("War is ending soon"),
    NOBLE_BONUS("Noble bonus active");


    private final String displayName;

    Event(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
