package model.characters.ai.actions;

import model.characters.Person;
import model.characters.Trait;
import model.characters.ai.actionCircle.WeightedObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class BaseActions {
    protected final Person person;
    protected final Map<Trait, Integer> profile;
    protected final List<WeightedObject> allActions = new LinkedList<>();
    protected final NPCActionLogger npcActionLogger;

    public BaseActions(Person person, NPCActionLogger npcActionLogger, Map<Trait, Integer> profile) {
        this.person = person;
        this.npcActionLogger = npcActionLogger;
        this.profile = profile;
        createAllActions();
    }

    protected abstract void createAllActions();
}
