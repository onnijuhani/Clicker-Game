package model.characters;

import model.characters.authority.Authority;
import model.worldCreation.Nation;

public class Role implements RoleBasedAttributes {
    private Nation nation;
    private Authority authority;
    private Status status;
    protected Authority position;
    private Person person;
    private Character character;

    public Role(Status status) {
        this.status = status;
    }
    public Character getCharacter() {
        return character;
    }
    public void setCharacter(Character character) {
        this.character = character;
    }
    public Person getPerson() {
        return person;
    }
    public void setPerson(Person person) {
        this.person = person;
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
    @Override
    public void setPosition(Authority authority) {
        this.position = authority;
    }
    public Authority getPosition() {
        return position;
    }

}
