package model.worldCreation;

import model.characters.Character;
import model.characters.Status;
import model.characters.authority.Authority;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ControlledArea extends Area implements Details {
    public Authority getAuthority() {
        return authority;
    }
    protected Nation nation;
    protected Authority authority;
    protected List<Character> citizenCache = null;
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


    public List<Status> getImportantStatusRank() {
        return List.of(
                Status.King, Status.Noble, Status.Vanguard,
                Status.Governor, Status.Mercenary,
                Status.Mayor,
                Status.Captain, Status.Merchant, Status.Miner, Status.Farmer
        );
    }



    public List<Status> getStatusRank() {
        List<Status> statusOrder = List.of(
                Status.King, Status.Noble, Status.Vanguard,
                Status.Governor, Status.Mercenary, Status.Mayor,
                Status.Captain, Status.Merchant, Status.Miner,
                Status.Farmer, Status.Slave
        );
        return statusOrder;
    }


    public List<Character> getImportantCharacters() {
        if (citizenCache == null) {
            updateCitizenCache();
        }
        return citizenCache;
    }

    protected void updateCitizenCache() {

        List<Character> characters = new ArrayList<>();

        List<Status> statusOrder = getImportantStatusRank();
        citizenCache = characters.stream()
                .filter(character -> statusOrder.contains(character.getStatus()))
                .sorted(Comparator.comparingInt(character -> statusOrder.indexOf(character.getStatus())))
                .collect(Collectors.toList());
    }

    protected void onCitizenUpdate() {
        updateCitizenCache();
    }

}
