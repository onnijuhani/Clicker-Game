package model.characters.ai.actionCircle;

import model.Settings;
import model.characters.Trait;
import model.characters.ai.actions.NPCAction;

import java.util.Map;
import java.util.Random;

public class WeightedObject implements NPCAction {
    protected int weight;
    protected int importance;
    protected Map<Trait, Integer> profile;



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


    /**
     * if there is only default action, overwriting this should improve performance slightly
     */
    public void execute()  {

        if(Settings.DB) {System.out.println("execute 1");}
        Trait trait = pickTrait(profile);

        if(Settings.DB) {System.out.println("execute 2");}
        if(trait == null){
            defaultSkip();
            if(Settings.DB) {System.out.println("execute 3");}
            return;
        }

        if(Settings.DB) {System.out.println("execute 4");}
        switch (trait) {
            case Ambitious:
                if(Settings.DB) {System.out.println("ambitious 1" + this.getClass().getSimpleName());}
                ambitiousAction();
                if(Settings.DB) {System.out.println("ambitious 2" + this.getClass().getSimpleName());}
                break;
            case Unambitious:
                if(Settings.DB) {System.out.println("unambitious 1" + this.getClass().getSimpleName());}
                unambitiousAction();
                if(Settings.DB) {System.out.println("unambitious 2" + this.getClass().getSimpleName());}
                break;
            case Slaver:
                if(Settings.DB) {System.out.println("slaver 1" + this.getClass().getSimpleName());}
                slaverAction();
                if(Settings.DB) {System.out.println("slaver 2" + this.getClass().getSimpleName());}
                break;
            case Liberal:
                if(Settings.DB) {System.out.println("liberal 1" + this.getClass().getSimpleName());}
                liberalAction();
                if(Settings.DB) {System.out.println("liberal 2" + this.getClass().getSimpleName());}
                break;
            case Aggressive:
                if(Settings.DB) {System.out.println("aggressivr1!!" + this.getClass().getSimpleName());}
                aggressiveAction();
                if(Settings.DB) {System.out.println("aggressiv2" + this.getClass().getSimpleName());}
                break;
            case Passive:
                if(Settings.DB) {System.out.println("passive " + this.getClass().getSimpleName());}
                passiveAction();
                break;
            case Loyal:
                if(Settings.DB) {System.out.println("loyal " + this.getClass().getSimpleName());}
                loyalAction();
                break;
            case Disloyal:
                if(Settings.DB) {System.out.println("disloyal " + this.getClass().getSimpleName());}
                disloyalAction();
                break;
            case Attacker:
                if(Settings.DB) {System.out.println("attacker " + this.getClass().getSimpleName());}
                attackerAction();
                break;
            case Defender:
                if(Settings.DB) {System.out.println("defender" + this.getClass().getSimpleName());}
                defenderAction();
                break;
            default:
                if(Settings.DB) {System.out.println("default" + this.getClass().getSimpleName());}
                defaultSkip();
                break;
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


        if(Settings.DB) {System.out.println("traitPick 1");}
        int totalSum = profile.values().stream().mapToInt(Integer::intValue).sum();
        if(Settings.DB) {System.out.println("traitPick 2");}
        int randomNum = new Random().nextInt(totalSum);
        if(Settings.DB) {System.out.println("traitPick 3");}
        int cumulativeProbability = 0;
        if(Settings.DB) {System.out.println("traitPick 4");}

        // 5% chance of not picking anything, that's default Skip
        if (randomNum < totalSum * 0.05) {
            if(Settings.DB) {System.out.println("traitPick 5");}
            return null;

        }
        if(Settings.DB) {System.out.println("traitPick 6");}
        for (Map.Entry<Trait, Integer> entry : profile.entrySet()) {
            cumulativeProbability += entry.getValue();
            if(Settings.DB) {System.out.println("traitPick 7");}
            if (randomNum < cumulativeProbability) {
                if(Settings.DB) {System.out.println("traitPick 8");}
                return entry.getKey();
            }
        }
        if(Settings.DB) {System.out.println("traitPick 9");}

        // This should never happen if the probabilities sum up to 100
        throw new IllegalStateException("Unable to pick a trait.");
    }
}
