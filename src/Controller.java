import java.util.ArrayList;

public class Controller {
    public Model getModel() {
        return model;
    }
    private Model model;
    private UI view;

    public Controller(UI view) {
        this.view = view;
        this.model = new Model();
    }

    public ArrayList<Quarter> getQuarterList() {
        return model.accessCurrentPosition().getAvailableQuarters();
    }

}




