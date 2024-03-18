package model.characters;

import model.characters.authority.Authority;
import model.characters.authority.QuarterAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RelationshipManager {
    private final Set<Person> allies = new HashSet<>();
    private final Set<Person> enemies = new HashSet<>();
    private final Set<Person> listOfDefeatedPersons = new HashSet<>();
    private final Set<Person> listOfDefeats = new HashSet<>();
    private final Set<Person> listOfSentinels = new HashSet<>(); //SENTINELS ARE SUPPORTS
    private final Set<Person> listOfSubordinates = new HashSet<>();

    private final Person owner;

    public RelationshipManager(Person owner) {
        this.owner = owner;
    }

    public void updateSets(){
        addSubordinates();
        addSentinels();
    }

    private void addSubordinates(){
        Authority position = owner.getRole().getPosition();
        if(position instanceof QuarterAuthority){
            ((QuarterAuthority) position).getPeasants().forEach(peasant -> listOfSubordinates.add(peasant.getPerson()));
        }
        position.getSubordinate().forEach(subordinate -> listOfSubordinates.add(subordinate.getCharacterInThisPosition().getPerson()));
    }
    private void addSentinels(){
        owner.getRole().getPosition().getSupporters().forEach(support -> listOfSentinels.add(support.getPerson()));
    }


    public void addAlly(Person personToBeAdded) {
        if (enemies.contains(personToBeAdded)) {
            enemies.remove(personToBeAdded);
        } else {
            allies.add(personToBeAdded);
        }
    }
    public void addEnemy(Person personToBeAdded) {
        if (allies.contains(personToBeAdded)) {
            allies.remove(personToBeAdded);
        } else {
            enemies.add(personToBeAdded);
        }
    }


    /**
     * Updates both managers
     */
    public void addVictory(Person person) {
        listOfDefeatedPersons.add(person);
        person.getRelationshipManager().addDefeat(person);
    }
    private void addDefeat(Person person){
        listOfDefeats.add(person);
    }

    public Set<Person> getListOfSentinels() {
        return listOfSentinels;
    }

    public Set<Person> getListOfSubordinates() {
        return listOfSubordinates;
    }


    public void addEnemiesAlliesAsEnemies(Person person) {
        for (Person enemy : person.getRelationshipManager().getEnemies()) {
            addAlly(enemy);
        }
        for (Person ally : person.getRelationshipManager().getAllies()) {
            addEnemy(ally);
        }
    }
    public void addEnemiesEnemiesAsAllies(Person person) {
        for (Person enemy : person.getRelationshipManager().getEnemies()) {
            addEnemy(enemy);
        }
        for (Person ally : person.getRelationshipManager().getAllies()) {
            addAlly(ally);
        }
    }

    public String getRelationshipDescription(Person person) {
        List<String> descriptions = new ArrayList<>();

        if (allies.contains(person)) {
            descriptions.add("he is your ally");
        }
        if (listOfSentinels.contains(person)) {
            descriptions.add("he is your sentinel");
        }
        if (listOfSubordinates.contains(person)) {
            descriptions.add("he is your subordinate");
        }
        if (enemies.contains(person)) {
            descriptions.add("he is your enemy");
        }
        if (listOfDefeatedPersons.contains(person)) {
            descriptions.add("you have defeated him");
        }
        if (listOfDefeats.contains(person)) {
            descriptions.add("he has defeated you");
        }

        return descriptions.isEmpty() ? "No specific relationship found." : String.join(", ", descriptions) + ".";
    }


    public Set<Person> getListOfDefeats() {
        return listOfDefeats;
    }

    public void removeAlly(Person person) {
        allies.remove(person);
    }
    public void removeEnemy(Person person) {
        enemies.remove(person);
    }
    public boolean isAlly(Person person) {
        return allies.contains(person);
    }
    public Set<Person> getAllies() {
        return new HashSet<>(allies); // Return a copy to prevent external modifications
    }
    public Set<Person> getEnemies() {
        return new HashSet<>(enemies); // Same here
    }

    public Set<Person> getListOfDefeatedPersons() {
        return listOfDefeatedPersons;
    }
}
