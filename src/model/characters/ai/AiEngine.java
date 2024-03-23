package model.characters.ai;

import model.characters.Person;
import model.characters.ai.actions.*;

public class AiEngine {

    private CombatActions combatActions;
    private PowerActions powerActions;
    private UtilityActions utilityActions;
    private WarActions warActions;
    private ManagementActions managementActions;


    public AiEngine(Person person) {
        this.combatActions = new CombatActions(person);
        this.powerActions = new PowerActions(person);
        this.utilityActions = new UtilityActions(person);
        this.warActions = new WarActions(person);
        this.managementActions = new ManagementActions(person);
    }
}
