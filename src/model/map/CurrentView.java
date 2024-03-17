package model.map;

import model.worldCreation.Area;

import java.util.List;

public class CurrentView {
    private Area currentView;

    public CurrentView() {
    }
    public Area getCurrentView() {
        return currentView;
    }

    public void setCurrentView(Area currentView) {
        this.currentView = currentView;
    }

    public Area getHigher() {
        return this.currentView.getHigher();
    }

    public List<Area> getContents() {
        return this.currentView.getContents();
    }
}
