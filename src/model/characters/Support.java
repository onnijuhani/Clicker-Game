package model.characters;

import model.buildings.PropertyCreation;
import model.characters.authority.Authority;
import model.resourceManagement.payments.Salary;

public class Support extends Character {
    protected Salary salary;
    protected Authority authorityTo;
    public static int totalAmount;

    public Support(Authority authorityTo) {
        this.authorityTo = authorityTo;
        this.totalAmount += 1;
        createProperty();
    }

    public void createProperty() {
        this.property = PropertyCreation.createSupportProperty(this);
        this.property.setOwner(this);
    }

    public Salary getSalary() {
        return salary;
    }

    public void setSalary(Salary salary) {
        this.salary = salary;
    }
}
