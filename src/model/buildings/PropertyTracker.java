package model.buildings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PropertyTracker {
    private final HashSet<Property> properties;

    public PropertyTracker() {
        this.properties = new HashSet<>();
    }

    public void addProperty(Property property) {
        properties.add(property);
    }
    public void removeProperty(Property property) {
        properties.remove(property);
    }

    public List<Property> getProperties() {
        ArrayList<Property> properties = new ArrayList<>(this.properties);
        return properties;
    }
}
