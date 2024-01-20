package model.worldCreation;

import model.buildings.PropertyTracker;

import java.util.ArrayList;

public abstract class Area implements Details {
    protected String name;

    public PropertyTracker propertyTracker;

    public abstract ArrayList getContents();

    public abstract String getName();

    public abstract Area getHigher();

    @Override
    public String toString() {
        return getName();
    }

}
