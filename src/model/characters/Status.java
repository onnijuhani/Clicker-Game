package model.characters;

public enum Status {
    Noble,
    Vanguard,
    Mercenary,
    Slave,
    Farmer,
    Miner,
    Merchant,
    King,
    Governor,
    Mayor,
    Captain,
    Peasant;

    public boolean isAuthority() {
        return this == King || this == Governor || this == Mayor || this == Captain;
    }

    public boolean isSentinel() {
        return this == Vanguard || this == Noble || this == Mercenary;
    }

    public boolean isCivilian() {
        return !(isAuthority() || isSentinel());
    }

    public boolean isCommander() {
        return (isAuthority() && this != King) || this == Mercenary;
    }

    public boolean isRoyal() {
        return  this == King || this == Vanguard || this == Noble;
    }

    public boolean isCitadelWorthy() { return this.isRoyal() || isSentinel() || this == Mayor || this == Governor;}
    public boolean isFortressWorthy() { return this.isRoyal() || this == Governor;}

}
