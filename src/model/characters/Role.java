package model.characters;

import model.characters.authority.Authority;
import model.worldCreation.Nation;

public class Role implements RoleBasedAttributes {
    private Nation nation;
    private Authority authority;
    private Status status;

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    private Person person;
    private Character character;


    protected Authority authorityPosition;

    public Role() {
    }

    @Override
    public String toString() {
        return getStatus().toString();
    }

    @Override
    public Nation getNation() {
        return nation;
    }

    @Override
    public void setNation(Nation nation) {
        this.nation = nation;
    }

    @Override
    public Authority getAuthority() {
        return authority;
    }

    @Override
    public void setAuthority(Authority authority) {
        this.authority = authority;
    }
    @Override
    public Status getStatus() {
        return status;
    }
    @Override
    public void setStatus(Status status) {
        this.status = status;
    }
    public Authority getAuthorityPosition() {
        return authorityPosition;
    }

    public void setAuthorityPosition(Authority authorityPosition) {
        this.authorityPosition = authorityPosition;
    }

    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
    }






}
