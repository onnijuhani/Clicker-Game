import java.util.ArrayList;

public class Model {
    private final CreateWorld world;
    private CurrentPosition currentPosition;
    private CurrentView currentView;

    public Model(){
        this.world = new CreateWorld();
        this.currentPosition = new CurrentPosition();
        currentPosition.updateCurrentQuarter(world.getSpawnQuarter());
        this.currentView = new CurrentView();
        currentView.setCurrentView(world.getSpawnQuarter());
    }

    public CreateWorld getWorld() {
        return world;
    }

    public CurrentPosition accessCurrentPosition() {
        return currentPosition;
    }

    public CurrentView accessCurrentView() {
        return currentView;
    }
}

class CreateWorld {
    private final Size size;
    private World world;
    private Quarter spawnQuarter;

    public CreateWorld(){
        this.size = Size.MEDIUM;
        this.world = new World("Medium World", this.size);
        this.spawnQuarter = spawn();
    }
    private Quarter spawn(){
        Quarter spawn = this.world.getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0).getContents().get(0);
        return spawn;
    }
    public Quarter getSpawnQuarter() {
        return spawnQuarter;
    }
}

class CurrentView {
    private Area currentView;
    public Area getCurrentView() {
        return currentView;
    }
    public void setCurrentView(Area currentView) {
        this.currentView = currentView;
    }
    public CurrentView(){
    }
    public void updateCurrentView(Area area) {
        this.currentView = area;
    }
    public Area getHigher(){
        return this.currentView.getHigher();
    }
    public ArrayList<Area> getContents() {
        return this.currentView.getContents();
    }
}

class CurrentPosition {
    private City currentCity;
    private Province currentProvince;
    private Nation currentNation;
    private Continent currentContinent;
    private World currentWorld;

    public CurrentPosition(){
    }
    public Quarter getCurrentQuarter() {
        return currentQuarter;
    }
    public void updateCurrentQuarter(Quarter newQuarter) {
        this.currentQuarter = newQuarter;
        updateOtherAreas(newQuarter);
    }
    public ArrayList<Quarter> getAvailableQuarters(){
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
    private void updateOtherAreas(Quarter quarter){
        this.currentCity = (City) quarter.getHigher();
        this.currentProvince = (Province) currentCity.getHigher();
        this.currentNation = (Nation) currentProvince.getHigher();
        this.currentContinent = (Continent) currentNation.getHigher();
        this.currentWorld = (World) currentContinent.getHigher();
    }

}
