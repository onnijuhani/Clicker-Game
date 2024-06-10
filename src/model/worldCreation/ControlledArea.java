package model.worldCreation;

import model.characters.Character;
import model.characters.Status;
import model.characters.authority.Authority;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ControlledArea extends Area implements Details {
    public Authority getAuthorityHere() {
        return authorityHere;
    }
    protected Nation nation;

    public void setAuthorityHere(Authority authorityHere, Character character) {
        this.authorityHere = authorityHere;
        authorityHere.setCharacterToThisPosition(character);
    }

    protected Authority authorityHere;
    protected List<Character> citizenCache = null;
    public Nation getNation(){
        return nation;
    }
    public abstract void setNation(Nation nation);

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
                Status.Captain, Status.Merchant, Status.Miner, Status.Farmer, Status.Peasant
        );
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
                .filter(character -> statusOrder.contains(character.getRole().getStatus()))
                .sorted(Comparator.comparingInt(character -> statusOrder.indexOf(character.getRole().getStatus())))
                .collect(Collectors.toList());
    }

    protected void onCitizenUpdate() {
        updateCitizenCache();
    }

}
