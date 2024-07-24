package model.characters;

import model.characters.authority.Authority;
import model.characters.authority.QuarterAuthority;
import model.stateSystem.Event;
import model.stateSystem.GameEvent;
import model.time.EventManager;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class RelationsManager {

    private final CopyOnWriteArraySet<Person> allies = new CopyOnWriteArraySet<>();
    private final CopyOnWriteArraySet<Person> enemies = new CopyOnWriteArraySet<>();
    private final Set<Person> listOfDefeatedPersons = new HashSet<>();
    private final Set<Person> listOfDefeats = new HashSet<>();
    private final Set<Person> listOfSentinels = new HashSet<>(); //SENTINELS ARE SUPPORTS
    private final Set<Person> listOfSubordinates = new HashSet<>();
    private final Person owner;
    private final HashMap<Status, Person> authorities = new HashMap<>(); // Unique authorities

    private final HashSet<Person> duelTruceList = new HashSet<>();
    private Person latestTarget;

    public RelationsManager(Person owner) {
        this.owner = owner;
    }

    public void updateSets(){
        listOfSentinels.clear();
        listOfSubordinates.clear();
        authorities.clear();

        addSubordinates();
        addSentinels();
        addAuthorities();
    }

    private void addAuthorities(){
        Person captain = owner.getProperty().getLocation().getAuthorityHere().getCharacterInThisPosition().getPerson();
        Person mayor = owner.getProperty().getLocation().getCity().getAuthorityHere().getCharacterInThisPosition().getPerson();
        Person governor = owner.getProperty().getLocation().getCity().getProvince().getAuthorityHere().getCharacterInThisPosition().getPerson();
        Person king = owner.getProperty().getLocation().getCity().getProvince().getNation().getAuthorityHere().getCharacterInThisPosition().getPerson();

        if(captain!=owner){
            authorities.put(Status.Captain, captain);
        }
        if(mayor!=owner){
            authorities.put(Status.Mayor, mayor);
        }
        if(governor!=owner){
            authorities.put(Status.Governor, governor);
        }
        if(king!=owner){
            authorities.put(Status.King, king);
        }
    }

    private void addSubordinates(){

        Authority position = owner.getRole().getPosition();

        if(position == null){
            return;
        }

        if(position instanceof QuarterAuthority){
            ((QuarterAuthority) position).getPeasants().forEach(peasant -> listOfSubordinates.add(peasant.getPerson()));
        }
        position.getSubordinate().forEach(subordinate -> listOfSubordinates.add(subordinate.getCharacterInThisPosition().getPerson()));
    }
    private void addSentinels(){


        Authority position = owner.getRole().getPosition();

        if(position == null){
            return;
        }

        if(owner.getRole().getPosition().getSupporters() == null){
            return;
        }
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
     * Processes results. Must be called by the WINNER. Both managers are updated
     * @param loser must be loser inserted here.
     */
    public void processResults(Person loser) {
        listOfDefeatedPersons.add(loser);
        loser.getRelationsManager().addDefeat(owner);
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
        for (Person enemy : person.getRelationsManager().getEnemies()) {
            addAlly(enemy);
        }
        for (Person ally : person.getRelationsManager().getAllies()) {
            addEnemy(ally);
        }
    }
    public void addEnemiesEnemiesAsAllies(Person person) {
        for (Person enemy : person.getRelationsManager().getEnemies()) {
            addEnemy(enemy);
        }
        for (Person ally : person.getRelationsManager().getAllies()) {
            addAlly(ally);
        }
    }

    public String getRelationshipDescription(Person person) {
        List<String> relationshipDescriptions = new ArrayList<>();
        List<String> roleDescriptions = new ArrayList<>();

        // Relationships
        if (allies.contains(person)) {
            relationshipDescriptions.add("ally");
        }
        if (listOfSentinels.contains(person)) {
            relationshipDescriptions.add("sentinel");
        }
        if (listOfSubordinates.contains(person)) {
            relationshipDescriptions.add("subordinate");
        }
        if (enemies.contains(person)) {
            relationshipDescriptions.add("enemy");
        }
        if (listOfDefeatedPersons.contains(person)) {
            relationshipDescriptions.add("one you have defeated");
        }
        if (listOfDefeats.contains(person)) {
            relationshipDescriptions.add("one who has defeated you");
        }

        // Authorities
        for (Map.Entry<Status, Person> entry : authorities.entrySet()) {
            if (entry.getValue().equals(person)) {
                roleDescriptions.add(entry.getKey().toString().toLowerCase()); // Assuming toString() returns a readable format
            }
        }

        StringBuilder description = new StringBuilder();
        if (!relationshipDescriptions.isEmpty()) {

            description.append(String.join("\n", relationshipDescriptions));
            if (!roleDescriptions.isEmpty()) {
                description.append("\n");
            }
        }
        if (!roleDescriptions.isEmpty()) {
            description.append(String.join("\n", roleDescriptions));
        }

        return !description.isEmpty() ? description.toString() + "" : "No specific relationship found.";
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
        synchronized (this) {
            return new HashSet<>(allies);
        }
    }
    public Set<Person> getEnemies() {
        try {
            synchronized (this) {
                return new HashSet<>(enemies);
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
    public Set<Person> getListOfDefeatedPersons() {
        return listOfDefeatedPersons;
    }


    /**
     * Losing duel makes it impossible for the loser to attack the winner for a while.
     * @param thisPerson person who owns the relationsManager
     * @param opponent person they lost the duel to
     */
    public void addLostDuel(Person thisPerson, Person opponent){
        if(opponent == thisPerson.getRole().getAuthority().getCharacterInThisPosition().getPerson()){
            return; // if authority duels this person, duelTruce won't apply
        }
        duelTruceList.add(opponent);
        GameEvent gameEvent = new GameEvent(Event.DUEL_TRUCE, thisPerson);

        EventManager.scheduleEvent(() -> removePersonFromTruceDuels(opponent), 1080, gameEvent);
    }

    private void removePersonFromTruceDuels(Person opponent){
        this.duelTruceList.remove(opponent);
    }

    /**
     * @param opponent Opponent who might be in the truce list
     * @return returns true if opponent is in the list, owner of this manager cannot enter the battle
     */
    public boolean checkForTruce(Person opponent) {
        return duelTruceList.contains(opponent);
    }

    public Person getLatestTarget() {
        return latestTarget;
    }

    public void setLatestTarget(Person latestTarget) {
        this.latestTarget = latestTarget;
    }

}
