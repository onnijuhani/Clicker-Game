package model.characters;

import model.buildings.PropertyCreation;
import model.characters.authority.Authority;
import model.resourceManagement.payments.Salary;

public class Support extends Character {
    protected Salary salary;

    public static int totalAmount;

    public Support(Authority authorityTo) {
        setAuthority(authorityTo);
        totalAmount += 1;
        createProperty();
    }

    public void createProperty() {
        setProperty(PropertyCreation.createSupportProperty(this));
        getProperty().setOwner(this.getPerson());
    }

    public Salary getSalary() {
        return salary;
    }

    public void setSalary(Salary salary) {
        this.salary = salary;
    }
}
