package controller;

import model.Model;

public abstract class BaseController {
    protected Model model;
    public BaseController() {
    }
    public void setModel(Model model) {
        this.model = model;
    }
    public Model getModel() {
        return model;
    }
    public abstract void initialize();
}
