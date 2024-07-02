package controller;

import javafx.fxml.FXML;
import model.Model;

public abstract class BaseController implements Updatable {
    protected Model model;
    protected MainController main;
    public BaseController() {
    }

    @Override
    public void update() {}

    @FXML
    public void initialize() {
        ControllerManager.registerController(this);
    }

    public void setModel(Model model) {
        this.model = model;
    }
    public Model getModel() {
        return model;
    }


    protected void setMain(MainController mainController) {
        this.main = mainController;
    }
}
