package model.buildings;

import java.util.ArrayList;
import java.util.List;

public class PropertyTracker {
    private List<Property> properties;

    public PropertyTracker() {
        this.properties = new ArrayList<>();
    }

    public void addProperty(Property property) {
        properties.add(property);
    }

    public List<Property> getProperties() {
        return properties;
    }
}
