package model.stateSystem;

public enum Event {
    ROBBERY("Robbery"),
    DUEL("Duel"),
    CONSTRUCTION("Construction"),
    AuthorityBattle("Authority Battle"),
    SupportBattle("Support Battle"),
    RecruitSoldier("Recruit Soldier"),
    ArmyTraining("Army Training"),
    TRUCE("truce");

    private final String displayName;

    Event(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
