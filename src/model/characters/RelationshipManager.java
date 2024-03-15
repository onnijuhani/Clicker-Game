package model.characters;

import java.util.HashSet;
import java.util.Set;

public class RelationshipManager {
    private final Set<Character> allies = new HashSet<>();
    private final Set<Character> enemies = new HashSet<>();
    private final Set<Character> listOfDefeatedCharacters = new HashSet<>();
    public void addDefeatedCharacter(Character character) {
        listOfDefeatedCharacters.add(character);
    }
    public void addAlly(Character character) {
        if (enemies.contains(character)) {
            enemies.remove(character);
        } else {
            allies.add(character);
        }
    }
    public void addEnemy(Character character) {
        if (allies.contains(character)) {
            allies.remove(character);
        } else {
            enemies.add(character);
        }
    }
    public void addEnemiesAlliesAsEnemies(Character character) {
        for (Character enemy : character.getRelationshipManager().getEnemies()) {
            addAlly(enemy);
        }
        for (Character ally : character.getRelationshipManager().getAllies()) {
            addEnemy(ally);
        }
    }
    public void addEnemiesEnemiesAsAllies(Character character) {
        for (Character enemy : character.getRelationshipManager().getEnemies()) {
            addEnemy(enemy);
        }
        for (Character ally : character.getRelationshipManager().getAllies()) {
            addAlly(ally);
        }
    }


    public void removeAlly(Character character) {
        allies.remove(character);
    }
    public void removeEnemy(Character character) {
        enemies.remove(character);
    }
    public boolean isAlly(Character character) {
        return allies.contains(character);
    }
    public boolean isEnemy(Character character) {
        return enemies.contains(character);
    }
    public Set<Character> getAllies() {
        return new HashSet<>(allies); // Return a copy to prevent external modifications
    }
    public Set<Character> getEnemies() {
        return new HashSet<>(enemies); // Same here
    }

    public Set<Character> getListOfDefeatedCharacters() {
        return listOfDefeatedCharacters;
    }
}
