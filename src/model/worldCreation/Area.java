package model.worldCreation;

import model.buildings.PropertyTracker;

import java.util.List;

public abstract class Area implements Details, HasContents {
    protected String name;

    public PropertyTracker propertyTracker;

    public abstract List getContents();

    public abstract String getName();


    public abstract Area getHigher();

    @Override
    public String toString() {
        return getName();
    }



}
