package model.map;

import model.worldCreation.*;

import java.util.ArrayList;

public class CurrentPosition {
    private City currentCity;
    private Province currentProvince;
    private Nation currentNation;
    private Continent currentContinent;
    private World currentWorld;

    public CurrentPosition() {
    }

    public Quarter getCurrentQuarter() {
        return currentQuarter;
    }

    public void updateCurrentQuarter(Quarter newQuarter) {
        this.currentQuarter = newQuarter;
        updateOtherAreas(newQuarter);
    }

    public ArrayList<Quarter> getAvailableQuarters() {
        return this.currentCity.getContents();
    }

    private Quarter currentQuarter;

    public City getCurrentCity() {
        return currentCity;
    }

    public Province getCurrentProvince() {
        return currentProvince;
    }

    public Nation getCurrentNation() {
        return currentNation;
    }

    public Continent getCurrentContinent() {
        return currentContinent;
    }

    public World getCurrentWorld() {
        return currentWorld;
    }

    private void updateOtherAreas(Quarter quarter) {
        this.currentCity = (City) quarter.getHigher();
        this.currentProvince = (Province) currentCity.getHigher();
        this.currentNation = (Nation) currentProvince.getHigher();
        this.currentContinent = (Continent) currentNation.getHigher();
        this.currentWorld = (World) currentContinent.getHigher();
    }

}
