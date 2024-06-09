package model.worldCreation;

import model.buildings.PropertyTracker;
import model.characters.Person;
import model.war.AreaStateManager;

import java.util.List;
@SuppressWarnings("CallToPrintStackTrace")
public abstract class Area implements Details, HasContents {



    protected final AreaStateManager areaStateManager = new AreaStateManager(null);

    protected AreaState areaState;

    protected Nation claimedBy;

    protected String name;
    public PropertyTracker propertyTracker;
    public abstract List getContents();
    public abstract String getName();
    public void setName(String name) {
        this.name = name;
    }
    public abstract Area getHigher();
    @Override
    public String toString() {
        return getName();
    }
    public String toAreaString() {
        return this.getClass().getSimpleName() +" - "+ getName();
    }

    public void addHomeToName() {
        this.name += " (Home)";
    }

    public AreaStateManager getAreaStateManager() {
        return areaStateManager;
    }

    protected Person getHighestAuthority() {
        try {
            Area area = this;

            if (area instanceof World world) {
                return world.getAreaStateManager().getClaimingNation().getAuthorityHere().getCharacterInThisPosition().getPerson();

            } else if (area instanceof Continent continent) {
                return continent.getAreaStateManager().getClaimingNation().getAuthorityHere().getCharacterInThisPosition().getPerson();

            } else if (area instanceof ControlledArea controlledArea) {
                controlledArea.onCitizenUpdate();
                return controlledArea.getAuthorityHere().getCharacterInThisPosition().getPerson();
            } else {
                throw new IllegalArgumentException("Unknown area type: " + area.getClass().getName());
            }
        } catch (RuntimeException e) {
            e.printStackTrace();throw new RuntimeException(e);
        }

    }
}
