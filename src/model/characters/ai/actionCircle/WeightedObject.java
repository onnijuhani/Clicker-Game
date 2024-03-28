package model.characters.ai.actionCircle;

import model.characters.Trait;
import model.characters.ai.actions.NPCAction;

import java.util.Map;
import java.util.Random;

public class WeightedObject implements NPCAction {
    protected int weight;
    protected int importance;
    protected Map<Trait, Integer> profile;
    boolean DB = true; //TODO remove the debugger eventually


    public WeightedObject(int weight, Map<Trait, Integer> profile) {
        this.weight = weight;
        this.importance = weight;
        this.profile = profile;
    }
    public WeightedObject() {
        this.weight = 1;
        this.importance = 1;
    }
    public WeightedObject(int weight, int importance) {
        this.weight = weight;
        this.importance = importance;
    }

    public int getWeight(){
        return weight;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }
    public void resetWeight(){
        this.weight = 1;
    }

    public void execute()  {



        if(DB) {System.out.println("execute 1");}
        Trait trait = pickTrait(profile);

        if(DB) {System.out.println("execute 2");}
        if(trait == null){
            defaultSkip();
            if(DB) {System.out.println("execute 3");}
            return;
        }

        if(DB) {System.out.println("execute 4");}
        switch (trait) {
            case Ambitious:
                if(DB) {System.out.println("ambitious 1");}
                ambitiousAction();
                if(DB) {System.out.println("ambitious 2");}
            case Unambitious:
                if(DB) {System.out.println("unambitious 1");}
                unambitiousAction();
                if(DB) {System.out.println("unambitious 2");}
            case Slaver:
                if(DB) {System.out.println("slaver 1");}
                slaverAction();
                if(DB) {System.out.println("slaver 2");}
            case Liberal:
                if(DB) {System.out.println("liberal 1");}
                liberalAction();
                if(DB) {System.out.println("liberal 2");}
            case Aggressive:

                aggressiveAction();

            case Passive:
                passiveAction();
            case Loyal:
                loyalAction();
            case Disloyal:
                disloyalAction();
            case Attacker:
                attackerAction();
            case Defender:
                defenderAction();
            default:
                defaultSkip();
        }
    }
    @Override
    public void defaultAction()  {
    }
    @Override
    public void defaultSkip() {
    }

    @Override
    public void ambitiousAction() {
        defaultAction();
    }

    @Override
    public void unambitiousAction() {
        defaultAction();
    }

    @Override
    public void slaverAction() {
        defaultAction();
    }

    @Override
    public void liberalAction() {
        defaultAction();
    }

    @Override
    public void aggressiveAction() {
        defaultAction();
    }

    @Override
    public void passiveAction() {
        defaultAction();
    }

    @Override
    public void loyalAction() {
        defaultAction();
    }

    @Override
    public void disloyalAction() {
        defaultAction();
    }

    @Override
    public void attackerAction() {
        defaultAction();
    }

    @Override
    public void defenderAction() {
        defaultAction();
    }

    public void incrementWeight(int increment){
        this.weight += increment*importance;
    }
    public void setImportance(int importance) {
        this.importance = importance;
    }

    @Override
    public int compareTo(NPCAction o) {
        return 0;
    }


    /**
     *  Used to pick random trait by the probability
     * @param profile personality/trait profile
     * @return returns random trait
     */
    public Trait pickTrait(Map<Trait, Integer> profile) {


        if(DB) {System.out.println("traitPick 1");}
        System.out.println(profile.values());
        int totalSum = profile.values().stream().mapToInt(Integer::intValue).sum();
        if(DB) {System.out.println("traitPick 2");}
        int randomNum = new Random().nextInt(totalSum);
        if(DB) {System.out.println("traitPick 3");}
        int cumulativeProbability = 0;
        if(DB) {System.out.println("traitPick 4");}

        // 5% chance of not picking anything, that's default Skip
        if (randomNum < totalSum * 0.05) {
            if(DB) {System.out.println("traitPick 5");}
            return null;

        }
        if(DB) {System.out.println("traitPick 6");}
        for (Map.Entry<Trait, Integer> entry : profile.entrySet()) {
            cumulativeProbability += entry.getValue();
            if(DB) {System.out.println("traitPick 7");}
            if (randomNum < cumulativeProbability) {
                if(DB) {System.out.println("traitPick 8");}
                return entry.getKey();
            }
        }
        if(DB) {System.out.println("traitPick 9");}

        // This should never happen if the probabilities sum up to 100
        throw new IllegalStateException("Unable to pick a trait.");
    }
}
