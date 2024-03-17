package model.characters;

import java.util.HashSet;
import java.util.Set;

public class RelationshipManager {
    private final Set<Person> allies = new HashSet<>();
    private final Set<Person> enemies = new HashSet<>();
    private final Set<Person> listOfDefeatedPersons = new HashSet<>();
    public void addDefeatedPerson(Person person) {
        listOfDefeatedPersons.add(person);
    }
    public void addAlly(Person person) {
        if (enemies.contains(person)) {
            enemies.remove(person);
        } else {
            allies.add(person);
        }
    }
    public void addEnemy(Person person) {
        if (allies.contains(person)) {
            allies.remove(person);
        } else {
            enemies.add(person);
        }
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
