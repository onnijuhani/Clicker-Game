package model.characters.decisions;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class TreeNode {
    abstract void execute(Character npc);
}

class DecisionNode extends TreeNode {
    private Predicate<Character> condition;
    private TreeNode trueBranch;
    private TreeNode falseBranch;

    public DecisionNode(Predicate<Character> condition, TreeNode trueBranch, TreeNode falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    @Override
    void execute(Character npc) {
        if (condition.test(npc)) {
            trueBranch.execute(npc);
        } else {
            falseBranch.execute(npc);
        }
    }
}

class ActionNode extends TreeNode {
    private Consumer<Character> action;

    public ActionNode(Consumer<Character> action) {
        this.action = action;
    }

    @Override
    void execute(Character npc) {
        action.accept(npc);
    }
}
