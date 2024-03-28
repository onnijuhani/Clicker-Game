package model.characters.ai.actions;

import model.characters.Person;
import model.characters.Trait;

import java.util.Map;

public class WarActions {
    private Person person;
    private final Map<Trait, Integer> profile;

    public WarActions(Person person, Map<Trait, Integer> profile) {
        this.person = person;
        this.profile = profile;
    }
}
