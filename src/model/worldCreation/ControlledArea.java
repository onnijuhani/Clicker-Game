package model.worldCreation;

import model.characters.Status;
import model.characters.authority.Authority;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ControlledArea extends Area implements Details {
    public Authority getAuthority() {
        return authority;
    }
    protected Nation nation;
    Authority authority;
    public Nation getNation(){
        return nation;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void addKingToName() {
        this.name += " (King)";
    }

    @NotNull
    public List<Status> getImportantStatusRank() {
        List<Status> statusOrder = List.of(
                Status.King, Status.Noble, Status.Vanguard,
                Status.Governor, Status.Mercenary, Status.Mayor
                //doesn't include unimportant ranks
        );
        return statusOrder;
    }

    @NotNull
    public List<Status> getStatusRank() {
        List<Status> statusOrder = List.of(
                Status.King, Status.Noble, Status.Vanguard,
                Status.Governor, Status.Mercenary, Status.Mayor,
                Status.Captain, Status.Merchant, Status.Miner,
                Status.Farmer, Status.Slave
        );
        return statusOrder;
    }

}
