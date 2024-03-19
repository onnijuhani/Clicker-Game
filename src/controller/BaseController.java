package controller;

import model.Model;

public abstract class BaseController {
    protected Model model;
    protected MainController main;
    public BaseController() {
    }
    public void setModel(Model model) {
        this.model = model;
    }
    public Model getModel() {
        return model;
    }
    public abstract void initialize();

    protected void setMain(MainController mainController) {
        this.main = mainController;
    }
}
