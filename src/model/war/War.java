package model.war;

import model.characters.Person;
import model.resourceManagement.TransferPackage;
import model.stateSystem.State;
import model.time.Time;
import model.time.WarManager;
import model.time.WarObserver;
import model.worldCreation.Details;
import model.worldCreation.Nation;

import java.util.*;
import java.util.function.BiConsumer;

import static model.war.War.Phase.*;

public class War implements WarObserver, Details {
    @Override
    public void warUpdate(int day) {
        try {
            this.days ++;
            if(days < 360){
                return;
            }
            startPhase1();
            if(day == 23 || day == 8){
                finalPhaseActions();
                royalsMatchMaking(day);
                updateSets();
                updateOnGoingBattles();
                matchAndPlay(day);
                updatePhase();
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }

    @Override
    public String getDetails() {
        StringBuilder s = new StringBuilder("Current phase: " + currentPhase + "\n" +
                "Attacker Militaries In Play: " + attackerMilitariesInPlay.size() + "\n" +
                "Defender Militaries In Play: " + defenderMilitariesInPlay.size() +  "\n" +
                "Attacker Militaries In Battle: " + attackerMilitariesInBattle.size() +  "\n" +
                "Defender Militaries In Battle: " + defenderMilitariesInBattle.size() +  "\n" +
                "Attacker Defeated Militaries: " + attackerDefeatedMilitaries.size() + "\n" +
                "Defender Defeated Militaries: " + defenderDefeatedMilitaries.size() + "\n" +
                "On Going Battles: " + onGoingBattles.size() + "\n");

        if(currentPhase == PHASE3){
            s.append("Attacker Royals: ").append(attackerRoyals.size()).append("\n").append("Defender Royals: ").append(defenderRoyals.size());
        }

        return s.toString();
    }

    public War(Nation attacker, Nation defender, String warName) {
        this.attacker = attacker;
        this.defender = defender;
        startPreparing();
        WarManager.subscribe(this);
        generateStartingNote(warName);
    }

    public enum Phase {
        PREPARING, PHASE1, PHASE2, PHASE3, ENDED, WAITING,
    }

    private final WarNotes warNotes = new WarNotes();

    private int days = 0;
    private int phase3StartingDay;

    private Phase currentPhase;
    private final Nation attacker; // Attacker means the nation that started the war. Otherwise, there is no difference.
    private final Nation defender;


    private final HashSet<Military> attackerMilitariesInPlay = new HashSet<>(); // Militaries that are available to send into battle against others
    private final HashSet<Military> defenderMilitariesInPlay = new HashSet<>();

    private final HashSet<Military> attackerMilitariesInBattle = new HashSet<>(); // Militaries that are currently in battle
    private final HashSet<Military> defenderMilitariesInBattle = new HashSet<>();

    private final HashSet<Military> attackerDefeatedMilitaries = new HashSet<>(); // Militaries that are out of the war completely
    private final HashSet<Military> defenderDefeatedMilitaries = new HashSet<>();

    private final HashSet<Military> attackerRoyals = new HashSet<>(); // Militaries that are out of the war completely
    private final HashSet<Military> defenderRoyals = new HashSet<>();

    private final ArrayList<MilitaryBattle> onGoingBattles = new ArrayList<>();

    private boolean aWarTax = false; // attacker war tax set
    private boolean dWarTax = false; // defender war tax set

    private enum SetName {
        IN_PLAY, IN_BATTLE, DEFEATED, ROYALS
    }

    private void deleteFromCorrectList(Military military, SetName setName) {
        Nation nation = military.getOwner().getRole().getNation();
        switch (setName) {
            case IN_PLAY:
                if (nation == attacker) {
                    attackerMilitariesInPlay.remove(military);
                } else if (nation == defender) {
                    defenderMilitariesInPlay.remove(military);
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
                break;
            case IN_BATTLE:
                if (nation == attacker) {
                    attackerMilitariesInBattle.remove(military);
                } else if (nation == defender) {
                    defenderMilitariesInBattle.remove(military);
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
                break;
            case DEFEATED:
                if (nation == attacker) {
                    attackerDefeatedMilitaries.remove(military);
                } else if (nation == defender) {
                    defenderDefeatedMilitaries.remove(military);
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
                break;
            case ROYALS:
                if (nation == attacker) {
                    attackerRoyals.remove(military);
                } else if (nation == defender) {
                    defenderRoyals.remove(military);
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + setName);
        }
    }
    private void addIntoCorrectList(Military military, SetName setName) {
        Nation nation = military.getOwner().getRole().getNation();
        switch (setName) {
            case IN_PLAY:
                if (nation == attacker) {
                    addIntoAttackerList(military, attackerMilitariesInPlay);
                } else if (nation == defender) {
                    addIntoDefenderList(military, defenderMilitariesInPlay);
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
                break;
            case IN_BATTLE:
                if (nation == attacker) {
                    addIntoAttackerList(military, attackerMilitariesInBattle);
                } else if (nation == defender) {
                    addIntoDefenderList(military, defenderMilitariesInBattle);
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
                break;
            case DEFEATED:
                if (nation == attacker) {
                    addIntoAttackerList(military, attackerDefeatedMilitaries);
                } else if (nation == defender) {
                    addIntoDefenderList(military, defenderDefeatedMilitaries);
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
                break;
            case ROYALS:
                if (nation == attacker) {
                    addIntoAttackerList(military, attackerRoyals);
                } else if (nation == defender) {
                    addIntoDefenderList(military, defenderRoyals);
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + setName);
        }
    }

    private void addIntoAttackerList(Military military, Set<Military> attackerList) {
        if (military.getOwner().getRole().getNation() != this.attacker) {
            throw new RuntimeException("Tried to add into wrong list: Attacker list expected.");
        }

        if (!(attackerList instanceof HashSet<?> && (attackerList == attackerMilitariesInPlay
                || attackerList == attackerMilitariesInBattle
                || attackerList == attackerDefeatedMilitaries
                || attackerList == attackerRoyals))) {
            String listName = getDefenderListName(attackerList);
            throw new RuntimeException("Invalid attacker list provided: " + listName);
        }
        attackerList.add(military);
    }


    private String getDefenderListName(Set<Military> list) {
        if (list == defenderMilitariesInPlay) return "defenderMilitariesInPlay";
        if (list == defenderMilitariesInBattle) return "defenderMilitariesInBattle";
        if (list == defenderDefeatedMilitaries) return "defenderDefeatedMilitaries";
        if (list == defenderRoyals) return "defenderRoyals";
        return "unknown list";
    }

    private void addIntoDefenderList(Military military, Set<Military> defenderList) {
        if (military.getOwner().getRole().getNation() != this.defender) {
            throw new RuntimeException("Tried to add into wrong list: Defender list expected.");
        }

        if (!(defenderList instanceof HashSet<?> && (defenderList == defenderMilitariesInPlay
                || defenderList == defenderMilitariesInBattle
                || defenderList == defenderDefeatedMilitaries
                || defenderList == defenderRoyals))) {
            String listName = getAttackerListName(defenderList);
            throw new RuntimeException("Invalid defender list provided: " + listName);
        }
        defenderList.add(military);
    }


    private String getAttackerListName(Set<Military> list) {
        if (list == attackerMilitariesInPlay) return "attackerMilitariesInPlay";
        if (list == attackerMilitariesInBattle) return "attackerMilitariesInBattle";
        if (list == attackerDefeatedMilitaries) return "attackerDefeatedMilitaries";
        if (list == attackerRoyals) return "attackerRoyals";
        return "unknown list";
    }


    private void finalPhaseActions(){
        if(currentPhase != PHASE3){
            return;
        }

        sendResourceAidToRoyals(attacker, attackerRoyals);
        sendResourceAidToRoyals(defender, defenderRoyals);
    }

    private void sendResourceAidToRoyals(Nation nation, HashSet<Military> royals) {
        if(nation.getWallet().isEmpty()){
            return;
        }
        TransferPackage amountPerRoyal =  nation.getWallet().getBalance().divide(royals.size() + 1);
        System.out.println(nation.getWallet());
        for(Military military : royals){
            military.getOwner().getWallet().addResources(amountPerRoyal);

        }
    }

    private void checkAndAddWarTax(double adr, double ddr) {
        if(adr > 0.6 && !aWarTax){
            attacker.setWarTaxToWorkWallets();
            aWarTax = true;
        }
        if(ddr > 0.6 && !dWarTax){
            defender.setWarTaxToWorkWallets();
            dWarTax = true;
        }
    }


    private void updatePhase() {
        if (currentPhase != PHASE1){
            if (testForEmpty(attackerDefeatedMilitaries, defenderDefeatedMilitaries)) return;
        }

        double adr = getDefeatRatio(attackerDefeatedMilitaries, attackerMilitariesInPlay, attackerMilitariesInBattle);
        double ddr = getDefeatRatio(defenderDefeatedMilitaries, defenderMilitariesInPlay, defenderMilitariesInBattle);

        if(currentPhase == PHASE1) {
            if(adr > 0.6){
                startPhase2(defender + " has defeated 60% of the opponents civilian armies.");
            }
            if(ddr > 0.6){
                startPhase2(attacker + " has defeated 60% of the opponents civilian armies.");
            }
            return;
        }

        checkAndAddWarTax(adr, ddr); // war tax is only set after certain amount of opponents armies have been defeated


        if(currentPhase == PHASE2) {
            if(adr >= 0.9){
                startPhase3(defender + " has defeated 90% of the opponents commander armies.");
            }
            if(ddr >= 0.9){
                startPhase3(attacker + " has defeated 90% of the opponents commander armies.");
            }
            return;
        }

        if(currentPhase == PHASE3) {
            if(adr >= 0.98 && attackerRoyals.isEmpty()){
                System.out.println(defender + " has won the war against " + attacker);
                endWar("Loser", "Winner"); // Attacker has lost the offensive war

            }
            if(ddr >= 0.98 && defenderRoyals.isEmpty()){
                System.out.println(attacker + " has won the war against " + defender);
                endWar("Winner", "Loser"); // Attacker has won the offensive war

            }
        }




    }

    private static double getDefeatRatio(HashSet<Military> defeatedMilitaries, HashSet<Military> militariesInPlay, HashSet<Military> militariesInBattle) {
        return (double) defeatedMilitaries.size() / (defeatedMilitaries.size() + militariesInPlay.size() + militariesInBattle.size());
    }

    private static boolean testForEmpty(Collection<?>... collections) {
        for (Collection<?> collection : collections) {
            if (collection.isEmpty()) {
                return true;
            }
        }
        return false;
    }


    private void updateSets() {
        // Attacker
        filterArmies(attackerMilitariesInPlay, attackerDefeatedMilitaries, attackerMilitariesInBattle, (military, list) -> addIntoAttackerList(military, list));

        // Defender
        filterArmies(defenderMilitariesInPlay, defenderDefeatedMilitaries, defenderMilitariesInBattle, this::addIntoDefenderList);
    }



    private void filterArmies(Set<Military> militariesAvailable, Set<Military> defeatedMilitaries, Set<Military> militariesInBattle, BiConsumer<Military, Set<Military>> addMethod) {
        Set<Military> all = new HashSet<>(militariesAvailable);
        all.addAll(militariesInBattle);

        for (Military military : all) {
            Army.ArmyState state = military.getState();

            if (state == null) {
                militariesInBattle.remove(military);
                addMethod.accept(military, militariesAvailable);
                continue;
            }

            // If military is defeated, move to defeatedMilitaries
            if (state == Army.ArmyState.DEFEATED) {
                addMethod.accept(military, defeatedMilitaries);
                militariesAvailable.remove(military);
                militariesInBattle.remove(military);
                military.getOwner().removeState(State.ACTIVE_COMBAT);
                continue;
            }

            // If military is in battle, move to militariesInBattle
            if (state == Army.ArmyState.ATTACKING || state == Army.ArmyState.DEFENDING) {
                addMethod.accept(military, militariesInBattle);
                militariesAvailable.remove(military);
                continue;
            }
        }
    }


    /**
     * @param attacker whether attacker of the war is "Winner" or "Loser"
     * @param defender "Winner" or "Loser"
     */
    public void endWar(String attacker, String defender){

        if(Objects.equals(attacker, "Winner")) {
            generateWarReport(this.attacker);
        }else{
            generateWarReport(this.defender);
        }

        currentPhase = ENDED;

        this.attacker.endWar(this.defender, attacker, warNotes);
        this.defender.endWar(this.attacker, defender, warNotes);

        WarManager.unsubscribe(this);

        WarPlanningManager.filterNations(); // War Planning manager should be updated here
    }

    private void updateOnGoingBattles(){
        onGoingBattles.removeIf(battle -> !battle.isOnGoing());
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    public Nation getAttacker() {
        return attacker;
    }

    public Nation getDefender() {
        return defender;
    }

    // Automatic matchmaking and battles
    public void startPreparing() {
        setCurrentPhase(PREPARING);

        attacker.startWar(defender, this);
        defender.startWar(attacker, this);

        // Add civilian militaries to in play set
        attackerMilitariesInPlay.addAll(attacker.getMilitariesOwnedByCivilians());
        defenderMilitariesInPlay.addAll(defender.getMilitariesOwnedByCivilians());


    }
    public void startPhase1() {
        if(currentPhase == PREPARING){
            setCurrentPhase(PHASE1);

            if(attackerMilitariesInPlay.isEmpty()){
                startPhase2(attacker + " has no civilian armies. Phase 2 started.");
                return;
            }
            if(defenderMilitariesInPlay.isEmpty()){
                startPhase2(defender + " has no civilian armies. Phase 2 started.");
            }
        }
    }



    public void startPhase2(String message) {
        if(currentPhase == PHASE1) {
            System.out.println(message);

            reduceMilitaries(attackerMilitariesInPlay);
            reduceMilitaries(defenderMilitariesInPlay);

            attackerMilitariesInPlay.addAll(attacker.getMilitariesOwnedByCommanders());
            defenderMilitariesInPlay.addAll(defender.getMilitariesOwnedByCommanders());

            System.out.println("Commanders added");

        }

        setCurrentPhase(PHASE2);

    }

    private void reduceMilitaries(HashSet<Military> set) {
        if (set.size() > 50) {
            Iterator<Military> iterator = set.iterator();
            while (iterator.hasNext() && set.size() > 50) {
                iterator.next();
                iterator.remove();
            }
        }
    }

    public void startPhase3(String message) {
        if(currentPhase == PHASE2) {
            System.out.println(message);

            attackerRoyals.addAll(attacker.getMilitariesOwnedByKingAndHisSentinels());


            defenderRoyals.addAll(defender.getMilitariesOwnedByKingAndHisSentinels());

            setCurrentPhase(PHASE3);
            phase3StartingDay = days;
            finalPhaseActions();
        }

    }

    private void royalsMatchMaking(int day){
        if(currentPhase != PHASE3) return;
        if(days < phase3StartingDay + 180) return;

        testRoyalMilitary(attackerRoyals, attackerDefeatedMilitaries, this::addIntoAttackerList);
        testRoyalMilitary(defenderRoyals, defenderDefeatedMilitaries, this::addIntoDefenderList);

        if(day == 23) {
            matchMaking(attackerRoyals, defenderRoyals, day);
        }else{
            matchMaking(defenderRoyals, attackerRoyals, day);
        }
    }

    private void testRoyalMilitary(HashSet<Military> set, HashSet<Military> defeated, BiConsumer<Military, Set<Military>> addMethod) {
        Iterator<Military> iterator = set.iterator();
        while (iterator.hasNext()) {
            Military m = iterator.next();
            if (m.getState() == Army.ArmyState.DEFEATED) {
                iterator.remove();
                addMethod.accept(m, defeated);
            }
        }
    }
    private void matchAndPlay(int day) {
        if(day == 23) {
            matchMaking(attackerMilitariesInPlay, defenderMilitariesInPlay, day);
        }else{
            matchMaking(attackerMilitariesInPlay, defenderMilitariesInPlay, day);
        }
    }

    /**
     * @param aip Attacker Militaries In Play
     * @param dip Defender Militaries In Play
     */
    private void matchMaking(HashSet<Military> aip, HashSet<Military> dip, int day) {
        if (testForEmpty(aip, dip)) return;

        List<Military> attackerList = new ArrayList<>(aip);
        List<Military> defenderList = new ArrayList<>(dip);

        if (day == 8) {
            while (!attackerList.isEmpty() && !defenderList.isEmpty()) {
                // Handle the first pair of militaries
                handleBattle(attackerList.remove(0), defenderList.remove(0), true);

                // If lists are empty after the first battle, break the loop
                if (attackerList.isEmpty() || defenderList.isEmpty()) break;

                // Handle the second pair of militaries with roles reversed
                handleBattle(defenderList.remove(0), attackerList.remove(0), false);
            }
        }else{
            while (!attackerList.isEmpty() && !defenderList.isEmpty()) {
                // Handle the first pair of militaries
                handleBattle(defenderList.remove(0), attackerList.remove(0), false);

                // If lists are empty after the first battle, break the loop
                if (attackerList.isEmpty() || defenderList.isEmpty()) break;

                // Handle the second pair of militaries with roles reversed
                handleBattle(attackerList.remove(0), defenderList.remove(0), true);
            }
        }
    }

    private void handleBattle(Military attacker, Military defender, boolean isOriginalOrder) {
        if (testMilitariesForUndefeated(attacker, defender)) {
            if (attacker == defender) {
                throw new RuntimeException("Tried to attack self in war");
            }

            MilitaryBattle battle = SiegeService.executeMilitaryBattle(attacker.getOwner(), defender.getOwner());
            if (addMilitaryBattle(battle)) {
                deleteFromCorrectList(attacker, SetName.IN_PLAY);
                deleteFromCorrectList(defender, SetName.IN_PLAY);
                addIntoCorrectList(attacker, SetName.IN_BATTLE);
                addIntoCorrectList(defender, SetName.IN_BATTLE);
            }
        }
    }


    /**
     * @param battle battle to be added to the class list
     * @return returns false if battle is null, and true if battle started successfully
     */
    public boolean addMilitaryBattle(MilitaryBattle battle) {
        if(battle == null){
            return false;
        }
        onGoingBattles.add(battle);
        return true;
    }
    public ArrayList<MilitaryBattle> getOnGoingBattles() {
        return onGoingBattles;
    }




    /**
     * @param m military 1
     * @param m2 military 2
     * @return Returns true if either army is undefeated, false if either is defeated
     */
    private boolean testMilitariesForUndefeated(Military m, Military m2) {
        boolean isUndefeated = true;
        if(m.getState() == Army.ArmyState.DEFEATED){
            addIntoAttackerList(m, attackerDefeatedMilitaries);
            attackerMilitariesInPlay.remove(m);
            attackerMilitariesInBattle.remove(m);
            isUndefeated = false;
        }
        if(m2.getState() == Army.ArmyState.DEFEATED){
            defenderDefeatedMilitaries.add(m2);
            defenderMilitariesInPlay.remove(m2);
            defenderMilitariesInBattle.remove(m2);
            isUndefeated = false;
        }

        return isUndefeated;
    }

    public void manualCommand(Military attacker, Military defender) {
        if (currentPhase != PHASE2) return;
        // Implement manual command logic
        battle(attacker, defender);
    }

    private void battle(Military attacker, Military defender) {
        // Implement battle logic, including removing defeated armies
        // and updating eliminatedMilitaries list
    }

    public void finalAssault() {
        if (currentPhase != PHASE3) return;
        // Implement logic for final assault on the king and bodyguards
    }

    private void addWarNote(String note){
        warNotes.warLog.add(days + " " + note);
    }

    private void generateStartingNote(String warName){
        warNotes.warName = warName;
        addWarNote(warName + " started: " + Time.getClock());
    }

    private void generateWarReport(Nation winner){
        warNotes.attackingKing = attacker.getAuthorityHere().getCharacterInThisPosition().getPerson();
        warNotes.defendingKing = defender.getAuthorityHere().getCharacterInThisPosition().getPerson();
        warNotes.attacker = attacker;
        warNotes.defender = defender;
        warNotes.lastedForDays = days;
        warNotes.winner = winner;
        warNotes.totalBattles = attackerDefeatedMilitaries.size() + defenderDefeatedMilitaries.size();

    }

    public static class WarNotes {
        ArrayList<String> warLog = new ArrayList<>(); // messages here

        Nation attacker;
        Nation defender;
        Nation winner;
        int totalBattles;
        int lastedForDays;
        Person attackingKing;
        Person defendingKing;

        Person deadliestAttacker;
        Person deadliestDefender;

        String warName;



    }

}
