package model.war;

import model.Model;
import model.Settings;
import model.characters.Person;
import model.characters.npc.King;
import model.resourceManagement.TransferPackage;
import model.resourceManagement.wallets.Wallet;
import model.stateSystem.Event;
import model.stateSystem.GameEvent;
import model.stateSystem.MessageTracker;
import model.stateSystem.State;
import model.time.EventManager;
import model.time.Time;
import model.time.WarManager;
import model.time.WarObserver;
import model.worldCreation.Details;
import model.worldCreation.Nation;

import java.util.*;
import java.util.function.BiConsumer;

import static model.stateSystem.SpecialEventsManager.triggerWarEnding;
import static model.war.War.Phase.*;

public class War implements WarObserver, Details {
    @Override
    public void warUpdate(int day) {
        try {
            this.days ++;
            if(forceContinuation()){
                if(currentPhase == ENDING_SOON){
                    if(onGoingBattles.isEmpty()) {
                        endWar();
                    }
                }
                return;
            }
            if(days < prepareTime){
                return;
            }
            startPhase1();
            if(day == 23 || day == 8){
                royalResourceAid();
                royalsMatchMaking(day);
                updateSets();
                updateOnGoingBattles();
                matchAndPlay(day);
                updatePhase();
                updateTotalPower();
            }
        } catch (Exception e) {
            e.printStackTrace();throw new RuntimeException(e);
        }
    }
    // returns true if war is over
    private boolean forceContinuation(){
        if(days == 500){
            startPhase2("Phase 2 started for war reaching 400 days.");
            return false;
        }
        if(days == 900){
            startPhase3("Phase 3 started for reaching 900 days.", null);
            return false;
        }
        if(days == 2000){
            double adr = getDefeatRatio(attackerDefeatedMilitaries, attackerMilitariesInPlay, attackerMilitariesInBattle);
            double ddr = getDefeatRatio(defenderDefeatedMilitaries, defenderMilitariesInPlay, defenderMilitariesInBattle);

            if(adr >= ddr){
                selectWinner("Loser", "Winner",defender + " has won the war against " + attacker); // Defender has won
            }else{
                selectWinner("Winner", "Loser", attacker + " has won the war against " + defender); // Attacker has won
            }
            return true;
        }
        return days > 2000;
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
        generateStartingNote(warName);
        startPreparing();
        WarManager.subscribe(this);
    }

    public enum Phase {
        PREPARING, PHASE1, PHASE2, PHASE3, ENDED, WAITING, ENDING_SOON
    }

    private final WarNotes warNotes = new WarNotes();
    private static final int prepareTime = 30;

    private int attackerTotalPower;
    private int defenderTotalPower;
    private Nation winner;
    private int days = 0;
    private int royalMatchmakingStartingDay;

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

    public enum SetName {
        IN_PLAY, IN_BATTLE, DEFEATED, ROYALS, ALL_IN_PLAY
    }

    private void updatePhase() {
        if (currentPhase != PHASE1){
            if (testForEmpty(attackerDefeatedMilitaries, defenderDefeatedMilitaries)) return;
        }

        double adr = getDefeatRatio(attackerDefeatedMilitaries, attackerMilitariesInPlay, attackerMilitariesInBattle);
        double ddr = getDefeatRatio(defenderDefeatedMilitaries, defenderMilitariesInPlay, defenderMilitariesInBattle);

        if(currentPhase == PHASE1) {
            if(adr > 0.5){
                startPhase2(defender + " has defeated 50% of the opponents civilian armies.");
                return;
            }
            if(ddr > 0.5){
                startPhase2(attacker + " has defeated 50% of the opponents civilian armies.");
                return;
            }
            return;
        }

        checkAndAddWarTax(adr, ddr); // war tax is only set after certain amount of opponents armies have been defeated


        if(currentPhase == PHASE2) {
            if(adr >= 0.9){
                startPhase3(defender + " has defeated 90% of the opponents commander armies.", attacker);
                return;
            }
            if(ddr >= 0.9){
                startPhase3(attacker + " has defeated 90% of the opponents commander armies.", defender);
                return;
            }
            return;
        }

        if(currentPhase == PHASE3) {
            if(adr >= 1 && attackerRoyals.isEmpty()){
                selectWinner("Loser", "Winner",defender + " has won the war against " + attacker); // Attacker has lost the offensive war
                winner = defender;
                return;
            }
            if(ddr >= 1 && defenderRoyals.isEmpty()){
                selectWinner("Winner", "Loser", attacker + " has won the war against " + defender); // Attacker has won the offensive war
                winner = attacker;
                return;
            }
        }

        if(currentPhase == ENDING_SOON){
            if(onGoingBattles.isEmpty()) {
                endWar();
            }
        }
    }

    private void endWar() {
        GameEvent gameEvent = new GameEvent(Event.WAR_ENDING_SOON);
        EventManager.scheduleEvent(() -> endMethod(winner), 10, gameEvent);
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

    public Set<Military> getCorrectSet(Nation nation, SetName setName) {
        switch (setName) {
            case IN_PLAY:
                if (nation == attacker) {
                    return attackerMilitariesInPlay;
                } else if (nation == defender) {
                    return defenderMilitariesInPlay;
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
            case IN_BATTLE:
                if (nation == attacker) {
                    return attackerMilitariesInBattle;
                } else if (nation == defender) {
                    return defenderMilitariesInBattle;
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
            case DEFEATED:
                if (nation == attacker) {
                    return attackerDefeatedMilitaries;
                } else if (nation == defender) {
                    return defenderDefeatedMilitaries;
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
            case ROYALS:
                if (nation == attacker) {
                    return attackerRoyals;
                } else if (nation == defender) {
                    return defenderRoyals;
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
            case ALL_IN_PLAY:
                if (nation == attacker) {
                    return getAllArmiesStillInPlay("Attacker");
                } else if (nation == defender) {
                    return getAllArmiesStillInPlay("Defender");
                } else {
                    throw new RuntimeException("Wrong Nationality part of the War");
                }
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

    private void updateTotalPower(){
        attackerTotalPower = Nation.countTotalMilitaryStrength(getCorrectSet(attacker, War.SetName.ALL_IN_PLAY));
        defenderTotalPower = Nation.countTotalMilitaryStrength(getCorrectSet(defender, War.SetName.ALL_IN_PLAY));
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


    private void royalResourceAid(){
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
        Wallet wallet = nation.getWallet();
        TransferPackage amountPerRoyal =  wallet.getBalance().divide(royals.size() + 2);
        for(Military military : royals){
            military.getOwner().getWallet().addResources(amountPerRoyal);
            wallet.subtractResources(amountPerRoyal);
            if(military.getOwner().isPlayer()){
                military.getOwner().getMessageTracker().addMessage(MessageTracker.Message("Minor", "War-aid received: " + amountPerRoyal.toShortString()));
            }
        }

        if(nation == attacker) {
            getWarNotes().attackerAidSent = getWarNotes().attackerAidSent.add(amountPerRoyal);
        } else {
            getWarNotes().defenderAidSent = getWarNotes().attackerAidSent.add(amountPerRoyal);
        }
    }

    private void checkAndAddWarTax(double adr, double ddr) {
        if(ddr > 0.5 && !aWarTax){
            attacker.setWarTaxToWorkWallets();
            aWarTax = true;
            addWarNote(attacker + " war taxation started.");
        }
        if(adr > 0.5 && !dWarTax){
            defender.setWarTaxToWorkWallets();
            dWarTax = true;
            addWarNote(defender + " war taxation started.");
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
    public void selectWinner(String attacker, String defender, String message){
        addWarNote(message);
        currentPhase = ENDING_SOON;
    }

    private void endMethod(Nation winner) {

        triggerPopUp(winner);

        String attacker;
        String defender;

        if((this.attacker == winner)) {
            generateWarReport(this.attacker);
            attacker = "Winner";
            defender = "Loser";
        }else{
            generateWarReport(this.defender);
            attacker = "Loser";
            defender = "Winner";
        }

        currentPhase = ENDED;

        this.attacker.endWar(this.defender, attacker, warNotes);
        this.defender.endWar(this.attacker, defender, warNotes);

        WarManager.unsubscribe(this);

        WarPlanningManager.filterNations(); // War Planning manager should be updated here

        Model.calculatePlayerTerritory(); // Calculate Player Territory




    }

    private void triggerPopUp(Nation winner) {
        if(this.attacker.isPlayerNation() || this.defender.isPlayerNation()) {

            boolean playerWon;
            playerWon = winner.isPlayerNation();

            Nation w = attacker == winner ? attacker : defender;
            Nation l = attacker == winner ? defender : attacker;

            triggerWarEnding(w, l, warNotes.warName, playerWon); // send popup
        }
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

        addWarNote(String.format("%s sent %d civilian militaries to war. Total power: %d",
                attacker,
                attackerMilitariesInPlay.size(),
                Nation.countTotalMilitaryStrength(attackerMilitariesInPlay)));

        addWarNote(String.format("%s sent %d civilian militaries to war. Total power: %d",
                defender,
                defenderMilitariesInPlay.size(),
                Nation.countTotalMilitaryStrength(defenderMilitariesInPlay)));

        addWarNote(String.format("Battles will start in %d days", prepareTime));


    }
    public void startPhase1() {
        if (currentPhase == PREPARING) {
            setCurrentPhase(PHASE1);

            boolean attackerEmpty = attackerMilitariesInPlay.isEmpty();
            boolean defenderEmpty = defenderMilitariesInPlay.isEmpty();

            if (attackerEmpty && defenderEmpty) {
                startPhase2("Both " + attacker + " and " + defender + " have no civilian armies. Phase 2 started.");
                return;
            }

            if (attackerEmpty) {
                startPhase2(attacker + " has no civilian armies. Phase 2 started.");
                return;
            }

            if (defenderEmpty) {
                startPhase2(defender + " has no civilian armies. Phase 2 started.");
            }

            addWarNote("Phase 1: Battles are now starting.");
        }
    }



    public void startPhase2(String message) {
        if(currentPhase == PHASE1) {

            addWarNote(message);

            int aReduced = reduceMilitaries(attackerMilitariesInPlay);
            int dReduced = reduceMilitaries(defenderMilitariesInPlay);

            if(aReduced > 0){
                addWarNote(attacker + ": " + aReduced + " civilian militaries retreated.");
            }
            if(dReduced > 0){
                addWarNote(defender + ": " + dReduced + " civilian militaries retreated.");
            }


            List<Military> a = attacker.getMilitariesOwnedByCommanders();
            List<Military> d = defender.getMilitariesOwnedByCommanders();

            attackerMilitariesInPlay.addAll(a);
            defenderMilitariesInPlay.addAll(d);

            addWarNote(String.format("%s sent %d commanders to war. Power: %d. Total Power: %d.",
                    attacker,
                    a.size(),
                    Nation.countTotalMilitaryStrength(a),
                    Nation.countTotalMilitaryStrength(getAllArmiesStillInPlay("Attacker"))));

            addWarNote(String.format("%s sent %d commanders to war. Power: %d. Total Power: %d.",
                    defender,
                    d.size(),
                    Nation.countTotalMilitaryStrength(d),
                    Nation.countTotalMilitaryStrength(getAllArmiesStillInPlay("Defender"))));

            setCurrentPhase(PHASE2);

        }
    }

    private Set<Military> getAllArmiesStillInPlay(String side){
        Set<Military> set = new HashSet<>();
        if(Objects.equals(side, "Attacker")){
            set.addAll(attackerMilitariesInPlay);
            set.addAll(attackerMilitariesInBattle);
            set.addAll(attackerRoyals);
        }
        if(Objects.equals(side, "Defender")){
            set.addAll(defenderMilitariesInPlay);
            set.addAll(defenderMilitariesInBattle);
            set.addAll(defenderRoyals);
        }
        return set;
    }



    private int reduceMilitaries(HashSet<Military> set) {
        int amount = 0;
        if (set.size() > 25) {
            Iterator<Military> iterator = set.iterator();
            while (iterator.hasNext() && set.size() > 20) {

                if(iterator instanceof Military m){ // don't retreat player.
                    if(m.getOwner().isPlayer()){
                        continue;
                    }
                    m.getOwner().removeState(State.ACTIVE_COMBAT);
                }

                iterator.next();
                iterator.remove();
                amount++;
            }
        }
        return amount;
    }

    public void startPhase3(String message, Nation loser) {
        if(currentPhase == PHASE2) {
            addWarNote(message);

            TransferPackage amount = null;
            if(loser == defender){
                amount = defender.getWallet().getBalance().divide(2);
                attacker.getWallet().deposit(defender.getWallet(), amount);
            }else if (loser == attacker){
                amount = attacker.getWallet().getBalance().divide(2);
                defender.getWallet().deposit(attacker.getWallet(), amount);
            }

            if (loser != null) {
                addWarNote(loser + " paid: " + amount.toShortString() + " for losing phase 2.");
            }


            royalMatchmakingStartingDay = days + 30;

            List<Military> a = attacker.getRoyalMilitaries();
            List<Military> d = defender.getRoyalMilitaries();

            attackerRoyals.addAll(a);
            defenderRoyals.addAll(d);

            addWarNote(String.format("%s sent %d Royals to war. Royals power: %d.",
                    attacker,
                    a.size(),
                    Nation.countTotalMilitaryStrength(a)));

            addWarNote(String.format("%s sent %d Royals to war. Royals power: %d.",
                    defender,
                    d.size(),
                    Nation.countTotalMilitaryStrength(d)));

            setCurrentPhase(PHASE3);

            addWarNote("Royal battles will begin in day: " + royalMatchmakingStartingDay + ".");
        }

    }

    private void royalsMatchMaking(int day){
        if(currentPhase != PHASE3) return;
        if(days < royalMatchmakingStartingDay) return;
        if(days == royalMatchmakingStartingDay){
            addWarNote("Royal battles have started.");
        }

        HashSet<Military> aar = testRoyalMilitary(attackerRoyals, attackerDefeatedMilitaries, this::addIntoAttackerList);
        HashSet<Military> adr = testRoyalMilitary(defenderRoyals, defenderDefeatedMilitaries, this::addIntoDefenderList);

        // if either army is empty, add into full in play list
        if(aar.isEmpty() || adr.isEmpty()){
            for(Military m : aar) {
                addIntoCorrectList(m, SetName.IN_PLAY);
            }
            for(Military m : adr) {
                addIntoCorrectList(m, SetName.IN_PLAY);
            }
        }

        if(day == 23) {
            matchMaking(aar, adr, day);
        }else{
            matchMaking(adr, aar, day);
        }
    }

    private HashSet<Military> testRoyalMilitary(HashSet<Military> set, HashSet<Military> defeated, BiConsumer<Military, Set<Military>> addMethod) {
        Iterator<Military> iterator = set.iterator();
        HashSet<Military> availableRoyals = new HashSet<>();
        while (iterator.hasNext()) {
            Military m = iterator.next();
            if (m.getState() == Army.ArmyState.DEFEATED) {
                iterator.remove();
                addMethod.accept(m, defeated);
                if(m.getOwner().getCharacter() instanceof King king){
                    addWarNote("King of " + Settings.removeUiNameAddition(king.getRole().getNation().toString()) + " has fallen.");
                    addWarNote(king.getRole().getNation().toString() + " has " + set.size() + " Royal Armies left.");
                }
                continue;
            }
            if (m.getState() == Army.ArmyState.ATTACKING || m.getState() == Army.ArmyState.DEFENDING ) {
                continue;
            }
            availableRoyals.add(m);
        }
        return availableRoyals;
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

    public WarNotes getWarNotes() {
        return warNotes;
    }
    public int getDays() {
        return days;
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

    private void addWarNote(String note) {
        if (!note.endsWith(".")) {
            note += ".";
        }
        warNotes.warLog.add("Day: " + days + ": " + note);
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

    public int getTotalPower(Nation n) {
        if(n == attacker){
            return attackerTotalPower;
        }else {
            return defenderTotalPower;
        }
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

        TransferPackage attackerAidSent = new TransferPackage(0,0,0);
        TransferPackage defenderAidSent = new TransferPackage(0,0,0);


        public ArrayList<String> getWarLog() {
            return warLog;
        }

        public Nation getAttacker() {
            return attacker;
        }

        public Nation getDefender() {
            return defender;
        }

        public Nation getWinner() {
            return winner;
        }

        public int getTotalBattles() {
            return totalBattles;
        }

        public int getLastedForDays() {
            return lastedForDays;
        }

        public Person getAttackingKing() {
            return attackingKing;
        }

        public Person getDefendingKing() {
            return defendingKing;
        }

        public Person getDeadliestAttacker() {
            return deadliestAttacker;
        }

        public Person getDeadliestDefender() {
            return deadliestDefender;
        }

        public String getWarName() {
            return warName;
        }



    }

}
